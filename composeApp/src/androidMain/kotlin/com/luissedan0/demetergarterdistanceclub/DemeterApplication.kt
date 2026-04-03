package com.luissedan0.demetergarterdistanceclub

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class DemeterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        FirebaseCrashlytics.getInstance().apply {
             isCrashlyticsCollectionEnabled = true
            setCustomKey("platform", "android")
            sendUnsentReports()
            log("Crashlytics initialized from Application.onCreate")
        }
    }
}
