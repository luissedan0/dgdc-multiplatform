package com.luissedan0.demetergarterdistanceclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Ensure modern TLS support on older devices
        try {
            ProviderInstaller.installIfNeeded(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        FirebaseApp.initializeApp(this)

        FirebaseCrashlytics.getInstance().apply {
            isCrashlyticsCollectionEnabled = true
            setCustomKey("platform", "android")
            sendUnsentReports()
            log("Crashlytics initialized from Application.onCreate")
        }


        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
