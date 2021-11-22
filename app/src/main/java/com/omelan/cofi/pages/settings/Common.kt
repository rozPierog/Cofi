package com.omelan.cofi.pages.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.settingsItemModifier(onClick: () -> Unit, enabled: Boolean = true) = this
    .height(56.dp)
    .clickable(
        onClick = onClick,
        role = Role.Button,
        interactionSource = remember { MutableInteractionSource() },
        enabled = enabled,
        indication = rememberRipple(bounded = true),
    )