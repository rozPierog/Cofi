@file:OptIn(ExperimentalHorologistApi::class)

package com.omelan.cofi.wearos.presentation.pages.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import com.omelan.cofi.share.*
import com.omelan.cofi.share.R
import com.omelan.cofi.wearos.presentation.components.OpenOnPhoneConfirm
import com.omelan.cofi.wearos.presentation.model.DataStore
import com.omelan.cofi.wearos.presentation.model.SYNC_SETTINGS_FROM_PHONE_DEFAULT_VALUE
import com.omelan.cofi.wearos.presentation.utils.WearUtils
import kotlinx.coroutines.launch


@Composable
fun Settings(navigateToLicenses: () -> Unit) {
    val activity = LocalContext.current as ComponentActivity
    val dataStore = DataStore(activity)
    val lazyListState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }

    val coroutineScope = rememberCoroutineScope()
    val getSettingsFromPhone by dataStore.getSyncSettingsFromPhoneSetting()
        .collectAsState(initial = SYNC_SETTINGS_FROM_PHONE_DEFAULT_VALUE)
    val stepChangeSound by dataStore.getStepChangeSoundSetting()
        .collectAsState(initial = STEP_SOUND_DEFAULT_VALUE)
    val stepChangeVibration by dataStore.getStepChangeVibrationSetting()
        .collectAsState(initial = STEP_VIBRATION_DEFAULT_VALUE)
    val weightSettings by dataStore.getWeightSetting()
        .collectAsState(initial = COMBINE_WEIGHT_DEFAULT_VALUE)
    val backgroundTimer by dataStore.getBackgroundTimerSetting()
        .collectAsState(initial = false)
    var showConfirmation by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val versionName = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }
    Scaffold(
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = lazyListState)
        },
        timeText = {
            TimeText(Modifier.scrollAway(lazyListState))
        },
    ) {
        ScalingLazyColumn(
            state = lazyListState,
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .rotaryWithScroll(scrollableState = lazyListState, focusRequester),
        ) {
            item {
                Text(text = stringResource(id = R.string.settings_title))
            }
//            item {
//                ToggleChip(
//                    checked = backgroundTimer ?: false,
//                    onCheckedChange = {
//                        coroutineScope.launch {
//                            dataStore.setBackgroundTimerEnabled(it)
//                            context.askForNotificationPermission()
//                        }
//                    },
//                    label = {
//                        Text(text = stringResource(id = R.string.settings_background_timer_item))
//                    },
//                    toggleControl = {
//                        Switch(
//                            checked = backgroundTimer ?: false,
//                            onCheckedChange = {
//                                coroutineScope.launch {
//                                    dataStore.setBackgroundTimerEnabled(it)
//                                    context.askForNotificationPermission()
//                                }
//                            },
//                        )
//                    },
//                )
//            }
            item {
                ToggleChip(
                    checked = getSettingsFromPhone,
                    onCheckedChange = {
                        coroutineScope.launch {
                            dataStore.setSyncSettingsFromPhone(it)
                        }
                    },
                    label = {
                        Text(text = stringResource(id = R.string.settings_sync_with_phone))
                    },
                    toggleControl = {
                        Switch(
                            checked = getSettingsFromPhone,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    dataStore.setSyncSettingsFromPhone(it)
                                }
                            },
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
                    toggleControl = {
                        Switch(
                            checked = stepChangeSound,
                            enabled = !getSettingsFromPhone,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    dataStore.setStepChangeSound(it)
                                }
                            },
                        )
                    },
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
                        Switch(
                            checked = stepChangeVibration,
                            enabled = !getSettingsFromPhone,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    dataStore.setStepChangeVibration(it)
                                }
                            },
                        )
                    },
                )
            }
            item {
                ToggleChip(
                    label = {
                        Text(
                            text = stringResource(
                                stringToCombineWeight(weightSettings).settingsStringId,
                            ),
                        )
                    },
                    enabled = !getSettingsFromPhone,
                    onCheckedChange = {
                        val values = CombineWeight.entries.toTypedArray()
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
                Text(text = stringResource(id = R.string.step_type_other))
            }
            item {
                Card(onClick = navigateToLicenses) {
                    Text(text = stringResource(id = R.string.settings_licenses_item))
                }
            }
            item {
                Card(
                    onClick = {
                        WearUtils.openLinkOnPhone(
                            "https://github.com/rozPierog/Cofi/blob/main/docs/Changelog.md",
                            activity = activity,
                            onSuccess = { showConfirmation = true },
                        )
                    },
                ) {
                    Column {
                        Text(text = stringResource(id = R.string.app_version))
                        Text(
                            text = versionName,
                            fontWeight = FontWeight.Light,
                        )

                    }
                }
            }
        }
        OpenOnPhoneConfirm(isVisible = showConfirmation, onTimeout = { showConfirmation = false })
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
fun SettingsPreview() {

}
