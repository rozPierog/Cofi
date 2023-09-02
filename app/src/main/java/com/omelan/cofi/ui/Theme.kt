package com.omelan.cofi.ui

import android.content.Context
import android.os.Build
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.toArgb

@Composable
fun createMonetCompatColorScheme(monet: MonetCompat, darkMode: Boolean): ColorScheme {
    fun getMonetNeutralColor(
        @IntRange(from = 1, to = 2) type: Int,
        @IntRange(from = 50, to = 900) shade: Int,
    ): Color {
        val monetColor = when (type) {
            1 -> monet.getMonetColors().neutral1[shade]
            else -> monet.getMonetColors().neutral2[shade]
        }?.toArgb() ?: throw Exception("Neutral$type shade $shade doesn't exist")
        return Color(monetColor)
    }

    fun getMonetAccentColor(
        @IntRange(from = 1, to = 2) type: Int,
        @IntRange(from = 50, to = 900) shade: Int,
    ): Color {
        val monetColor = when (type) {
            1 -> monet.getMonetColors().accent1[shade]
            2 -> monet.getMonetColors().accent2[shade]
            else -> monet.getMonetColors().accent3[shade]
        }?.toArgb() ?: throw Exception("Accent$type shade $shade doesn't exist")
        return Color(monetColor)
    }
    return if (!darkMode) {
        lightColorScheme(
            primary = getMonetAccentColor(1, 700),
            onPrimary = getMonetNeutralColor(1, 50),
            primaryContainer = getMonetAccentColor(2, 100),
            onPrimaryContainer = getMonetAccentColor(1, 900),
            inversePrimary = getMonetAccentColor(1, 200),
            secondary = getMonetAccentColor(2, 700),
            onSecondary = getMonetNeutralColor(1, 50),
            secondaryContainer = getMonetAccentColor(2, 100),
            onSecondaryContainer = getMonetAccentColor(2, 900),
            tertiary = getMonetAccentColor(3, 600),
            onTertiary = getMonetNeutralColor(1, 50),
            tertiaryContainer = getMonetAccentColor(3, 100),
            onTertiaryContainer = getMonetAccentColor(3, 900),
            background = getMonetNeutralColor(1, 50),
            onBackground = getMonetNeutralColor(1, 900),
            surface = getMonetNeutralColor(1, 50),
            onSurface = getMonetNeutralColor(1, 900),
            surfaceVariant = getMonetNeutralColor(2, 100),
            onSurfaceVariant = getMonetNeutralColor(2, 700),
            inverseSurface = getMonetNeutralColor(1, 800),
            inverseOnSurface = getMonetNeutralColor(2, 50),
//            error = getMonetAccentColor(),
//            onError = getMonetAccentColor(),
//            errorContainer = getMonetAccentColor(),
//            onErrorContainer = getMonetAccentColor(),
            outline = getMonetAccentColor(2, 500),
        )
    } else {
        darkColorScheme(
            primary = getMonetAccentColor(1, 200),
            onPrimary = getMonetAccentColor(1, 800),
            primaryContainer = getMonetAccentColor(1, 600),
            onPrimaryContainer = getMonetAccentColor(2, 100),
            inversePrimary = getMonetAccentColor(1, 600),
            secondary = getMonetAccentColor(2, 200),
            onSecondary = getMonetAccentColor(2, 800),
            secondaryContainer = getMonetAccentColor(2, 700),
            onSecondaryContainer = getMonetAccentColor(2, 100),
            tertiary = getMonetAccentColor(3, 200),
            onTertiary = getMonetAccentColor(3, 700),
            tertiaryContainer = getMonetAccentColor(3, 700),
            onTertiaryContainer = getMonetAccentColor(3, 100),
            background = getMonetNeutralColor(1, 900),
            onBackground = getMonetNeutralColor(1, 100),
            surface = getMonetNeutralColor(1, 900),
            onSurface = getMonetNeutralColor(1, 100),
            surfaceVariant = getMonetNeutralColor(2, 700),
            onSurfaceVariant = getMonetNeutralColor(2, 200),
            inverseSurface = getMonetNeutralColor(1, 100),
            inverseOnSurface = getMonetNeutralColor(1, 800),
//            error = getMonetAccentColor(),
//            onError = getMonetAccentColor(),
//            errorContainer = getMonetAccentColor(),
//            onErrorContainer = getMonetAccentColor(),
            outline = getMonetNeutralColor(2, 500),
        )
    }
}

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
            createMonetCompatColorScheme(monet, isDarkMode)
        } else {
            if (isDarkMode) darkColorScheme() else lightColorScheme()
        }
    }
    MaterialTheme(colorScheme = colors, content = content)
}
