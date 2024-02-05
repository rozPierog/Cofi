package com.omelan.cofi.wearos.presentation.components

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Confirmation
import com.omelan.cofi.wearos.R

@Composable
fun OpenOnPhoneConfirm(isVisible: Boolean, onTimeout: () -> Unit) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        Confirmation(
            onTimeout = onTimeout,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.common_full_open_on_phone),
                    contentDescription = "",
                )
            },
        ) {
            Text(text = stringResource(id = R.string.common_open_on_phone))
        }
    }
}
