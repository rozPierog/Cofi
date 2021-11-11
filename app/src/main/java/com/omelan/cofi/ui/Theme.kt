package com.omelan.cofi.ui

import android.content.Context
import android.os.Build
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.toArgb

@Composable
fun MaterialTheme.createTextFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    textColor = this.colorScheme.onBackground,
    focusedBorderColor = this.colorScheme.secondary,
    focusedLabelColor = this.colorScheme.onBackground,
    placeholderColor = this.colorScheme.onBackground,
    unfocusedLabelColor = this.colorScheme.onBackground,
    cursorColor = this.colorScheme.secondary,
    unfocusedBorderColor = this.colorScheme.outline,
)

@Composable
fun createMaterialYouPallets(monet: MonetCompat): Pair<ColorScheme, ColorScheme> {
    val context = LocalContext.current
    fun getMonetNeutralColor(
        @IntRange(from = 1, to = 2) type: Int,
        @IntRange(from = 50, to = 900) level: Int
    ): Color {
        val monetColor = when (type) {
            1 -> monet.getMonetColors().neutral1[level]
            else -> monet.getMonetColors().neutral2[level]
        }?.toArgb() ?: throw Exception("wrong color buddy")

        return Color(monetColor)
    }

    fun getMonetAccentColor(
        @IntRange(from = 1, to = 2) type: Int,
        @IntRange(from = 50, to = 900) level: Int
    ): Color {
        val monetColor = when (type) {
            1 -> monet.getMonetColors().accent1[level]
            2 -> monet.getMonetColors().accent2[level]
            else -> monet.getMonetColors().accent3[level]
        }?.toArgb() ?: throw Exception("wrong color buddy")
        return Color(monetColor)
    }
    return Pair(
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
        ),
        darkColorScheme(
            primary = Color(monet.getPrimaryColor(context = context, darkMode = true)),
//            primaryVariant = colorResource(id = android.R.color.system_accent1_400),
            secondary = Color(monet.getSecondaryColor(context = context, darkMode = true)),
//            secondaryVariant = colorResource(id = android.R.color.system_accent1_600),
            background = Color(monet.getBackgroundColor(context = context, darkMode = true)),
            surface = Color(
                monet.getBackgroundColorSecondary(context = context, darkMode = true)
                    ?: monet.getBackgroundColor(context = context, darkMode = true)
            ),
            onPrimary = getMonetNeutralColor(1, 100) ?: Color.Black,
            onSecondary = getMonetNeutralColor(1, 100) ?: Color.Black,
            onBackground = Color.White,
            onSurface = getMonetNeutralColor(1, 100) ?: Color.White,
        )
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun getMaterialYouPallets(context: Context): Pair<ColorScheme, ColorScheme> {
    return Pair(
        dynamicLightColorScheme(context),
        dynamicDarkColorScheme(context)
    )
}

val spacingDefault = 16.dp

@ExperimentalAnimatedInsets
@Composable
fun CofiTheme(
    monet: MonetCompat? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val (lightColors, darkColors) = if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    ) {
        getMaterialYouPallets(context)
    } else {
        if (monet != null) {
            createMaterialYouPallets(monet)
        } else {
            Pair(lightColorScheme(), darkColorScheme())
        }
    }
    val colors = if (isSystemInDarkTheme()) darkColors else lightColors
    MaterialTheme(colorScheme = colors) {
        ProvideWindowInsets(windowInsetsAnimationsEnabled = false, content = content)
    }
}