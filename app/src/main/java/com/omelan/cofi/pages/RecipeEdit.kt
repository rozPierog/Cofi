@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class,
)

package com.omelan.cofi.pages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.omelan.cofi.LocalPiPState
import com.omelan.cofi.R
import com.omelan.cofi.components.*
import com.omelan.cofi.share.model.*
import com.omelan.cofi.share.pages.Destinations
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.buildAnnotatedStringWithUrls
import com.omelan.cofi.utils.getDefaultPadding
import com.omelan.cofi.utils.requestFocusSafer
import kotlinx.coroutines.launch

fun NavGraphBuilder.addRecipe(
    navController: NavController,
    db: AppDatabase,
    goBack: () -> Unit = {
        navController.popBackStack()
    },
) {
    composable(Destinations.RECIPE_ADD) {
        val coroutineScope = rememberCoroutineScope()
        RecipeEdit(
            saveRecipe = { recipe, steps ->
                coroutineScope.launch {
                    val idOfRecipe = db.recipeDao().insertRecipe(recipe)
                    db.stepDao().insertAll(steps.map { it.copy(recipeId = idOfRecipe.toInt()) })
                }
                goBack()
            },
            goBack = goBack,
        )
    }
}

fun NavGraphBuilder.recipeEdit(
    navController: NavController,
    db: AppDatabase,
    goBack: () -> Unit = {
        navController.popBackStack()
    },
) {
    composable(
        Destinations.RECIPE_EDIT,
        arguments = listOf(navArgument("recipeId") { type = NavType.IntType }),
    ) { backStackEntry ->
        val recipeId = backStackEntry.arguments?.getInt("recipeId")
            ?: throw IllegalStateException("No Recipe ID")
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val recipeViewModel: RecipeViewModel = viewModel()
        val stepsViewModel: StepsViewModel = viewModel()
        val recipe by recipeViewModel.getRecipe(recipeId)
            .observeAsState(Recipe(name = "", description = ""))
        val steps by stepsViewModel.getAllStepsForRecipe(recipeId)
            .observeAsState(listOf())
        RecipeEdit(
            goBack = goBack,
            isEditing = true,
            recipeToEdit = recipe,
            stepsToEdit = steps,
            saveRecipe = { newRecipe, newSteps ->
                coroutineScope.launch {
                    db.recipeDao().updateRecipe(newRecipe)
                    db.stepDao().deleteAllStepsForRecipe(newRecipe.id)
                    db.stepDao().insertAll(newSteps)
                }
                goBack()
            },
            cloneRecipe = { newRecipe, newSteps ->
                coroutineScope.launch {
                    val idOfRecipe = db.recipeDao().insertRecipe(
                        newRecipe.copy(
                            id = 0,
                            name = context.resources.getString(
                                R.string.recipe_clone_suffix,
                                recipe.name,
                            ),
                        ),
                    )
                    db.stepDao().insertAll(
                        newSteps.map {
                            it.copy(recipeId = idOfRecipe.toInt(), id = 0)
                        },
                    )
                }
                navController.navigate("list") {
                    this.popUpTo("list") {
                        inclusive = true
                    }
                }
            },
            deleteRecipe = {
                coroutineScope.launch {
                    db.recipeDao().deleteById(recipeId = recipeId)
                    db.stepDao().deleteAllStepsForRecipe(recipeId = recipeId)
                }
                ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(recipeId.toString()))
                navController.navigate("list") {
                    this.popUpTo("list") {
                        inclusive = true
                    }
                }
            },
        )
    }
}

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
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val (appBarBehavior, collapse) = createAppBarBehaviorWithCollapse()
    val lazyListState = rememberLazyListState()
    val (nameFocusRequester, descriptionFocusRequester) = remember { FocusRequester.createRefs() }

    var isDeleteModalVisible by remember { mutableStateOf(false) }
    var isCloneModalVisible by remember { mutableStateOf(false) }
    var isSaveModalVisible by remember { mutableStateOf(false) }
    var isIconPickerVisible by remember { mutableStateOf(false) }
    var pickedIcon by remember(recipeToEdit) { mutableStateOf(recipeToEdit.recipeIcon) }

    var showDescription by remember(recipeToEdit.description) {
        mutableStateOf(recipeToEdit.description.isNotBlank())
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

    val canSave by remember(name.text, steps) {
        derivedStateOf {
            name.text.isNotBlank() && steps.isNotEmpty()
        }
    }

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

    val canSafelyExit = !(
        steps !== stepsToEdit ||
            name.text != recipeToEdit.name ||
            description.text != recipeToEdit.description ||
            pickedIcon != recipeToEdit.recipeIcon
        )

    val safeGoBack: () -> Unit = {
        if (!canSafelyExit) {
            isSaveModalVisible = true
        } else {
            goBack()
        }
    }

    BackHandler(stepWithOpenEditor != null || !canSafelyExit) {
        if (stepWithOpenEditor != null) {
            stepWithOpenEditor = null
        } else {
            safeGoBack()
        }
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
                            isIconPickerVisible = !isIconPickerVisible
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
                            if (it.isFocused) {
                                collapse()
                            }
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
            AnimatedContent(
                targetState = showDescription,
                label = "Description animated content",
            ) {
                if (!it) {
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
                        onValueChange = { newDescription -> description = newDescription },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Spacing.big)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    collapse()
                                }
                            }
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
                exit = shrinkVertically(),
            ) {
                StepListItem(
                    step = step,
                    stepProgress = StepProgress.Upcoming,
                    onClick = {
                        collapse()
                        stepWithOpenEditor = it
                    },
                )
            }
        }
        item {
            AnimatedVisibility(
                visible = stepWithOpenEditor == null,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                StepAddCard(
                    onTypeSelect = {
                        coroutineScope.launch {
                            collapse()
                        }
                    },
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
    Scaffold(
        modifier = Modifier.nestedScroll(appBarBehavior.nestedScrollConnection),
        topBar = {
            PiPAwareAppBar(
                navigationIcon = {
                    IconButton(onClick = safeGoBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { isCloneModalVisible = true }) {
                            Icon(
                                painterResource(id = R.drawable.ic_copy),
                                contentDescription = null,
                            )
                        }
                        IconButton(onClick = { isDeleteModalVisible = true }) {
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
    if (isIconPickerVisible) {
        IconPickerBottomSheet({ pickedIcon = it }, { isIconPickerVisible = false })
    }
    if (isDeleteModalVisible && isEditing) {
        DeleteDialog(onConfirm = deleteRecipe, onDismiss = { isDeleteModalVisible = false })
    }
    if (isSaveModalVisible) {
        SaveDialog(
            canSave = canSave,
            onSave = onSave,
            onDiscard = goBack,
            onDismiss = { isSaveModalVisible = false },
        )
    }
    if (isCloneModalVisible) {
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
            onDismiss = { isCloneModalVisible = false },
        )
    }

    LaunchedEffect(showDescription) {
        if (showDescription && recipeToEdit.description.isBlank()) {
            descriptionFocusRequester.requestFocusSafer()
        } else {
            nameFocusRequester.requestFocusSafer()
        }
    }
}

@Composable
private fun IconPickerBottomSheet(
    setPickedIcon: (RecipeIcon) -> Unit,
    onDismiss: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Material3BottomSheet(onDismissRequest = onDismiss) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .waterfallPadding()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.Bottom,
        ) {
            RecipeIcon.entries.map {
                val tooltipState = rememberTooltipState()
                Box(
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, maxWidth = 68.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .combinedClickable(
                            role = Role.Button,
                            onClick = {
                                onDismiss()
                                setPickedIcon(it)
                            },
                            onLongClick = {
                                coroutineScope.launch {
                                    tooltipState.show()
                                }
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    TooltipBox(
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(id = it.nameResId))
                            }
                        },
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        state = tooltipState,
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
            .imePadding()
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
            .padding(getDefaultPadding()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.normal),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = true)
                .padding(paddingValues),
        ) {
            renderNameAndDescriptionEdit()
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = true)
                .padding(paddingValues),
            state = lazyListState,
        ) {
            renderSteps()
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
