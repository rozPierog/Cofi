@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
)

package com.omelan.cofi.pages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.omelan.cofi.LocalPiPState
import com.omelan.cofi.R
import com.omelan.cofi.components.*
import com.omelan.cofi.share.Recipe
import com.omelan.cofi.share.RecipeIcon
import com.omelan.cofi.share.Step
import com.omelan.cofi.ui.*
import com.omelan.cofi.utils.buildAnnotatedStringWithUrls
import com.omelan.cofi.utils.getDefaultPadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecipeEdit(
    saveRecipe: (Recipe, List<Step>) -> Unit,
    goBack: () -> Unit = {},
    stepsToEdit: List<Step> = listOf(),
    recipeToEdit: Recipe = Recipe(name = "", description = "", recipeIcon = RecipeIcon.Grinder),
    deleteRecipe: () -> Unit = {},
    cloneRecipe: (Recipe, List<Step>) -> Unit = { _, _ -> },
    isEditing: Boolean = false,
    windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(
        DpSize(1920.dp, 1080.dp),
    ),
) {
    var showDeleteModal by remember { mutableStateOf(false) }
    var showCloneModal by remember { mutableStateOf(false) }
    var showSaveModal by remember { mutableStateOf(false) }
    var showDescription by remember(recipeToEdit.description) {
        mutableStateOf(recipeToEdit.description.isNotBlank())
    }
    var pickedIcon by remember(recipeToEdit) { mutableStateOf(recipeToEdit.recipeIcon) }
    val tooltipStates = remember {
        RecipeIcon.values().associateWith { TooltipState() }
    }
    var name by remember(recipeToEdit) {
        mutableStateOf(
            TextFieldValue(
                recipeToEdit.name,
                TextRange(recipeToEdit.name.length),
            ),
        )
    }
    var description by remember(recipeToEdit) {
        mutableStateOf(
            TextFieldValue(
                recipeToEdit.description,
                TextRange(recipeToEdit.description.length),
            ),
        )
    }
    var steps by remember(stepsToEdit) { mutableStateOf(stepsToEdit) }
    var stepWithOpenEditor by remember { mutableStateOf<Step?>(null) }

    val bottomSheetScaffoldState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val (appBarBehavior, collapse) = createAppBarBehaviorWithCollapse()
    val lazyListState = rememberLazyListState()
    val textSelectionColors = MaterialTheme.createTextSelectionColors()
    val (nameFocusRequester, descriptionFocusRequester) = remember { FocusRequester.createRefs() }

    val canSave = name.text.isNotBlank() && steps.isNotEmpty()
    val configuration = LocalConfiguration.current

    val isPhoneLayout by remember(
        windowSizeClass.widthSizeClass,
        configuration.screenHeightDp,
        configuration.screenWidthDp,
    ) {
        derivedStateOf {
            windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
                    (configuration.screenHeightDp > configuration.screenWidthDp)
        }
    }

    val safeGoBack: () -> Unit = {
        if (steps !== stepsToEdit ||
            name.text != recipeToEdit.name ||
            description.text != recipeToEdit.description ||
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
        if (bottomSheetScaffoldState.isVisible) {
            coroutineScope.launch {
                bottomSheetScaffoldState.hide()
            }
            return@BackHandler
        }
        safeGoBack()
    }

    val onSave: () -> Unit = {
        saveRecipe(
            recipeToEdit.copy(
                name = name.text,
                description = description.text,
                recipeIcon = pickedIcon,
            ),
            steps.mapIndexed { index, step -> step.copy(orderInRecipe = index) },
        )
    }

    fun pickIcon(icon: RecipeIcon) {
        coroutineScope.launch {
            bottomSheetScaffoldState.hide()
            pickedIcon = icon
        }
    }

    LaunchedEffect(Unit) {
        collapse()
        delay(100)
        try {
//            nameFocusRequester.requestFocus()
        } catch (e: IllegalStateException) {
            // do nothing - it's not vital part of the app, just QoL feature
        }
    }
    LaunchedEffect(showDescription) {
        if (showDescription && recipeToEdit.description.isBlank()) {
            descriptionFocusRequester.requestFocus()
        } else {
            delay(100)
//            nameFocusRequester.requestFocus()
        }
    }
    val renderNameAndDescriptionEdit: LazyListScope.() -> Unit = {
        item {
            Row(verticalAlignment = Alignment.Bottom) {
                OutlinedIconButton(
                    modifier = Modifier
                        .padding(end = Spacing.normal)
                        .defaultMinSize(
                            minHeight = TextFieldDefaults.MinHeight,
                            minWidth = TextFieldDefaults.MinHeight,
                        ),
                    shape = ShapeDefaults.ExtraSmall,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    onClick = {
                        keyboardController?.hide()
                        coroutineScope.launch {
                            if (bottomSheetScaffoldState.isVisible) {
                                bottomSheetScaffoldState.hide()
                            } else {
                                bottomSheetScaffoldState.show()
                            }
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(id = pickedIcon.icon),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null,
                    )
                }
                var nameHasFocus by remember { mutableStateOf(true) }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    isError = name.text.isBlank() && !nameHasFocus,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(nameFocusRequester)
                        .onFocusChanged {
                            nameHasFocus = it.isFocused
                        }
                        .testTag("recipe_edit_name"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                    label = { Text(stringResource(id = R.string.recipe_edit_name)) },
                )
            }
        }
        item {
            val linkColor = MaterialTheme.colorScheme.secondary
            AnimatedContent(targetState = showDescription) {
                if (!showDescription) {
                    TextButton(
                        modifier = Modifier.testTag("recipe_edit_description_button"),
                        onClick = { showDescription = !showDescription },
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "",
                        )
                        Spacer(modifier = Modifier.size(Spacing.small))
                        Text(text = stringResource(id = R.string.recipe_edit_description_button))
                    }
                } else {
                    OutlinedTextField(
                        value = description,
                        visualTransformation = {
                            TransformedText(
                                buildAnnotatedStringWithUrls(description.text, linkColor),
                                OffsetMapping.Identity,
                            )
                        },
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Spacing.big)
                            .focusRequester(descriptionFocusRequester)
                            .testTag("recipe_edit_description"),
                        keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                        label = { Text(stringResource(id = R.string.recipe_edit_description)) },
                    )
                }
            }
        }
    }

    val renderSteps: LazyListScope.() -> Unit = {
        itemsIndexed(
            steps,
            { _, step -> if (step.id == 0) step.hashCode() else step.id },
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
                    onClick = { stepWithOpenEditor = it },
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
                    onTypeSelect = {
                        coroutineScope.launch {
                            collapse()
                        }
                    },
                    modifier = Modifier.animateItemPlacement(),
                    save = { stepToSave ->
                        if (stepToSave != null) {
                            steps = steps.toMutableList().apply { add(stepToSave) }
                        }
                    },
                    orderInRecipe = steps.size,
                    recipeId = recipeToEdit.id,
                )
            }
        }
    }
    val iconScrollState = rememberScrollState()
    ModalBottomSheetLayout(
        sheetShape = shapes.modal,
        sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        sheetState = bottomSheetScaffoldState,
        sheetContent = {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .verticalScroll(iconScrollState)
                    .imePadding()
                    .padding(getDefaultPadding(skipNavigationBarPadding = true)),
                lastLineMainAxisAlignment = FlowMainAxisAlignment.Center,
                mainAxisAlignment = FlowMainAxisAlignment.Center,
                crossAxisAlignment = FlowCrossAxisAlignment.Center,
                mainAxisSpacing = Spacing.big,
                crossAxisSpacing = Spacing.big,
                ) {
                RecipeIcon.values().map {
                    PlainTooltipBox(tooltip = { Text(stringResource(id = it.nameResId)) }) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .clickable(role = Role.Button) { pickIcon(it) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = it.icon),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = it.name,
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(appBarBehavior.nestedScrollConnection),
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
                                    contentDescription = null,
                                )
                            }
                            IconButton(onClick = { showDeleteModal = true }) {
                                Icon(
                                    painterResource(id = R.drawable.ic_delete),
                                    contentDescription = null,
                                )
                            }
                        }
                        IconButton(
                            modifier = Modifier.testTag("recipe_edit_save"),
                            onClick = onSave,
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
            },
        ) {
            CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
                BoxWithConstraints {
                    if (isPhoneLayout) {
                        PhoneLayout(
                            it,
                            lazyListState,
                            renderNameAndDescriptionEdit,
                            renderSteps,
                        )
                    } else {
                        TabletLayout(
                            it,
                            lazyListState,
                            renderNameAndDescriptionEdit,
                            renderSteps,
                        )
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
                onSave = onSave,
                onDiscard = goBack,
                onDismiss = { showSaveModal = false },
            )
        }
        if (showCloneModal) {
            CloneDialog(
                onConfirm = {
                    cloneRecipe(
                        recipeToEdit.copy(
                            name = name.text,
                            description = description.text,
                            recipeIcon = pickedIcon,
                        ),
                        steps.mapIndexed { index, step -> step.copy(orderInRecipe = index) },
                    )
                },
                onDismiss = { showCloneModal = false },
            )
        }
    }
}

