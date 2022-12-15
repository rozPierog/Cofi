package com.omelan.cofi.wearos.presentation.pages.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.*
import com.omelan.cofi.share.*
import com.omelan.cofi.share.R
import kotlinx.coroutines.launch

@Composable
fun Settings(navigateToLicenses: () -> Unit) {
    val dataStore = DataStore(LocalContext.current)
    val lazyListState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var getSettingsFromPhone by remember {
        mutableStateOf(false)
    }
    val stepChangeSound by dataStore.getStepChangeSoundSetting()
        .collectAsState(initial = STEP_SOUND_DEFAULT_VALUE)
    val stepChangeVibration by dataStore.getStepChangeVibrationSetting()
        .collectAsState(initial = STEP_VIBRATION_DEFAULT_VALUE)
    val weightSettings by dataStore.getWeightSetting()
        .collectAsState(initial = COMBINE_WEIGHT_DEFAULT_VALUE)
    Scaffold(
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = lazyListState)
        },
    ) {
        ScalingLazyColumn(state = lazyListState) {
            item {
                Text(text = stringResource(id = R.string.settings_title))
            }
            item {
                ToggleChip(
                    checked = getSettingsFromPhone,
                    onCheckedChange = {
                        getSettingsFromPhone = it
//                        coroutineScope.launch {
//                            dataStore.setStepChangeSound(it)
//                        }
                    },
                    label = {
                        Text(text = "Get settings from the phone")
                    },
                    toggleControl = {
                        Icon(
                            ToggleChipDefaults.switchIcon(getSettingsFromPhone),
                            "",
                        )
                    },
                )
            }
            item {
                ToggleChip(
                    checked = stepChangeSound,
                    enabled = !getSettingsFromPhone,
                    onCheckedChange = {
                        coroutineScope.launch {
                            dataStore.setStepChangeSound(it)
                        }
                    },
                    label = {
                        Text(text = stringResource(id = R.string.settings_step_sound_item))
                    },
                    toggleControl = { Icon(ToggleChipDefaults.switchIcon(stepChangeSound), "") },
                )
            }
            item {
                ToggleChip(
                    checked = stepChangeVibration,
                    enabled = !getSettingsFromPhone,
                    onCheckedChange = {
                        coroutineScope.launch {
                            dataStore.setStepChangeVibration(it)
                        }
                    },
                    label = {
                        Text(text = stringResource(id = R.string.settings_step_vibrate_item))

                    },
                    toggleControl = {
                        Icon(ToggleChipDefaults.switchIcon(stepChangeVibration), "")
                    },
                )
            }
            item {
                ToggleChip(
                    label = {
                        Column {
//                            Text(text = stringResource(id = R.string.settings_combine_weight_item))
                            Text(
                                text = stringResource(
                                    stringToCombineWeight(weightSettings).settingsStringId,
                                ),
                                fontWeight = FontWeight.Light,
                            )
                        }
                    },
                    enabled = !getSettingsFromPhone,
                    onCheckedChange = {
                        val values = CombineWeight.values()
                        coroutineScope.launch {
                            dataStore.selectCombineMethod(
                                values.getOrElse(
                                    values.indexOfFirst { it.name == weightSettings } + 1,
                                ) { values.first() },
                            )
                        }
                    },
                    checked = true,
                    toggleControl = {},
                )
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
