@file:OptIn(ExperimentalMaterial3Api::class)

package com.omelan.cofi.pages

import android.util.Log
import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.toArgb
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.getDefaultPadding

@Composable
fun ColorPicker(goToList: () -> Unit, monet: MonetCompat) {
    val colors = listOf(
        Pair("primary", MaterialTheme.colorScheme.primary),
        Pair("onPrimary", MaterialTheme.colorScheme.onPrimary),
        Pair("primaryContainer", MaterialTheme.colorScheme.primaryContainer),
        Pair("onPrimaryContainer", MaterialTheme.colorScheme.onPrimaryContainer),
        Pair("inversePrimary", MaterialTheme.colorScheme.inversePrimary),
        Pair("secondary", MaterialTheme.colorScheme.secondary),
        Pair("onSecondary", MaterialTheme.colorScheme.onSecondary),
        Pair("secondaryContainer", MaterialTheme.colorScheme.secondaryContainer),
        Pair("onSecondaryContainer", MaterialTheme.colorScheme.onSecondaryContainer),
        Pair("tertiary", MaterialTheme.colorScheme.tertiary),
        Pair("onTertiary", MaterialTheme.colorScheme.onTertiary),
        Pair("tertiaryContainer", MaterialTheme.colorScheme.tertiaryContainer),
        Pair("onTertiaryContainer", MaterialTheme.colorScheme.onTertiaryContainer),
        Pair("background", MaterialTheme.colorScheme.background),
        Pair("onBackground", MaterialTheme.colorScheme.onBackground),
        Pair("surface", MaterialTheme.colorScheme.surface),
        Pair("onSurface", MaterialTheme.colorScheme.onSurface),
        Pair("surfaceVariant", MaterialTheme.colorScheme.surfaceVariant),
        Pair("onSurfaceVariant", MaterialTheme.colorScheme.onSurfaceVariant),
        Pair("inverseSurface", MaterialTheme.colorScheme.inverseSurface),
        Pair("inverseOnSurface", MaterialTheme.colorScheme.inverseOnSurface),
        Pair("error", MaterialTheme.colorScheme.error),
        Pair("onError", MaterialTheme.colorScheme.onError),
        Pair("errorContainer", MaterialTheme.colorScheme.errorContainer),
        Pair("onErrorContainer", MaterialTheme.colorScheme.onErrorContainer),
        Pair("outline", MaterialTheme.colorScheme.outline),
    )

    fun getMonetNeutralColor(
        @IntRange(from = 1, to = 2) type: Int,
        @IntRange(from = 50, to = 900) level: Int,
    ): Color? {
        val monetColor = when (type) {
            1 -> monet.getMonetColors().neutral1[level]
            else -> monet.getMonetColors().neutral2[level]
        }?.toArgb() ?: return null

        return Color(monetColor)
    }

    fun getMonetAccentColor(
        @IntRange(from = 1, to = 2) type: Int,
        @IntRange(from = 50, to = 900) level: Int,
    ): Color? {
        val monetColor = when (type) {
            1 -> monet.getMonetColors().accent1[level]
            2 -> monet.getMonetColors().accent2[level]
            else -> monet.getMonetColors().accent3[level]
        }?.toArgb() ?: return null
        return Color(monetColor)
    }

    val shades = listOf(50, 100, 200, 300, 400, 500, 600, 700, 800, 900)
    Scaffold(
        topBar = {
            PiPAwareAppBar(
                navigationIcon = {
                    IconButton(onClick = goToList) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) {
        LazyColumn(contentPadding = getDefaultPadding(paddingValues = it)) {
//            shades.forEach { shade ->
//                item {
//                    Box(
//                        modifier = Modifier
//                            .background(color = getMonetAccentColor(1, shade) ?: Color.Cyan)
//                            .height(50.dp)
//                            .fillMaxWidth()
//                            .padding(15.dp)
//                            .clickable { Log.e("color", "accent1$shade") }
//                    ) {
//                        Text(text = "accent1$shade")
//                    }
//                }
//            }
//            shades.forEach { shade ->
//                item {
//                    Box(
//                        modifier = Modifier
//                            .background(color = getMonetAccentColor(2, shade) ?: Color.Cyan)
//                            .height(50.dp)
//                            .fillMaxWidth()
//                            .padding(15.dp)
//                            .clickable { Log.e("color", "accent2$shade") }
//                    ) {
//                        Text(text = "accent2$shade")
//                    }
//                }
//            }
//            shades.forEach { shade ->
//                item {
//                    Box(
//                        modifier = Modifier
//                            .background(color = getMonetAccentColor(3, shade) ?: Color.Cyan)
//                            .height(50.dp)
//                            .fillMaxWidth()
//                            .padding(15.dp)
//                            .clickable { Log.e("color", "accent3$shade") }
//                    ) {
//                        Text(text = "accent3$shade")
//                    }
//                }
//            }
//            shades.forEach { shade ->
//                item {
//                    Box(
//                        modifier = Modifier
//                            .background(color = getMonetNeutralColor(1, shade) ?: Color.Cyan)
//                            .height(50.dp)
//                            .fillMaxWidth()
//                            .padding(15.dp)
//                            .clickable { Log.e("color", "neutral1$shade") }
//                    ) {
//                        Text(text = "neutral1$shade")
//                    }
//                }
//            }
//            shades.forEach { shade ->
//                item {
//                    Box(
//                        modifier = Modifier
//                            .background(color = getMonetNeutralColor(2, shade) ?: Color.Cyan)
//                            .height(50.dp)
//                            .fillMaxWidth()
//                            .padding(15.dp)
//                            .clickable { Log.e("color", "neutral2$shade") }
//                    ) {
//                        Text(text = "neutral2$shade")
//                    }
//                }
//            }
            colors.forEach { (colorName, color) ->
                item {
                    Box(
                        modifier = Modifier
                            .background(color = color)
                            .height(50.dp)
                            .fillMaxWidth()
                            .padding(Spacing.big)
                            .clickable { Log.e("color", colorName) },
                    ) {
                        Text(text = colorName)
                    }
                }
            }
        }
    }
}
