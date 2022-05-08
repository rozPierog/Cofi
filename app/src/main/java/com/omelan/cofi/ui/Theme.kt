package com.omelan.cofi.ui

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.insets.ProvideWindowInsets
import com.kieronquinn.monetcompat.compose.darkMonetCompatScheme
import com.kieronquinn.monetcompat.compose.lightMonetCompatScheme
import com.kieronquinn.monetcompat.core.MonetCompat

@Composable
fun MaterialTheme.createTextSelectionColors() = TextSelectionColors(
    handleColor = this.colorScheme.secondary,
    backgroundColor = this.colorScheme.secondary.copy(alpha = 0.4f)
)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun getMaterialYouPallets(context: Context, darkMode: Boolean) =
    if (darkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

@Composable
fun CofiTheme(
    monet: MonetCompat? = null,
    isDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getMaterialYouPallets(context, isDarkMode)
    } else {
        if (monet != null) {
            if (!isDarkMode) monet.lightMonetCompatScheme() else monet.darkMonetCompatScheme()
        } else {
            if (isDarkMode) darkColorScheme() else lightColorScheme()
        }
    }
    MaterialTheme(colorScheme = colors) {
        ProvideWindowInsets(windowInsetsAnimationsEnabled = false, content = content)
    }
}