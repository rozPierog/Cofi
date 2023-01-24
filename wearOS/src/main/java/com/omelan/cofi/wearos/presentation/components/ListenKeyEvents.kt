package com.omelan.cofi.wearos.presentation.components

import android.view.KeyEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import com.omelan.cofi.wearos.presentation.LocalKeyEventHandlers

typealias KeyEventHandler = (Int, KeyEvent) -> Boolean

@Composable
fun ListenKeyEvents(handler: KeyEventHandler) {
    val handlerState = rememberUpdatedState(handler)
    val eventHandlers = LocalKeyEventHandlers.current ?: return
    DisposableEffect(handlerState) {
        val localHandler: KeyEventHandler = { keyCode, keyEvent ->
            handlerState.value(keyCode, keyEvent)
        }
        eventHandlers.add(localHandler)
        onDispose {
            eventHandlers.remove(localHandler)
        }
    }
}
