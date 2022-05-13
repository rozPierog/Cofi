package com.omelan.cofi.pages.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.omelan.cofi.*
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.createAppBarBehavior
import com.omelan.cofi.model.AppDatabase
import com.omelan.cofi.model.PrepopulateData
import com.omelan.cofi.model.Recipe
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.checkPiPPermission
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AppSettings(goBack: () -> Unit, goToAbout: () -> Unit) {
    val layoutDirection = LocalLayoutDirection.current
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val dataStore = DataStore(context)
    val isStepSoundEnabled by dataStore.getStepChangeSetting()
        .collectAsState(STEP_SOUND_DEFAULT_VALUE)
    val isPiPEnabled by dataStore.getPiPSetting().collectAsState(PIP_DEFAULT_VALUE)
    val combineWeightState by dataStore.getWeightSetting()
        .collectAsState(COMBINE_WEIGHT_DEFAULT_VALUE)
    var showCombineWeightDialog by remember { mutableStateOf(false) }
    var showDefaultRecipeDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val appBarBehavior = createAppBarBehavior()

    val hasPiPPermission = checkPiPPermission(context)
    val togglePiP: () -> Unit = {
        coroutineScope.launch { dataStore.togglePipSetting() }
    }
    val toggleStepChangeSound: () -> Unit = {
        coroutineScope.launch { dataStore.toggleStepChangeSound() }
    }

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.secondary,
        checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
    )
    Scaffold(
        topBar = {
            PiPAwareAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                scrollBehavior = appBarBehavior,
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .nestedScroll(appBarBehavior.nestedScrollConnection)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = navigationBarPadding.calculateStartPadding(layoutDirection) +
                        it.calculateStartPadding(layoutDirection) + Spacing.big,
                top = navigationBarPadding.calculateTopPadding() +
                        it.calculateTopPadding() + Spacing.small,
                bottom = navigationBarPadding.calculateBottomPadding() +
                        it.calculateBottomPadding() + Spacing.big + 76.dp,
                end = navigationBarPadding.calculateEndPadding(layoutDirection) +
                        it.calculateEndPadding(layoutDirection) + Spacing.big
            )
        ) {
            item {
                ListItem(
                    text = {
                        Text(text = stringResource(id = R.string.settings_pip_item))
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_picture_in_picture),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = togglePiP,
                        enabled = hasPiPPermission
                    ),
                    trailing = {
                        // TODO: Material3 Switch - right now it has issue that it's stuck on default after first render
                        Switch(
                            checked = isPiPEnabled,
                            onCheckedChange = { togglePiP() },
                            enabled = hasPiPPermission,
                            colors = switchColors,
                        )
                    }
                )
            }
            item {
                ListItem(
                    text = {
                        Text(text = stringResource(id = R.string.settings_ding_item))
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_sound),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.settingsItemModifier(onClick = toggleStepChangeSound),
                    trailing = {
                        Switch(
                            checked = isStepSoundEnabled,
                            onCheckedChange = { toggleStepChangeSound() },
                            colors = switchColors,
                        )
                    }
                )
            }
            item {
                ListItem(
                    overlineText = {
                        Text(text = stringResource(id = R.string.settings_combine_weight_item))
                    },
                    text = {
                        Text(
                            text = stringResource(
                                stringToCombineWeight(combineWeightState).settingsStringId
                            ),
                            fontWeight = FontWeight.Light
                        )
                    },
                    icon = { Icon(Icons.Rounded.List, contentDescription = null) },
                    modifier = Modifier.settingsItemModifier(
                        onClick = { showCombineWeightDialog = true },
                    ),
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
                        combineWeightState = combineWeightState
                    )
                }
            }
            item {
                ListItem(
                    text = { Text(text = "Add default recipes") },
                    icon = { Icon(Icons.Rounded.AddCircle, contentDescription = null) },
                    modifier = Modifier.settingsItemModifier(
                        onClick = { showDefaultRecipeDialog = true }
                    ),
                )
                if (showDefaultRecipeDialog) {
                    DefaultRecipesDialog(dismiss = { showDefaultRecipeDialog = false })
                }
            }
            item {
                ListItem(
                    text = { Text(text = stringResource(id = R.string.settings_bug_item)) },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_bug_report),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = {
                            uriHandler.openUri("https://github.com/rozPierog/Cofi/issues")
                        }
                    ),
                )
            }
            item {
                ListItem(
                    text = { Text(text = stringResource(id = R.string.settings_about_item)) },
                    icon = { Icon(Icons.Rounded.Info, contentDescription = null) },
                    modifier = Modifier.settingsItemModifier(onClick = goToAbout)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterialApi
fun DefaultRecipesDialog(dismiss: () -> Unit) {
    val recipesToAdd = remember { mutableStateListOf<Recipe>() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val prepopulateData = PrepopulateData(context)
    val steps = prepopulateData.steps.groupBy { it.recipeId }
    val db = AppDatabase.getInstance(context)
    Dialog(
        onDismissRequest = dismiss
    ) {
        Surface(
            shape = RoundedCornerShape(28.0.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(Modifier.padding(vertical = Spacing.big)) {
                LazyColumn {
                    items(prepopulateData.recipes) {
                        val isSelected = recipesToAdd.contains(it)
                        val onCheck: () -> Unit = {
                            if (isSelected) recipesToAdd.remove(it) else recipesToAdd.add(it)
                        }
                        ListItem(
                            text = { Text(it.name) },
                            modifier = Modifier.selectable(
                                selected = isSelected,
                                onClick = onCheck
                            ),
                            icon = {
                                Checkbox(checked = isSelected, onCheckedChange = { onCheck() })
                            }
                        )
                    }
                }
                TextButton(
                    onClick = {
                        recipesToAdd.forEach {
                            coroutineScope.launch {
                                val idOfRecipe = db.recipeDao().insertRecipe(it.copy(id = 0))
                                val stepsOfTheRecipe =
                                    steps[prepopulateData.recipes.indexOf(it)] ?: return@launch
                                db.stepDao().insertAll(
                                    stepsOfTheRecipe.map {
                                        it.copy(
                                            id = 0,
                                            recipeId = idOfRecipe.toInt()
                                        )
                                    }
                                )
                            }
                        }
                        dismiss()
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = Spacing.big)
                ) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterialApi
@Composable
fun CombineWeightDialog(
    dismiss: () -> Unit,
    selectCombineMethod: (CombineWeight) -> Unit,
    combineWeightState: String
) {
    Dialog(
        onDismissRequest = dismiss
    ) {
        Surface(
            shape = RoundedCornerShape(28.0.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(vertical = Spacing.big)
            ) {
                CombineWeight.values().forEach {
                    ListItem(
                        text = { Text(stringResource(id = it.settingsStringId)) },
                        modifier = Modifier.selectable(
                            selected = combineWeightState == it.name,
                            onClick = { selectCombineMethod(it) },
                        ),
                        icon = {
                            RadioButton(
                                selected = combineWeightState == it.name,
                                onClick = { selectCombineMethod(it) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.secondary,
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Preview
@Composable
fun SettingsPagePreview() {
    AppSettings(goBack = { }, goToAbout = { })
}