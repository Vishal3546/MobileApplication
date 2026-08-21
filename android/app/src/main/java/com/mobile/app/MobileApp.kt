package com.mobile.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MobileApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
