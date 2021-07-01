package com.omelan.cofi.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.kieronquinn.monetcompat.core.MonetCompat


private val DarkColorPalette = darkColors(
    primary = brown300,
    primaryVariant = brown700,
    secondary = iconBackground,
)

private val LightColorPalette = lightColors(
    primary = brown500,
    primaryVariant = brown700,
    secondary = iconBackground,
    secondaryVariant = iconBackground,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun createMaterialYouPallets(monet: MonetCompat): Pair<Colors, Colors> {
    val cotext = androidx.compose.ui.platform.LocalContext.current
    return Pair(
        lightColors(
            primary = Color(monet.getPrimaryColor(context = cotext, darkMode = false)),
//            primaryVariant = colorResource(id = android.R.color.system_accent1_400),
            secondary = Color(monet.getSecondaryColor(context = cotext, darkMode = false)),
//            secondaryVariant = colorResource(id = android.R.color.system_accent1_600),


            background = Color(monet.getBackgroundColor(context = cotext, darkMode = false)),
            surface = Color(
                monet.getBackgroundColorSecondary(context = cotext, darkMode = false)
                    ?: monet.getBackgroundColor(context = cotext, darkMode = false)
            ),
//            onPrimary = colorResource(id = android.R.color.system_neutral1_900),
//            onSecondary = colorResource(id = android.R.color.system_neutral2_700),
//            onBackground = Color.Black,
//            onSurface = colorResource(id = android.R.color.system_neutral1_900),
        ), darkColors(
            primary = Color(monet.getPrimaryColor(context = cotext, darkMode = true)),
//            primaryVariant = colorResource(id = android.R.color.system_accent1_400),
            secondary = Color(monet.getSecondaryColor(context = cotext, darkMode = true)),
//            secondaryVariant = colorResource(id = android.R.color.system_accent1_600),


            background = Color(monet.getBackgroundColor(context = cotext, darkMode = true)),
            surface = Color(
                monet.getBackgroundColorSecondary(context = cotext, darkMode = true)
                    ?: monet.getBackgroundColor(context = cotext, darkMode = true)
            ),
//            onPrimary = colorResource(id = android.R.color.system_neutral1_100),
//            onSecondary = colorResource(id = android.R.color.system_neutral1_100),
//            onBackground = Color.Black,
//            onSurface = colorResource(id = android.R.color.system_neutral1_100),
        )
    )
}

val spacingDefault = 16.dp

@SuppressLint("NewApi")
@ExperimentalAnimatedInsets
@Composable
fun CofiTheme(
    monet: MonetCompat? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val (lightColors, darkColors) = if (monet != null) createMaterialYouPallets(monet) else {
        Pair(LightColorPalette, DarkColorPalette)
    }
    val colors = if (darkTheme) {
        darkColors
    } else {
        lightColors
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
    ) {
        ProvideWindowInsets(windowInsetsAnimationsEnabled = false, content = content)
    }
}