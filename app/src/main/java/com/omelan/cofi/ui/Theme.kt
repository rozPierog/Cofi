package com.omelan.cofi.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
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
    fun getMonetNeutral1Color(level: Int): Color? {
        val monetColor = monet.getMonetColors().neutral1[level]?.toLinearSrgb() ?: return null
        val red = monetColor.r.toFloat()
        val green = monetColor.g.toFloat()
        val blue = monetColor.b.toFloat()
        return Color(red, green, blue)
    }
    return Pair(
        lightColorScheme(
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
            onPrimary = getMonetNeutral1Color(100) ?: Color.Black,
            onSecondary = getMonetNeutral1Color(100) ?: Color.Black,
            onBackground = Color.White,
            onSurface = getMonetNeutral1Color(100) ?: Color.White,
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

@SuppressLint("NewApi")
@ExperimentalAnimatedInsets
@Composable
fun CofiTheme(
    monet: MonetCompat? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val (lightColors, darkColors) = if (monet != null &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S
    ) {
        createMaterialYouPallets(monet)
    } else {
        getMaterialYouPallets(context)
    }
    val colors = if (darkTheme) {
        darkColors
    } else {
        lightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
    ) {
        ProvideWindowInsets(windowInsetsAnimationsEnabled = false, content = content)
    }
}