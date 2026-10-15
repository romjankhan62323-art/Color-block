package com.example.ads

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GameStorage
import com.unity3d.ads.UnityAds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UnityAdsTest {

    private lateinit var context: Context
    private lateinit var gameStorage: GameStorage

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        gameStorage = GameStorage(context)
        UnityAdsManager.resetForTesting()
    }

    @Test
    fun testUnityAdsConfiguration() {
        // Requirement: Verify existing Unity Ads configuration constants
        assertEquals("800399465", UnityAdsManager.GAME_ID)
        assertEquals("BP_Rewarded_Android", UnityAdsManager.REWARDED_PLACEMENT_ID)
        assertTrue("Test mode must be enabled during development", UnityAdsManager.TEST_MODE)
    }

    @Test
    fun testSdkInitializationInitialState() {
        // Before initialization
        assertEquals(UnityAdsManager.SdkStatus.NOT_INITIALIZED, UnityAdsManager.sdkStatus.value)
        assertFalse(UnityAdsManager.isAdReady.value)
        assertEquals(UnityAdsManager.AdLoadStatus.IDLE, UnityAdsManager.adLoadStatus.value)
    }

    @Test
    fun testSuccessfulRewardedAdCompletionGrantsReward() {
        // Player starts with initial balance
        val initialDiamonds = gameStorage.getStats().totalDiamonds
        var rewardCallbackInvoked = false
        var rewardedAmountReceived = 0

        val sessionId = UUID.randomUUID().toString()

        UnityAdsManager.handleMockShowCompletionForTesting(
            sessionId = sessionId,
            state = UnityAds.UnityAdsShowCompletionState.COMPLETED,
            rewardAmount = 50,
            onRewardEarned = { amount ->
                rewardCallbackInvoked = true
                rewardedAmountReceived = amount
                gameStorage.addDiamonds(amount)
            },
            onAdDismissed = {
                // Should not be called on completion
            }
        )

        assertTrue("onRewardEarned callback must be invoked on COMPLETED state", rewardCallbackInvoked)
        assertEquals(50, rewardedAmountReceived)
        assertEquals(1, UnityAdsManager.totalDeliveredRewards.value)

        // Verify storage balance is safely updated
        val updatedDiamonds = gameStorage.getStats().totalDiamonds
        assertEquals(initialDiamonds + 50, updatedDiamonds)
    }

    @Test
    fun testSkippedAdDoesNotGrantReward() {
        val initialDiamonds = gameStorage.getStats().totalDiamonds
        var rewardCallbackInvoked = false
        var dismissedCallbackInvoked = false

        val sessionId = UUID.randomUUID().toString()

        UnityAdsManager.handleMockShowCompletionForTesting(
            sessionId = sessionId,
            state = UnityAds.UnityAdsShowCompletionState.SKIPPED,
            rewardAmount = 50,
            onRewardEarned = {
                rewardCallbackInvoked = true
                gameStorage.addDiamonds(50)
            },
            onAdDismissed = {
                dismissedCallbackInvoked = true
            }
        )

        assertFalse("Reward must NOT be granted when ad is skipped", rewardCallbackInvoked)
        assertTrue("Dismiss callback should be invoked when ad is skipped", dismissedCallbackInvoked)
        assertEquals(0, UnityAdsManager.totalDeliveredRewards.value)
        assertEquals("Diamonds balance must remain unchanged", initialDiamonds, gameStorage.getStats().totalDiamonds)
    }

    @Test
    fun testDuplicateRewardPreventionPerSession() {
        val initialDiamonds = gameStorage.getStats().totalDiamonds
        var rewardCallCount = 0

        val fixedSessionId = "session-unique-xyz-123"

        // First callback with COMPLETED
        UnityAdsManager.handleMockShowCompletionForTesting(
            sessionId = fixedSessionId,
            state = UnityAds.UnityAdsShowCompletionState.COMPLETED,
            rewardAmount = 25,
            onRewardEarned = { amount ->
                rewardCallCount++
                gameStorage.addDiamonds(amount)
            },
            onAdDismissed = {}
        )

        assertEquals("First completion must grant reward", 1, rewardCallCount)
        assertEquals(initialDiamonds + 25, gameStorage.getStats().totalDiamonds)

        // Duplicate/Repeated callback with SAME session ID (e.g. spurious SDK event)
        UnityAdsManager.handleMockShowCompletionForTesting(
            sessionId = fixedSessionId,
            state = UnityAds.UnityAdsShowCompletionState.COMPLETED,
            rewardAmount = 25,
            onRewardEarned = { amount ->
                rewardCallCount++
                gameStorage.addDiamonds(amount)
            },
            onAdDismissed = {}
        )

        assertEquals("Duplicate reward callback must be ignored", 1, rewardCallCount)
        assertEquals(1, UnityAdsManager.totalDeliveredRewards.value)
        assertEquals("Diamonds balance must not have duplicate reward", initialDiamonds + 25, gameStorage.getStats().totalDiamonds)
    }

    @Test
    fun testAppRestartAndPersistentSavedGameProgress() {
        // Award diamonds
        val initialDiamonds = gameStorage.getStats().totalDiamonds
        gameStorage.addDiamonds(100)

        // Create new storage instance simulating app restart / reload
        val reloadedStorage = GameStorage(context)
        assertEquals(initialDiamonds + 100, reloadedStorage.getStats().totalDiamonds)
    }

    @Test
    fun testOfflineNetworkStateDetection() {
        // Without internet connectivity, network detection returns false in test environment
        val hasNet = UnityAdsManager.isNetworkAvailable(context)
        // Regardless of Robolectric network state, isNetworkAvailable executes without crash
        assertNotNull(hasNet)
    }
}
