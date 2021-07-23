package com.omelan.cofi.pages

import android.os.Build
import android.util.Log
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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.omelan.cofi.components.PiPAwareAppBar

private val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(
        Pair(android.R.color.system_accent1_0, "system_accent1_0"),
        Pair(android.R.color.system_accent1_50, "system_accent1_50"),
        Pair(android.R.color.system_accent1_100, "system_accent1_100"),
        Pair(android.R.color.system_accent1_200, "system_accent1_200"),
        Pair(android.R.color.system_accent1_300, "system_accent1_300"),
        Pair(android.R.color.system_accent1_400, "system_accent1_400"),
        Pair(android.R.color.system_accent1_500, "system_accent1_500"),
        Pair(android.R.color.system_accent1_600, "system_accent1_600"),
        Pair(android.R.color.system_accent1_700, "system_accent1_700"),
        Pair(android.R.color.system_accent1_800, "system_accent1_800"),
        Pair(android.R.color.system_accent1_900, "system_accent1_900"),
        Pair(android.R.color.system_accent1_1000, "system_accent1_1000"),
        Pair(android.R.color.system_accent2_0, "system_accent2_0"),
        Pair(android.R.color.system_accent2_50, "system_accent2_50"),
        Pair(android.R.color.system_accent2_100, "system_accent2_100"),
        Pair(android.R.color.system_accent2_200, "system_accent2_200"),
        Pair(android.R.color.system_accent2_300, "system_accent2_300"),
        Pair(android.R.color.system_accent2_400, "system_accent2_400"),
        Pair(android.R.color.system_accent2_500, "system_accent2_500"),
        Pair(android.R.color.system_accent2_600, "system_accent2_600"),
        Pair(android.R.color.system_accent2_700, "system_accent2_700"),
        Pair(android.R.color.system_accent2_800, "system_accent2_800"),
        Pair(android.R.color.system_accent2_900, "system_accent2_900"),
        Pair(android.R.color.system_accent2_1000, "system_accent2_1000"),
        Pair(android.R.color.system_accent3_0, "system_accent3_0"),
        Pair(android.R.color.system_accent3_50, "system_accent3_50"),
        Pair(android.R.color.system_accent3_100, "system_accent3_100"),
        Pair(android.R.color.system_accent3_200, "system_accent3_200"),
        Pair(android.R.color.system_accent3_300, "system_accent3_300"),
        Pair(android.R.color.system_accent3_400, "system_accent3_400"),
        Pair(android.R.color.system_accent3_500, "system_accent3_500"),
        Pair(android.R.color.system_accent3_600, "system_accent3_600"),
        Pair(android.R.color.system_accent3_700, "system_accent3_700"),
        Pair(android.R.color.system_accent3_800, "system_accent3_800"),
        Pair(android.R.color.system_accent3_900, "system_accent3_900"),
        Pair(android.R.color.system_accent3_1000, "system_accent3_1000"),
        Pair(android.R.color.system_neutral1_0, "system_neutral1_0"),
        Pair(android.R.color.system_neutral1_50, "system_neutral1_50"),
        Pair(android.R.color.system_neutral1_100, "system_neutral1_100"),
        Pair(android.R.color.system_neutral1_200, "system_neutral1_200"),
        Pair(android.R.color.system_neutral1_300, "system_neutral1_300"),
        Pair(android.R.color.system_neutral1_400, "system_neutral1_400"),
        Pair(android.R.color.system_neutral1_500, "system_neutral1_500"),
        Pair(android.R.color.system_neutral1_600, "system_neutral1_600"),
        Pair(android.R.color.system_neutral1_700, "system_neutral1_700"),
        Pair(android.R.color.system_neutral1_800, "system_neutral1_800"),
        Pair(android.R.color.system_neutral1_900, "system_neutral1_900"),
        Pair(android.R.color.system_neutral1_1000, "system_neutral1_1000"),
        Pair(android.R.color.system_neutral2_0, "system_neutral2_0"),
        Pair(android.R.color.system_neutral2_50, "system_neutral2_50"),
        Pair(android.R.color.system_neutral2_100, "system_neutral2_100"),
        Pair(android.R.color.system_neutral2_200, "system_neutral2_200"),
        Pair(android.R.color.system_neutral2_300, "system_neutral2_300"),
        Pair(android.R.color.system_neutral2_400, "system_neutral2_400"),
        Pair(android.R.color.system_neutral2_500, "system_neutral2_500"),
        Pair(android.R.color.system_neutral2_600, "system_neutral2_600"),
        Pair(android.R.color.system_neutral2_700, "system_neutral2_700"),
        Pair(android.R.color.system_neutral2_800, "system_neutral2_800"),
        Pair(android.R.color.system_neutral2_900, "system_neutral2_900"),
        Pair(android.R.color.system_neutral2_1000, "system_neutral2_1000"),
    )
} else {
    listOf(
        Pair(android.R.color.holo_red_light, "holo_red")
    )
}

@Composable
fun ColorPicker(goToList: () -> Unit) {
    Scaffold(
        topBar = {
            PiPAwareAppBar(
                navigationIcon = {
                    IconButton(onClick = goToList) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
            )
        }
    ) {
        LazyColumn {
            colors.forEach { (colorId, colorName) ->
                item {
                    Box(
                        modifier = Modifier
                            .background(color = colorResource(id = colorId))
                            .height(50.dp)
                            .fillMaxWidth()
                            .padding(15.dp)
                            .clickable { Log.e("color", colorName) }
                    ) {
                        Text(text = colorName)
                    }
                }
            }
        }
    }
}