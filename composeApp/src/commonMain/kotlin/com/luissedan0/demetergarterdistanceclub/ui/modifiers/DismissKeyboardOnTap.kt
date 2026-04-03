package com.luissedan0.demetergarterdistanceclub.ui.modifiers

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.dismissKeyboardOnTap(
    onDismiss: () -> Unit
): Modifier = pointerInput(onDismiss) {
    awaitEachGesture {
        awaitFirstDown(pass = PointerEventPass.Final)
        val up = waitForUpOrCancellation(pass = PointerEventPass.Final)
        if (up != null) {
            onDismiss()
        }
    }
}
