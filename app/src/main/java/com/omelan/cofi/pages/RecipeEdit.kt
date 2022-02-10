package com.omelan.cofi.pages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.omelan.cofi.R
import com.omelan.cofi.components.*
import com.omelan.cofi.model.Recipe
import com.omelan.cofi.model.RecipeIcon
import com.omelan.cofi.model.Step
import com.omelan.cofi.ui.*
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun RecipeEdit(
    saveRecipe: (Recipe, List<Step>) -> Unit,
    goBack: () -> Unit = {},
    stepsToEdit: List<Step> = listOf(),
    recipeToEdit: Recipe = Recipe(name = "", description = "", recipeIcon = RecipeIcon.Grinder),
    deleteRecipe: () -> Unit = {},
    cloneRecipe: (Recipe, List<Step>) -> Unit = { _, _ -> },
    isEditing: Boolean = false,
) {
    var showDeleteModal by remember { mutableStateOf(false) }
    var showCloneModal by remember { mutableStateOf(false) }
    var showSaveModal by remember { mutableStateOf(false) }
    var pickedIcon by remember(recipeToEdit) { mutableStateOf(recipeToEdit.recipeIcon) }
    var name by remember(recipeToEdit) { mutableStateOf(recipeToEdit.name) }
    var description by remember(recipeToEdit) { mutableStateOf(recipeToEdit.description) }
    var steps by remember(stepsToEdit) { mutableStateOf(stepsToEdit) }
    var stepWithOpenEditor by remember { mutableStateOf<Step?>(null) }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val textFieldColors = MaterialTheme.createTextFieldColors()
    val appBarBehavior = createAppBarBehavior()
    val textSelectionColors = MaterialTheme.createTextSelectionColors()

    val canSave = name.isNotBlank() && steps.isNotEmpty()

    val safeGoBack: () -> Unit = {
        if (steps !== stepsToEdit ||
            name != recipeToEdit.name ||
            description != recipeToEdit.description ||
            pickedIcon != recipeToEdit.recipeIcon
        ) {
            showSaveModal = true
        } else {
            goBack()
        }
    }

    BackHandler {
        if (stepWithOpenEditor != null) {
            stepWithOpenEditor = null
            return@BackHandler
        }
        safeGoBack()
    }

    fun saveRecipe() = saveRecipe(
        recipeToEdit.copy(
            name = name,
            description = description,
            recipeIcon = pickedIcon,
        ),
        steps.mapIndexed { index, step -> step.copy(orderInRecipe = index) }
    )

    fun pickIcon(icon: RecipeIcon) {
        coroutineScope.launch {
            bottomSheetScaffoldState.bottomSheetState.collapse()
            pickedIcon = icon
        }
    }
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        modifier = Modifier.nestedScroll(appBarBehavior.nestedScrollConnection),
        sheetPeekHeight = 0.dp,
        sheetElevation = 30.dp,
        sheetShape = shapes.modal,
        sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        sheetContent = {
            FlowRow(
                modifier = Modifier
                    .navigationBarsWithImePadding()
                    .fillMaxWidth()
            ) {
                RecipeIcon.values().map {
                    IconButton(
                        onClick = { pickIcon(it) },
                        modifier = Modifier
                            .fillMaxWidth(0.2F)
                            .aspectRatio(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = it.icon),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = it.name,
                        )
                    }
                }
            }
        },
        topBar = {
            PiPAwareAppBar(
                navigationIcon = {
                    IconButton(onClick = safeGoBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { showCloneModal = true }) {
                            Icon(
                                painterResource(id = R.drawable.ic_copy),
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { showDeleteModal = true }) {
                            Icon(Icons.Rounded.Delete, contentDescription = null)
                        }
                    }
                    IconButton(
                        modifier = Modifier.testTag("recipe_edit_save"),
                        onClick = { saveRecipe() },
                        enabled = canSave,
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_save),
                            contentDescription = null,
                        )
                    }
                },
                title = {
                    Text(
                        text = if (isEditing) {
                            stringResource(id = R.string.recipe_edit_title)
                        } else {
                            stringResource(id = R.string.recipe_add_new_title)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                scrollBehavior = appBarBehavior,
            )
        }
    ) {
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            BoxWithConstraints {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(color = MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(
                        bottom = maxHeight / 2,
                        top = Spacing.big,
                        start = Spacing.big,
                        end = Spacing.big,
                    ),
                ) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                                            bottomSheetScaffoldState.bottomSheetState.collapse()
                                        } else {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                        keyboardController?.hide()
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = pickedIcon.icon),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null
                                )
                            }
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("recipe_edit_name"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                                label = {
                                    Text(
                                        text = stringResource(id = R.string.recipe_edit_name)
                                    )
                                },
                                colors = textFieldColors,
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Spacing.big)
                                .testTag("recipe_edit_description"),
                            keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                            label = {
                                Text(text = stringResource(id = R.string.recipe_edit_description))
                            },
                            colors = textFieldColors,
                        )
                    }
                    itemsIndexed(
                        steps,
                        { _, step -> if (step.id == 0) step.hashCode() else step.id }
                    ) { index, step ->
                        AnimatedVisibility(
                            modifier = Modifier.animateItemPlacement(),
                            visible = stepWithOpenEditor == step,
                            enter = expandVertically(),
                            exit = shrinkVertically(),
                        ) {
                            StepAddCard(
                                stepToEdit = step,
                                save = { stepToSave ->
                                    steps = if (stepToSave == null) {
                                        steps.minus(step)
                                    } else {
                                        steps.mapIndexed { mapIndex, step ->
                                            if (index == mapIndex) {
                                                stepToSave
                                            } else {
                                                step
                                            }
                                        }
                                    }
                                    stepWithOpenEditor = null
                                },
                                isFirst = index == 0,
                                isLast = index == steps.size - 1,
                                onPositionChange = { change ->
                                    steps = steps.toMutableList().apply {
                                        add(index + change, removeAt(index))
                                    }
                                },
                                orderInRecipe = index,
                                recipeId = recipeToEdit.id,
                            )
                        }
                        AnimatedVisibility(
                            visible = stepWithOpenEditor != step,
                            enter = expandVertically(),
                            modifier = Modifier.animateItemPlacement(),
                            exit = shrinkVertically(),
                        ) {
                            StepListItem(
                                step = step,
                                stepProgress = StepProgress.Upcoming,
                                onClick = { stepWithOpenEditor = it }
                            )
                        }
                    }
                    item {
                        AnimatedVisibility(
                            visible = stepWithOpenEditor == null,
                            enter = expandVertically(),
                            modifier = Modifier.animateItemPlacement(),
                            exit = shrinkVertically(),
                        ) {
                            StepAddCard(
                                modifier = Modifier.animateItemPlacement(),
                                save = { stepToSave ->
                                    if (stepToSave != null) {
                                        steps = listOf(
                                            *steps.toTypedArray(),
                                            stepToSave
                                        )
                                    }
                                },
                                orderInRecipe = steps.size,
                                recipeId = recipeToEdit.id,
                            )
                        }
                    }
                }
            }
        }

        if (showDeleteModal && isEditing) {
            DeleteDialog(onConfirm = deleteRecipe, onDismiss = { showDeleteModal = false })
        }
        if (showSaveModal) {
            SaveDialog(
                canSave = canSave,
                onSave = { saveRecipe() },
                onDiscard = goBack,
                onDismiss = { showSaveModal = false }
            )
        }
        if (showCloneModal) {
            CloneDialog(onConfirm = {
                cloneRecipe(
                    recipeToEdit.copy(
                        name = name,
                        description = description,
                        recipeIcon = pickedIcon,
                    ),
                    steps.mapIndexed { index, step -> step.copy(orderInRecipe = index) }
                )
            }, onDismiss = { showCloneModal = false })
        }
    }
}

