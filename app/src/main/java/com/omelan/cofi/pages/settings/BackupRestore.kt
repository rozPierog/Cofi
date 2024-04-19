@file:OptIn(ExperimentalMaterial3Api::class)

package com.omelan.cofi.pages.settings

import android.icu.text.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.components.Material3Dialog
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.createAppBarBehavior
import com.omelan.cofi.share.model.*
import com.omelan.cofi.utils.getDefaultPadding
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.nio.charset.StandardCharsets
import java.util.Calendar
import java.util.Locale

@Composable
fun BackupRestoreSettings(goBack: () -> Unit, goToRoot: () -> Unit) {
    val context = LocalContext.current
    val snackbarState = remember { SnackbarHostState() }
    var showDefaultRecipeDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val appBarBehavior = createAppBarBehavior()
    Scaffold(
        topBar = {
            PiPAwareAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_backup_item),
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
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarState,
                modifier = Modifier.padding(getDefaultPadding()),
            ) {
                Snackbar(
                    action = {
                        it.visuals.actionLabel?.let { label ->
                            TextButton(onClick = goToRoot) {
                                Text(text = label)
                            }
                        }
                    },
                ) {
                    Text(text = it.visuals.message)
                }
            }
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
                        Text(text = stringResource(id = R.string.settings_addDefault))
                    },
                    leadingContent = { Icon(Icons.Rounded.AddCircle, contentDescription = null) },
                    modifier = Modifier.settingsItemModifier(
                        onClick = { showDefaultRecipeDialog = true },
                    ),
                )
                if (showDefaultRecipeDialog) {
                    DefaultRecipesDialog(dismiss = { showDefaultRecipeDialog = false })
                }
            }
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_backup))
                    },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_save),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.settingsItemModifier(
                        onClick = { showBackupDialog = true },
                    ),
                )
                if (showBackupDialog) {
                    BackupDialog(
                        dismiss = { showBackupDialog = false },
                        afterBackup = { numberOfBackedUp ->
                            coroutineScope.launch {
                                snackbarState.showSnackbar(
                                    context.resources.getQuantityString(
                                        R.plurals.settings_snackbar_backup,
                                        numberOfBackedUp,
                                        numberOfBackedUp,
                                    ),
                                )
                            }
                        },
                    )
                }
            }
            item {
                RestoreListItem(
                    afterRestore = { numberOfRestored ->
                        coroutineScope.launch {
                            snackbarState.showSnackbar(
                                context.resources.getQuantityString(
                                    R.plurals.settings_snackbar_restore,
                                    numberOfRestored,
                                    numberOfRestored,
                                ),
                                actionLabel = context.resources.getString(R.string.button_show),
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun RestoreListItem(afterRestore: (numberOfRestored: Int) -> Unit) {
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
                        val steps = jsonObject.getJSONArray(jsonSteps).toSteps(recipeId = recipeId)
                        db.stepDao().insertAll(steps)
                    }
                }
                afterRestore(jsonArray.length())
            }
        }

    ListItem(
        headlineContent = { Text(text = stringResource(id = R.string.settings_restore)) },
        leadingContent = {
            Icon(
                painterResource(id = R.drawable.ic_restore),
                contentDescription = null,
            )
        },
        modifier = Modifier.settingsItemModifier(
            onClick = { launcher.launch(arrayOf("application/json")) },
        ),
    )
}

@Composable
fun BackupDialog(dismiss: () -> Unit, afterBackup: (numberOfBackups: Int) -> Unit) {
    val recipesToBackup = remember { mutableStateListOf<Recipe>() }
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val recipes by db.recipeDao().getAll().observeAsState(initial = emptyList())
    LaunchedEffect(recipes.isEmpty()) {
        if (recipesToBackup.isEmpty()) {
            recipesToBackup.addAll(recipes)
        }
    }
    val steps by db.stepDao().getAll().observeAsState(initial = listOf())
    if (steps.isEmpty()) {
        return
    }
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
        afterBackup(recipesToBackup.size)
        dismiss()
    }

    Material3Dialog(
        onDismissRequest = dismiss,
        onSave = {
            val c = Calendar.getInstance().time
            val format: DateFormat =
                DateFormat.getDateTimeInstance(
                    DateFormat.SHORT,
                    DateFormat.SHORT,
                    Locale.getDefault(),
                )
            val formattedDate: String = format.format(c)
            launcher.launch("cofi_backup_$formattedDate.json")
        },
        title = { Text(text = stringResource(id = R.string.settings_backup)) },
    ) {
        HorizontalDivider()
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(recipes) {
                val isSelected = recipesToBackup.contains(it)
                val onCheck: () -> Unit = {
                    if (isSelected) recipesToBackup.remove(it) else recipesToBackup.add(it)
                }
                ListItem(
                    headlineContent = { Text(it.name) },
                    modifier = Modifier
                        .selectable(selected = isSelected, onClick = onCheck),
                    leadingContent = {
                        Checkbox(checked = isSelected, onCheckedChange = { onCheck() })
                    },
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun DefaultRecipesDialog(dismiss: () -> Unit) {
    val recipesToAdd = remember { mutableStateListOf<Recipe>() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val prepopulateData = PrepopulateData(context)
    val steps = prepopulateData.steps.groupBy { it.recipeId }
    val db = AppDatabase.getInstance(context)
    Material3Dialog(
        onDismissRequest = dismiss,
        onSave = {
            recipesToAdd.forEach { recipe ->
                coroutineScope.launch {
                    val idOfRecipe = db.recipeDao().insertRecipe(recipe.copy(id = 0))
                    val stepsOfTheRecipe = steps[recipe.id] ?: return@launch
                    db.stepDao().insertAll(
                        stepsOfTheRecipe.map {
                            it.copy(id = 0, recipeId = idOfRecipe.toInt())
                        },
                    )
                }
                dismiss()
            }
        },
        title = { Text(text = stringResource(id = R.string.settings_addDefault)) },
    ) {
        HorizontalDivider()
        LazyColumn {
            items(prepopulateData.recipes) {
                val isSelected = recipesToAdd.contains(it)
                val onCheck: () -> Unit = {
                    if (isSelected) recipesToAdd.remove(it) else recipesToAdd.add(it)
                }
                ListItem(
                    headlineContent = { Text(it.name) },
                    modifier = Modifier.selectable(selected = isSelected, onClick = onCheck),
                    leadingContent = {
                        Checkbox(checked = isSelected, onCheckedChange = { onCheck() })
                    },
                )
            }
        }
        HorizontalDivider()
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun BackupRestoreSettingsPreview() {
    BackupRestoreSettings(goBack = {}, goToRoot = {})
}