@Composable
private fun PhoneLayout(
    paddingValues: PaddingValues,
    lazyListState: LazyListState,
    renderNameAndDescriptionEdit: LazyListScope.() -> Unit,
    renderSteps: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        state = lazyListState,
        contentPadding = getDefaultPadding(
            paddingValues = paddingValues,
        ),
    ) {
        renderNameAndDescriptionEdit()
        renderSteps()
        item {
            Spacer(
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding(),
            )
        }
    }
}

@Composable
private fun TabletLayout(
    paddingValues: PaddingValues,
    lazyListState: LazyListState,
    renderNameAndDescriptionEdit: LazyListScope.() -> Unit,
    renderSteps: LazyListScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(getDefaultPadding(paddingValues)),
        horizontalArrangement = Arrangement.spacedBy(Spacing.normal),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f, fill = true),
        ) {
            renderNameAndDescriptionEdit()
            item {
                Spacer(
                    modifier = Modifier
                        .imePadding()
                        .navigationBarsPadding(),
                )
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f, fill = true),
            state = lazyListState,
        ) {
            renderSteps()
            item {
                Spacer(
                    modifier = Modifier
                        .imePadding()
                        .navigationBarsPadding(),
                )
            }
        }
    }
}

@Composable
private fun DeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
            ) {
                Text(text = stringResource(id = R.string.button_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.button_cancel))
            }
        },
        icon = { Icon(painterResource(id = R.drawable.ic_delete), null) },
        title = {
            Text(text = stringResource(id = R.string.recipe_delete_title))
        },
        text = {
            Text(text = stringResource(id = R.string.recipe_delete_text))
        },
    )
}

@Composable
private fun SaveDialog(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit,
    canSave: Boolean,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = if (canSave) onSave else onDismiss,
            ) {
                Text(
                    stringResource(
                        if (canSave) {
                            R.string.step_add_save
                        } else {
                            R.string.button_continue_editing
                        },
                    ),
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
private fun CloneDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
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
            Text(text = stringResource(id = R.string.recipe_clone_text))
        },
    )
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Preview
@Composable
private fun RecipeAddPreview() {
    CofiTheme {
        CompositionLocalProvider(
            LocalPiPState provides false,
        ) {
            RecipeEdit(saveRecipe = { _, _ -> })
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Preview
@Composable
private fun RecipeEditPreview() {
    CofiTheme {
        CompositionLocalProvider(
            LocalPiPState provides false,
        ) {
            RecipeEdit(saveRecipe = { _, _ -> }, isEditing = true)
        }
    }
}
