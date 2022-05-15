@file:OptIn(ExperimentalMaterialApi::class)

package com.omelan.cofi.pages.settings

import android.icu.text.DateFormat
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.omelan.cofi.*
import com.omelan.cofi.R
import com.omelan.cofi.components.Material3Dialog
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.createAppBarBehavior
import com.omelan.cofi.model.*
import com.omelan.cofi.utils.checkPiPPermission
import com.omelan.cofi.utils.getDefaultPadding
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.nio.charset.StandardCharsets
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AppSettings(goBack: () -> Unit, goToAbout: () -> Unit) {
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
    var showBackupDialog by remember { mutableStateOf(false) }
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
            contentPadding = getDefaultPadding(paddingValues = it)
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
                    text = { Text(text = "Backup") },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_save),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = { showBackupDialog = true }
                    ),
                )
                if (showBackupDialog) BackupDialog(dismiss = { showBackupDialog = false })
            }
            item {
                RestoreListItem()
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
fun RestoreListItem() {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val coroutineScope = rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it == null) {
                return@rememberLauncherForActivityResult
            }
            val contentResolver = context.contentResolver
            contentResolver.openInputStream(it).use { inputStream ->
                if (inputStream == null) return@rememberLauncherForActivityResult
                val jsonString = String(inputStream.readBytes(), StandardCharsets.UTF_8)
                val jsonArray = JSONArray(jsonString)
                coroutineScope.launch {
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val recipe = jsonObject.toRecipe()
                        val recipeId = db.recipeDao().insertRecipe(recipe)
                        val steps = jsonObject.getJSONArray(jsonSteps).toSteps(recipeId)
                        db.stepDao().insertAll(steps)
                    }
                }
            }
        }

    ListItem(
        text = { Text(text = "Restore") },
        icon = {
            Icon(
                painterResource(id = R.drawable.ic_restore),
                contentDescription = null
            )
        },
        modifier = Modifier.settingsItemModifier(
            onClick = { launcher.launch(arrayOf("application/json")) }
        ),
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BackupDialog(dismiss: () -> Unit) {
    val recipesToBackup = remember { mutableStateListOf<Recipe>() }
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val recipes by db.recipeDao().getAll().observeAsState(initial = listOf())
    LaunchedEffect(recipes) {
        if (recipesToBackup.isEmpty()) {
            recipesToBackup.addAll(recipes)
        }
    }
    val steps by db.stepDao().getAll().observeAsState(initial = listOf())
    val stepsWithRecipeId = steps.groupBy { it.recipeId }
    val launcher = rememberLauncherForActivityResult(CreateDocument("application/json")) {
        if (it == null) {
            return@rememberLauncherForActivityResult
        }
        val contentResolver = context.contentResolver
        contentResolver.openOutputStream(it).use { outputStream ->
            if (outputStream == null) return@rememberLauncherForActivityResult
            val jsonArray = JSONArray()
            recipesToBackup.forEach { recipe ->
                jsonArray.put(recipe.serialize(stepsWithRecipeId[recipe.id]))
            }
            outputStream.write(jsonArray.toString(2).toByteArray())
            outputStream.close()
        }
        dismiss()
    }

    Material3Dialog(modifier = Modifier.fillMaxSize(), onDismissRequest = dismiss, onSave = {
        val c = Calendar.getInstance().time
        val format: DateFormat =
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault())
        val formattedDate: String = format.format(c)
        launcher.launch("cofi_backup_${formattedDate}.json")
    }) {
        LazyColumn(Modifier.weight(1f, true)) {
            items(recipes) {
                val isSelected = recipesToBackup.contains(it)
                val onCheck: () -> Unit = {
                    if (isSelected) recipesToBackup.remove(it) else recipesToBackup.add(it)
                }
                ListItem(
                    text = { Text(it.name) },
                    modifier = Modifier.selectable(selected = isSelected, onClick = onCheck),
                    icon = {
                        Checkbox(checked = isSelected, onCheckedChange = { onCheck() })
                    }
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
    Material3Dialog(onDismissRequest = dismiss, onSave = {
        coroutineScope.launch {
            recipesToAdd.forEach { recipe ->
                val idOfRecipe = db.recipeDao().insertRecipe(recipe.copy(id = 0))
                val stepsOfTheRecipe =
                    steps[prepopulateData.recipes.indexOf(recipe)] ?: return@launch
                db.stepDao().insertAll(
                    stepsOfTheRecipe.map {
                        it.copy(
                            id = 0,
                            recipeId = idOfRecipe.toInt()
                        )
                    }
                )
            }
            dismiss()
        }
    }) {
        LazyColumn {
            items(prepopulateData.recipes) {
                val isSelected = recipesToAdd.contains(it)
                val onCheck: () -> Unit = {
                    if (isSelected) recipesToAdd.remove(it) else recipesToAdd.add(it)
                }
                ListItem(
                    text = { Text(it.name) },
                    modifier = Modifier.selectable(selected = isSelected, onClick = onCheck),
                    icon = {
                        Checkbox(checked = isSelected, onCheckedChange = { onCheck() })
                    }
                )
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
    Material3Dialog(onDismissRequest = dismiss) {
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


@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Preview
@Composable
fun SettingsPagePreview() {
    AppSettings(goBack = { }, goToAbout = { })
}