@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.omelan.cofi.ui

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.omelan.cofi.model.DYNAMIC_THEME_DEFAULT_VALUE
import com.omelan.cofi.model.DataStore

val lightColors by lazy {
    lightColorScheme(
        primary = Color(18406591352172183552UL),
        onPrimary = Color(18446744069414584320UL),
        primaryContainer = Color(18446142434690727936UL),
        onPrimaryContainer = Color(18385970840521932800UL),
        inversePrimary = Color(18437667283099713536UL),
        secondary = Color(18404904722810011648UL),
        onSecondary = Color(18446744069414584320UL),
        secondaryContainer = Color(18443892859670102016UL),
        onSecondaryContainer = Color(18385128627499958272UL),
        tertiary = Color(18408277981534355456UL),
        onTertiary = Color(18446744069414584320UL),
        tertiaryContainer = Color(18446704276542586880UL),
        onTertiaryContainer = Color(18387095623737278464UL),
        background = Color(18446736334178484224UL),
        onBackground = Color(18383160527456043008UL),
        surface = Color(18446736334178484224UL),
        onSurface = Color(18383160527456043008UL),
        surfaceVariant = Color(18440518492844195840UL),
        onSurfaceVariant = Color(18395592885819932672UL),
        surfaceTint = Color(18406591352172183552UL),
        inverseSurface = Color(18380899897189597184UL),
        inverseOnSurface = Color(18440518492844195840UL),
        error = Color(18425112410793705472UL),
        onError = Color(18446744069414584320UL),
        errorContainer = Color(18445018785346748416UL),
        onErrorContainer = Color(18392997793565245440UL),
        outline = Color(18409719617372028928UL),
        outlineVariant = Color(18432324816229892096UL),
        scrim = Color(18374686479671623680UL),
        surfaceBright = Color(18446736334178484224UL),
        surfaceDim = Color(18437975288089411584UL),
        surfaceContainer = Color(18443626863755526144UL),
        surfaceContainerHigh = Color(18442212866032402432UL),
        surfaceContainerHighest = Color(18440518492844195840UL),
        surfaceContainerLow = Color(18445322332160393216UL),
        surfaceContainerLowest = Color(18446744069414584320UL),
        primaryFixed = Color(18446142434690727936UL),
        primaryFixedDim = Color(18437667283099713536UL),
        onPrimaryFixed = Color(18385970840521932800UL),
        onPrimaryFixedVariant = Color(18399529090202730496UL),
        secondaryFixed = Color(18443892859670102016UL),
        secondaryFixedDim = Color(18435699178760830976UL),
        onSecondaryFixed = Color(18385128627499958272UL),
        onSecondaryFixedVariant = Color(18398122836305641472UL),
        tertiaryFixed = Color(18446704276542586880UL),
        tertiaryFixedDim = Color(18439916858120339456UL),
        onTertiaryFixed = Color(18387095623737278464UL),
        onTertiaryFixedVariant = Color(18400934248883159040UL),
    )
}

