package com.mobile.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.appdistribution.FirebaseAppDistribution
import com.mobile.app.core.navigation.AppNavigation
import com.mobile.app.core.ui.theme.MobileAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileAppTheme {
                AppNavigation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkForAppUpdates()
    }

    private fun checkForAppUpdates() {
        try {
            FirebaseAppDistribution.getInstance().updateIfNewReleaseAvailable()
                .addOnSuccessListener {
                    Log.d("MainActivity", "Firebase App Distribution update check completed")
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Firebase App Distribution update check failed", e)
                }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in checkForAppUpdates", e)
        }
    }
}
