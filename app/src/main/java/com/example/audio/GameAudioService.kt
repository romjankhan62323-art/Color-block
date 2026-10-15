package com.example.audio

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameAudioService {
    var soundEnabled: Boolean = true
    var musicEnabled: Boolean = true

    private var toneGenerator: ToneGenerator? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playTap() {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        } catch (_: Exception) {}
    }

    fun playButton3D() {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 60)
        } catch (_: Exception) {}
    }

    fun playDiamondChime() {
        if (!soundEnabled) return
        scope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 60)
                delay(70)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_A, 70)
                delay(70)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_B, 90)
            } catch (_: Exception) {}
        }
    }

    fun playFanfare() {
        if (!soundEnabled) return
        scope.launch {
            try {
                val tones = listOf(
                    ToneGenerator.TONE_DTMF_1,
                    ToneGenerator.TONE_DTMF_3,
                    ToneGenerator.TONE_DTMF_5,
                    ToneGenerator.TONE_DTMF_8,
                    ToneGenerator.TONE_DTMF_D
                )
                for (t in tones) {
                    toneGenerator?.startTone(t, 80)
                    delay(80)
                }
            } catch (_: Exception) {}
        }
    }

    fun playLineClear() {
        if (!soundEnabled) return
        scope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_L, 120)
                delay(90)
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 100)
            } catch (_: Exception) {}
        }
    }

    fun playInvalidDrop() {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 120)
        } catch (_: Exception) {}
    }

    fun playCombo(comboCount: Int) {
        if (!soundEnabled) return
        scope.launch {
            try {
                val notes = listOf(ToneGenerator.TONE_DTMF_1, ToneGenerator.TONE_DTMF_4, ToneGenerator.TONE_DTMF_7, ToneGenerator.TONE_DTMF_A)
                val count = minOf(notes.size, comboCount + 1)
                for (i in 0 until count) {
                    toneGenerator?.startTone(notes[i], 70)
                    delay(70)
                }
            } catch (_: Exception) {}
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
