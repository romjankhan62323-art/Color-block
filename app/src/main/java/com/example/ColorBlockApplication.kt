package com.example

import android.app.Application
import android.util.Log
import com.example.ads.UnityAdsManager

/**
 * Application class for COLOR BLOCK.
 * Initializes Unity Ads early in the app lifecycle.
 */
class ColorBlockApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("ColorBlockApp", "ColorBlockApplication started. Initializing Unity Ads SDK...")
        UnityAdsManager.initialize(this)
    }
}