@Composable
fun DeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(id = R.string.button_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.button_cancel))
            }
        },
        icon = { Icon(Icons.Rounded.Delete, null) },
        title = {
            Text(text = stringResource(id = R.string.recipe_delete_title))
        },
        text = {
            Text(text = stringResource(id = R.string.recpie_delete_text))
        },
    )
}

@Composable
fun SaveDialog(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit,
    canSave: Boolean,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = if (canSave) onSave else onDismiss
            ) {
                Text(
                    stringResource(
                        if (canSave) {
                            R.string.step_add_save
                        } else {
                            R.string.button_continue_editing
                        }
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDiscard) {
                Text(text = stringResource(id = R.string.button_discard))
            }
        },
        icon = { Icon(painterResource(id = R.drawable.ic_save), null) },
        title = {
            Text(text = stringResource(id = R.string.recipe_unsaved_title))
        },
        text = {
            Text(text = stringResource(id = R.string.recipe_unsaved_text))
        },
    )
}

@Composable
fun CloneDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(id = R.string.button_copy))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.button_cancel))
            }
        },
        icon = { Icon(painterResource(id = R.drawable.ic_copy), null) },
        title = {
            Text(text = stringResource(id = R.string.recipe_clone_title))
        },
        text = {
            Text(text = stringResource(id = R.string.recpie_clone_text))
        },
    )
}

@ExperimentalAnimatedInsets
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Preview
@Composable
fun RecipeEditPreview() {
    RecipeEdit(saveRecipe = { _, _ -> })
}