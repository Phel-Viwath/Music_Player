package com.viwath.music_player

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
    }
}