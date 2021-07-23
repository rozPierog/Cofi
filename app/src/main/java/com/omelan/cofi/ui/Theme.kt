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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.kieronquinn.monetcompat.core.MonetCompat

// private val DarkColorPalette = darkColors(
//    primary = brown300,
//    primaryVariant = brown700,
//    secondary = iconBackground,
// )
//
// private val LightColorPalette = lightColors(
//    primary = brown500,
//    primaryVariant = brown700,
//    secondary = iconBackground,
//    secondaryVariant = iconBackground,
//
//    /* Other default colors to override
//    background = Color.White,
//    surface = Color.White,
//    onPrimary = Color.White,
//    onSecondary = Color.Black,
//    onBackground = Color.Black,
//    onSurface = Color.Black,
//    */
// )

@Composable
fun createMaterialYouPallets(monet: MonetCompat): Pair<Colors, Colors> {
    val context = androidx.compose.ui.platform.LocalContext.current
    fun getMonetNeutral1Color(level: Int): Color? {
        val monetColor = monet.getMonetColors().neutral1[level]?.toLinearSrgb() ?: return null
        val red = monetColor.r.toFloat()
        val green = monetColor.g.toFloat()
        val blue = monetColor.b.toFloat()
        return Color(red, green, blue)
    }
    return Pair(
        lightColors(
            primary = Color(monet.getPrimaryColor(context = context, darkMode = false)),
//            primaryVariant = colorResource(id = android.R.color.system_accent1_400),
            secondary = Color(monet.getSecondaryColor(context = context, darkMode = false)),
//            secondaryVariant = colorResource(id = android.R.color.system_accent1_600),
            background = Color(monet.getBackgroundColor(context = context, darkMode = false)),
            surface = Color(
                monet.getBackgroundColorSecondary(context = context, darkMode = false)
                    ?: monet.getBackgroundColor(context = context, darkMode = false)
            ),
            onPrimary = getMonetNeutral1Color(900) ?: Color.White,
            onSecondary = getMonetNeutral1Color(700) ?: Color.Black,
            onBackground = Color.Black,
            onSurface = getMonetNeutral1Color(900) ?: Color.White,
        ),
        darkColors(
            primary = Color(monet.getPrimaryColor(context = context, darkMode = true)),
//            primaryVariant = colorResource(id = android.R.color.system_accent1_400),
            secondary = Color(monet.getSecondaryColor(context = context, darkMode = true)),
//            secondaryVariant = colorResource(id = android.R.color.system_accent1_600),
            background = Color(monet.getBackgroundColor(context = context, darkMode = true)),
            surface = Color(
                monet.getBackgroundColorSecondary(context = context, darkMode = true)
                    ?: monet.getBackgroundColor(context = context, darkMode = true)
            ),
            onPrimary = getMonetNeutral1Color(100) ?: Color.Black,
            onSecondary = getMonetNeutral1Color(100) ?: Color.Black,
            onBackground = Color.White,
            onSurface = getMonetNeutral1Color(100) ?: Color.White,
        )
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun getMaterialYouPallets(): Pair<Colors, Colors> {
    return Pair(
        lightColors(
            primary = colorResource(id = android.R.color.system_accent2_100),
            primaryVariant = colorResource(id = android.R.color.system_accent1_400), //
            secondary = colorResource(id = android.R.color.system_accent2_300),

            secondaryVariant = colorResource(id = android.R.color.system_accent1_600), //

            background = colorResource(id = android.R.color.system_neutral1_50),
            surface = colorResource(id = android.R.color.system_neutral1_100),
            onPrimary = colorResource(id = android.R.color.system_neutral1_900), //
            onSecondary = colorResource(id = android.R.color.system_neutral2_700), //
            onBackground = Color.Black, //
            onSurface = colorResource(id = android.R.color.system_neutral1_900), //
        ),
        darkColors(
            primary = colorResource(id = android.R.color.system_accent2_600),
//            primaryVariant = colorResource(id = android.R.color.system_accent1_400),
            secondary = colorResource(id = android.R.color.system_accent2_400),
//            secondaryVariant = colorResource(id = android.R.color.system_accent1_600),
            background = colorResource(id = android.R.color.system_neutral1_900),
            surface = colorResource(id = android.R.color.system_neutral1_700),
            onPrimary = colorResource(id = android.R.color.system_neutral1_100), //
            onSecondary = colorResource(id = android.R.color.system_neutral1_100), //
            onBackground = Color.White, //
            onSurface = colorResource(id = android.R.color.system_neutral1_100), //
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
    val (lightColors, darkColors) = if (monet != null &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S
    ) {
        createMaterialYouPallets(monet)
    } else {
        getMaterialYouPallets()
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