@file:OptIn(
    ExperimentalMaterial3Api::class,
)

package com.omelan.cofi.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.components.Material3Dialog
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.createAppBarBehavior
import com.omelan.cofi.model.DataStore
import com.omelan.cofi.model.NEXT_STEP_ENABLED_DEFAULT_VALUE
import com.omelan.cofi.model.PIP_DEFAULT_VALUE
import com.omelan.cofi.share.*
import com.omelan.cofi.share.utils.askForNotificationPermission
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.utils.checkPiPPermission
import com.omelan.cofi.utils.getDefaultPadding
import kotlinx.coroutines.launch

@Composable
fun TimerSettings(goBack: () -> Unit) {
    val context = LocalContext.current

    val dataStore = DataStore(context)
    val isStepSoundEnabled by dataStore.getStepChangeSoundSetting()
        .collectAsState(STEP_SOUND_DEFAULT_VALUE)
    val isStepVibrationEnabled by dataStore.getStepChangeVibrationSetting()
        .collectAsState(STEP_VIBRATION_DEFAULT_VALUE)
    val isPiPEnabled by dataStore.getPiPSetting().collectAsState(PIP_DEFAULT_VALUE)
    val isBackgroundTimerEnabled by dataStore.getBackgroundTimerSetting().collectAsState(false)
    val isNextStepEnabled by dataStore.getNextStepSetting().collectAsState(
        NEXT_STEP_ENABLED_DEFAULT_VALUE,
    )
    val combineWeightState by dataStore.getWeightSetting()
        .collectAsState(COMBINE_WEIGHT_DEFAULT_VALUE)
    var showCombineWeightDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val appBarBehavior = createAppBarBehavior()

    val hasPiPPermission = checkPiPPermission(context)
    val toggleBackgroundTimer: () -> Unit = {
        context.askForNotificationPermission()
        coroutineScope.launch { dataStore.toggleBackgroundTimerEnabled() }
    }
    val togglePiP: () -> Unit = {
        coroutineScope.launch { dataStore.togglePipSetting() }
    }
    val toggleNextStep: () -> Unit = {
        coroutineScope.launch { dataStore.toggleNextStepEnabled() }
    }
    val toggleStepChangeSound: () -> Unit = {
        coroutineScope.launch { dataStore.toggleStepChangeSound() }
    }
    val toggleStepChangeVibration: () -> Unit = {
        coroutineScope.launch { dataStore.toggleStepChangeVibration() }
    }

    Scaffold(
        topBar = {
            PiPAwareAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_timer_item),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                scrollBehavior = appBarBehavior,
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .nestedScroll(appBarBehavior.nestedScrollConnection)
                .fillMaxSize(),
            contentPadding = getDefaultPadding(
                paddingValues = it,
                additionalStartPadding = 0.dp,
                additionalEndPadding = 0.dp,
            ),
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_pip_item))
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_picture_in_picture),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier
                        .settingsItemModifier(
                            onClick = togglePiP,
                            enabled = hasPiPPermission,
                        )
                        .testTag("settings_timer_list_item_pip"),
                    trailingContent = {
                        Switch(
                            modifier = Modifier.testTag("settings_timer_switch_pip"),
                            checked = if (!hasPiPPermission) false else isPiPEnabled,
                            onCheckedChange = { togglePiP() },
                            enabled = hasPiPPermission,
                        )
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(R.string.settings_background_timer_item))
                    },
                    leadingContent = {
                        Icon(Icons.Rounded.Notifications, contentDescription = null)
                    },
                    modifier = Modifier.settingsItemModifier(onClick = toggleBackgroundTimer),
                    trailingContent = {
                        Switch(
                            checked = isBackgroundTimerEnabled ?: false,
                            onCheckedChange = { toggleBackgroundTimer() },
                        )
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_step_sound_item))
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_sound),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier
                        .settingsItemModifier(onClick = toggleStepChangeSound)
                        .testTag("settings_timer_list_item_sound"),
                    trailingContent = {
                        Switch(
                            modifier = Modifier.testTag("settings_timer_switch_sound"),
                            checked = isStepSoundEnabled,
                            onCheckedChange = { toggleStepChangeSound() },
                        )
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_step_vibrate_item))
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_vibration),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier
                        .settingsItemModifier(onClick = toggleStepChangeVibration)
                        .testTag("settings_timer_list_item_vibration"),
                    trailingContent = {
                        Switch(
                            modifier = Modifier.testTag("settings_timer_switch_vibration"),
                            checked = isStepVibrationEnabled,
                            onCheckedChange = { toggleStepChangeVibration() },
                        )
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_step_next_step_item))
                    },
                    leadingContent = {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier
                        .settingsItemModifier(onClick = toggleNextStep)
                        .testTag("settings_timer_list_item_nextStep"),
                    trailingContent = {
                        Switch(
                            modifier = Modifier.testTag("settings_timer_switch_nextStep"),
                            checked = isNextStepEnabled,
                            onCheckedChange = { toggleNextStep() },
                        )
                    },
                )
            }
            item {
                ListItem(
                    overlineContent = {
                        Text(text = stringResource(id = R.string.settings_combine_weight_item))
                    },
                    headlineContent = {
                        Text(
                            text = stringResource(
                                stringToCombineWeight(combineWeightState).settingsStringId,
                            ),
                            fontWeight = FontWeight.Light,
                        )
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_list_add),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier
                        .settingsItemModifier(onClick = { showCombineWeightDialog = true })
                        .testTag("settings_timer_list_item_weight"),
                )
                if (showCombineWeightDialog) {
                    CombineWeightDialog(
                        dismiss = { showCombineWeightDialog = false },
                        selectCombineMethod = {
                            coroutineScope.launch {
                                dataStore.selectCombineMethod(it)
                                showCombineWeightDialog = false
                            }
                        },
                        combineWeightState = combineWeightState,
                    )
                }
            }
        }
    }
}

@Composable
fun CombineWeightDialog(
    dismiss: () -> Unit,
    selectCombineMethod: (CombineWeight) -> Unit,
    combineWeightState: String,
) {
    Material3Dialog(
        onDismissRequest = dismiss,
        title = {
            Text(text = stringResource(id = R.string.settings_combine_weight_item))
        },
    ) {
        CombineWeight.entries.forEach {
            ListItem(
                headlineContent = { Text(stringResource(id = it.settingsStringId)) },
                modifier = Modifier
                    .selectable(
                        selected = combineWeightState == it.name,
                        onClick = { selectCombineMethod(it) },
                    )
                    .testTag("settings_timer_list_item_weight_dialog"),
                leadingContent = {
                    RadioButton(
                        selected = combineWeightState == it.name,
                        onClick = { selectCombineMethod(it) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.secondary,
                        ),
                    )
                },
            )
        }
    }
}

@Composable
@Preview
fun PreviewCombineWeightDialog() {
    CofiTheme {
        CombineWeightDialog(
            dismiss = { },
            selectCombineMethod = {},
            combineWeightState = "",
        )
    }
}