val darkColors by lazy {
    darkColorScheme(
        primary = Color(18437667283099713536UL),
        onPrimary = Color(18392749411311550464UL),
        primaryContainer = Color(18399529090202730496UL),
        onPrimaryContainer = Color(18446142434690727936UL),
        inversePrimary = Color(18406591352172183552UL),
        secondary = Color(18435699178760830976UL),
        onSecondary = Color(18391343153119494144UL),
        secondaryContainer = Color(18398122836305641472UL),
        onSecondaryContainer = Color(18443892859670102016UL),
        tertiary = Color(18439916858120339456UL),
        onTertiary = Color(18393873095015268352UL),
        tertiaryContainer = Color(18400934248883159040UL),
        onTertiaryContainer = Color(18446704276542586880UL),
        background = Color(18380899897189597184UL),
        onBackground = Color(18440518492844195840UL),
        surface = Color(18380899897189597184UL),
        onSurface = Color(18440518492844195840UL),
        surfaceVariant = Color(18395592885819932672UL),
        onSurfaceVariant = Color(18432324816229892096UL),
        surfaceTint = Color(18437667283099713536UL),
        inverseSurface = Color(18446736334178484224UL),
        inverseOnSurface = Color(18383160527456043008UL),
        error = Color(18443006511564193792UL),
        onError = Color(18401730136387878912UL),
        errorContainer = Color(18414124965327536128UL),
        onErrorContainer = Color(18445018785346748416UL),
        outline = Color(18417065561931382784UL),
        outlineVariant = Color(18395592885819932672UL),
        scrim = Color(18374686479671623680UL),
        surfaceBright = Color(18391636787148619776UL),
        surfaceDim = Color(18380899897189597184UL),
        surfaceContainer = Color(18384290842589265920UL),
        surfaceContainerHigh = Color(18387398105399033856UL),
        surfaceContainerHighest = Color(18390506472015396864UL),
        surfaceContainerLow = Color(18383160527456043008UL),
        surfaceContainerLowest = Color(18379205528296357888UL),
        primaryFixed = Color(18446142434690727936UL),
        primaryFixedDim = Color(18437667283099713536UL),
        onPrimaryFixed = Color(18385970840521932800UL),
        onPrimaryFixedVariant = Color(18399529090202730496UL),
        secondaryFixed = Color(18443892859670102016UL),
        secondaryFixedDim = Color(18435699178760830976UL),
        onSecondaryFixed = Color(18385128627499958272UL),
        onSecondaryFixedVariant = Color(18398122836305641472UL),
        tertiaryFixed = Color(18446704276542586880UL),
        tertiaryFixedDim = Color(18439916858120339456UL),
        onTertiaryFixed = Color(18387095623737278464UL),
        onTertiaryFixedVariant = Color(18400934248883159040UL),
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun getMaterialYouPallets(context: Context, darkMode: Boolean) =
    if (darkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

@Composable
fun CofiTheme(
    isDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val dataStore = DataStore(context)
    val dynamicThemeEnabled by dataStore.getDynamicThemeSetting().collectAsState(
        DYNAMIC_THEME_DEFAULT_VALUE,
    )
    val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && dynamicThemeEnabled) {
        getMaterialYouPallets(context, isDarkMode)
    } else {
        if (isDarkMode) darkColors else lightColors
    }
    Log.e("primary", "${colors.primary.value}")
    Log.e("onPrimary", "${colors.onPrimary.value}")
    Log.e("primaryContainer", "${colors.primaryContainer.value}")
    Log.e("onPrimaryContainer", "${colors.onPrimaryContainer.value}")
    Log.e("inversePrimary", "${colors.inversePrimary.value}")
    Log.e("secondary", "${colors.secondary.value}")
    Log.e("onSecondary", "${colors.onSecondary.value}")
    Log.e("secondaryContainer", "${colors.secondaryContainer.value}")
    Log.e("onSecondaryContainer", "${colors.onSecondaryContainer.value}")
    Log.e("tertiary", "${colors.tertiary.value}")
    Log.e("onTertiary", "${colors.onTertiary.value}")
    Log.e("tertiaryContainer", "${colors.tertiaryContainer.value}")
    Log.e("onTertiaryContainer", "${colors.onTertiaryContainer.value}")
    Log.e("background", "${colors.background.value}")
    Log.e("onBackground", "${colors.onBackground.value}")
    Log.e("surface", "${colors.surface.value}")
    Log.e("onSurface", "${colors.onSurface.value}")
    Log.e("surfaceVariant", "${colors.surfaceVariant.value}")
    Log.e("onSurfaceVariant", "${colors.onSurfaceVariant.value}")
    Log.e("surfaceTint", "${colors.surfaceTint.value}")
    Log.e("inverseSurface", "${colors.inverseSurface.value}")
    Log.e("inverseOnSurface", "${colors.inverseOnSurface.value}")
    Log.e("error", "${colors.error.value}")
    Log.e("onError", "${colors.onError.value}")
    Log.e("errorContainer", "${colors.errorContainer.value}")
    Log.e("onErrorContainer", "${colors.onErrorContainer.value}")
    Log.e("outline", "${colors.outline.value}")
    Log.e("outlineVariant", "${colors.outlineVariant.value}")
    Log.e("scrim", "${colors.scrim.value}")
    Log.e("surfaceBright", "${colors.surfaceBright.value}")
    Log.e("surfaceDim", "${colors.surfaceDim.value}")
    Log.e("surfaceContainer", "${colors.surfaceContainer.value}")
    Log.e("surfaceContainerHigh", "${colors.surfaceContainerHigh.value}")
    Log.e("surfaceContainerHighest", "${colors.surfaceContainerHighest.value}")
    Log.e("surfaceContainerLow", "${colors.surfaceContainerLow.value}")
    Log.e("surfaceContainerLowest", "${colors.surfaceContainerLowest.value}")
    Log.e("primaryFixed", "${colors.primaryFixed.value}")
    Log.e("primaryFixedDim", "${colors.primaryFixedDim.value}")
    Log.e("onPrimaryFixed", "${colors.onPrimaryFixed.value}")
    Log.e("onPrimaryFixedVariant", "${colors.onPrimaryFixedVariant.value}")
    Log.e("secondaryFixed", "${colors.secondaryFixed.value}")
    Log.e("secondaryFixedDim", "${colors.secondaryFixedDim.value}")
    Log.e("onSecondaryFixed", "${colors.onSecondaryFixed.value}")
    Log.e("onSecondaryFixedVariant", "${colors.onSecondaryFixedVariant.value}")
    Log.e("tertiaryFixed", "${colors.tertiaryFixed.value}")
    Log.e("tertiaryFixedDim", "${colors.tertiaryFixedDim.value}")
    Log.e("onTertiaryFixed", "${colors.onTertiaryFixed.value}")
    Log.e("onTertiaryFixedVariant", "${colors.onTertiaryFixedVariant.value}")
    MaterialExpressiveTheme(colorScheme = colors, content = content)
}
