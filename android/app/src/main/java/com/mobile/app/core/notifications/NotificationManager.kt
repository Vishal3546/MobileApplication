package com.mobile.app.core.notifications

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNotificationManager @Inject constructor() {

    fun getFCMToken(onTokenReceived: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onTokenReceived(task.result)
            }
        }
    }
}
