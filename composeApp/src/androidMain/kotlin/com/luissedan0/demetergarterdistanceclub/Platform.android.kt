package com.luissedan0.demetergarterdistanceclub

import android.os.Build
import com.google.firebase.crashlytics.FirebaseCrashlytics

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun triggerTestCrash() {
    FirebaseCrashlytics.getInstance().log("Manual test crash triggered from profile screen")
    throw RuntimeException("Test Crash")
}

actual fun supportsTestCrash(): Boolean = true
