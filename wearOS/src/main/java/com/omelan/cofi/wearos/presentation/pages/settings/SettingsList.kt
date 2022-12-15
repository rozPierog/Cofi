package com.omelan.cofi.wearos.presentation.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.*
import com.omelan.cofi.share.R

@Composable
fun Settings(navigateToLicenses: () -> Unit) {
    val lazyListState = rememberScalingLazyListState()
    Scaffold(
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = lazyListState)
        },
    ) {
        ScalingLazyColumn(state =  lazyListState) {
            item {
                Text(text = stringResource(id = R.string.settings_title))
            }
            item {
                Card(onClick = navigateToLicenses) {
                    Text(text = stringResource(id = R.string.settings_licenses_item))
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsPreview() {

}
