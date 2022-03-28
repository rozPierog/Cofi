package com.omelan.cofi.pages

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.*
import com.omelan.cofi.*
import com.omelan.cofi.R
import com.omelan.cofi.components.*
import com.omelan.cofi.model.*
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.Haptics
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetails(
    recipeId: Int,
    isInPiP: Boolean = LocalPiPState.current,
    onRecipeEnd: (Recipe) -> Unit = {},
    goToEdit: () -> Unit = {},
    goBack: () -> Unit = {},
    onTimerRunning: (Boolean) -> Unit = {},
    stepsViewModel: StepsViewModel = viewModel(),
    recipeViewModel: RecipeViewModel = viewModel(),
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    var currentStep by remember { mutableStateOf<Step?>(null) }
    var isDone by remember { mutableStateOf(false) }
    var showAutomateLinkDialog by remember { mutableStateOf(false) }
    val steps by stepsViewModel.getAllStepsForRecipe(recipeId).observeAsState(listOf())
    val recipe by recipeViewModel.getRecipe(recipeId)
        .observeAsState(Recipe(name = "", description = ""))
    val indexOfCurrentStep = steps.indexOf(currentStep)
    val indexOfLastStep = steps.lastIndex
    val animatedProgressValue = remember { Animatable(0f) }
    val animatedProgressColor = remember { Animatable(Color.DarkGray) }
    val clipboardManager = LocalClipboardManager.current
    val snackbarState = SnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarMessage = stringResource(id = R.string.snackbar_copied)
    val lazyListState = rememberLazyListState()
    val dataStore = DataStore(context)

    val isDingEnabled by dataStore.getStepChangeSetting()
        .collectAsState(initial = DING_DEFAULT_VALUE)
    val combineWeightState by dataStore.getWeightSetting()
        .collectAsState(initial = COMBINE_WEIGHT_DEFAULT_VALUE)

    val alreadyDoneWeight = remember(combineWeightState, currentStep) {
        val doneSteps = if (indexOfCurrentStep == -1) {
            listOf()
        } else {
            steps.subList(0, indexOfCurrentStep)
        }
        return@remember when (combineWeightState) {
            CombineWeight.ALL.name -> doneSteps.sumOf { it.value ?: 0 }
            CombineWeight.WATER.name -> doneSteps.sumOf {
                if (it.type === StepType.WATER) {
                    it.value ?: 0
                } else {
                    0
                }
            }
            CombineWeight.NONE.name -> 0
            else -> 0
        }
    }

    fun copyAutomateLink() {
        clipboardManager.setText(AnnotatedString(text = "$appDeepLinkUrl/recipe/$recipeId"))
        coroutineScope.launch {
            snackbarState.showSnackbar(message = snackbarMessage)
        }
    }

    suspend fun pauseAnimations() {
        animatedProgressColor.stop()
        animatedProgressValue.stop()
        onTimerRunning(false)
    }

    val haptics = Haptics(context)
    val mediaPlayer = MediaPlayer.create(context, R.raw.ding)
    suspend fun changeToNextStep(silent: Boolean = false) {
        animatedProgressValue.snapTo(0f)
        if (indexOfCurrentStep != indexOfLastStep) {
            currentStep = steps[indexOfCurrentStep + 1]
        } else {
            currentStep = null
            onTimerRunning(false)
            isDone = true
            onRecipeEnd(recipe)
        }
        if (!silent && isDingEnabled) {
            haptics.progress()
            mediaPlayer.start()
        }
    }

    lifecycle.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_START) {

        } else if (event == Lifecycle.Event.ON_STOP) {
            val pendingIntent: PendingIntent =
                Intent(context, MainActivity::class.java).let { notificationIntent ->
                    PendingIntent.getActivity(
                        context,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }

            val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, "background_timer")
                    .setContentTitle(recipe.name)
                    .setContentText(currentStep?.name)
                    .setSmallIcon(recipe.recipeIcon.icon)
                    .setContentIntent(pendingIntent)
                    .setProgress(
                        currentStep?.time ?: 0,
                        animatedProgressValue.value.roundToInt(),
                        false
                    )
                    .build()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            context.applicationContext.startForeground(1, notification)
        }
    })

    suspend fun progressAnimation() {
        val safeCurrentStep = currentStep ?: return
        isDone = false
        onTimerRunning(true)
        val duration =
            (safeCurrentStep.time - (safeCurrentStep.time * animatedProgressValue.value)).toInt()
        coroutineScope.launch {
            animatedProgressColor.animateTo(
                targetValue = safeCurrentStep.type.color,
                animationSpec = tween(durationMillis = duration, easing = LinearEasing),
            )
        }
        val result = animatedProgressValue.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = duration, easing = LinearEasing),
        )
        if (result.endReason != AnimationEndReason.Finished) {
            return
        }
        changeToNextStep()
    }

    suspend fun startAnimations() {
        coroutineScope.launch {
            progressAnimation()
        }
    }
    LaunchedEffect(currentStep) {
        progressAnimation()
    }
    val appBarBehavior = createAppBarBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(appBarBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarState,
                modifier = Modifier.padding(Spacing.medium)
            ) {
                Snackbar(
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = it.visuals.message)
                }
            }
        },
        topBar = {
            PiPAwareAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = recipe.recipeIcon.icon),
                            contentDescription = null,
                            modifier = Modifier.padding(end = Spacing.small)
                        )
                        Text(
                            text = recipe.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showAutomateLinkDialog = true }) {
                        Icon(
                            painterResource(id = R.drawable.ic_link),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = goToEdit) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
                    }
                },
                scrollBehavior = appBarBehavior,
            )
        },
        floatingActionButton = {
            if (!isInPiP) {
                StartFAB(
                    isAnimationRunning = animatedProgressValue.isRunning,
                    onClick =
                    if (currentStep != null) {
                        if (animatedProgressValue.isRunning) {
                            { coroutineScope.launch { pauseAnimations() } }
                        } else {
                            { coroutineScope.launch { startAnimations() } }
                        }
                    } else {
                        {
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(
                                    if (recipe.description.isNotBlank()) 1 else 0
                                )
                                changeToNextStep(silent = true)
                            }
                        }
                    },
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = lazyListState,
            contentPadding = if (isInPiP) {
                PaddingValues(0.dp)
            } else {
                rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.navigationBars,
                    additionalStart = Spacing.big,
                    additionalTop = Spacing.big,
                    additionalEnd = Spacing.big,
                    additionalBottom = 112.0.dp,
                )
            },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!isInPiP && recipe.description.isNotBlank()) {
                item {
                    Description(
                        modifier = Modifier.fillMaxWidth(),
                        descriptionText = recipe.description
                    )
                }
            }
            if (!isInPiP) {
                item {
                    Spacer(modifier = Modifier.height(Spacing.big))
                }
            }
            item {
                Timer(
                    currentStep = currentStep,
                    animatedProgressValue = animatedProgressValue,
                    animatedProgressColor = animatedProgressColor,
                    isInPiP = isInPiP,
                    alreadyDoneWeight = alreadyDoneWeight,
                    isDone = isDone,
                )
            }
            item {
                if (!isInPiP) {
                    Spacer(modifier = Modifier.height(Spacing.big))
                }
            }
            if (!isInPiP) {
                itemsIndexed(
                    items = steps,
                    key = { _, step -> step.id }
                ) { index, step ->
                    val stepProgress = when {
                        index < indexOfCurrentStep -> StepProgress.Done
                        indexOfCurrentStep == index -> StepProgress.Current
                        else -> StepProgress.Upcoming
                    }
                    StepListItem(step = step, stepProgress = stepProgress)
                    Divider(color = Color(0xFFE8EAF6))
                }
            }
        }
        if (showAutomateLinkDialog) {
            DirectLinkDialog(
                dismiss = { showAutomateLinkDialog = false },
                onConfirm = {
                    copyAutomateLink()
                    showAutomateLinkDialog = false
                }
            )
        }
    }
}

@Composable
fun DirectLinkDialog(dismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.button_copy))
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(text = stringResource(id = R.string.button_cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_link),
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(R.string.recipe_details_automation_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.recipe_details_automation_dialog_text))
        },
    )
}

@Composable
fun StartFAB(isAnimationRunning: Boolean, onClick: () -> Unit) {
    val fabShape by animateDpAsState(
        targetValue = if (isAnimationRunning) 28.0.dp else 100.dp,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
    )
    LargeFloatingActionButton(
        shape = RoundedCornerShape(fabShape),
        onClick = onClick,
        modifier = Modifier.navigationBarsPadding(),
    ) {
        Icon(
            painter = if (isAnimationRunning) {
                painterResource(id = R.drawable.ic_pause)
            } else {
                painterResource(id = R.drawable.ic_play_arrow)
            },
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
            contentDescription = null,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailsPreview() {
    RecipeDetails(recipeId = 1, isInPiP = false)
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailsPreviewPip() {
    RecipeDetails(recipeId = 1, isInPiP = true)
}