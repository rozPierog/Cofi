package com.omelan.cofi.wearos.presentation.pages.details

import android.view.KeyEvent
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.compose.layout.fillMaxRectangle
import com.omelan.cofi.share.R
import com.omelan.cofi.share.components.TimeText
import com.omelan.cofi.share.components.TimerValue
import com.omelan.cofi.share.model.Recipe
import com.omelan.cofi.share.timer.TimerControllers
import com.omelan.cofi.share.utils.toStringShort
import com.omelan.cofi.wearos.presentation.components.ListenKeyEvents
import com.omelan.cofi.wearos.presentation.components.StartFAB
import kotlinx.coroutines.launch

@Composable
fun TimerPage(
    timerControllers: TimerControllers,
    recipe: Recipe,
    weightMultiplier: Float,
    timeMultiplier: Float,
) {

    val (
        animationControllers,
        currentStep,
        _,
        _,
        changeToNextStep,
        isDone,
        isTimerRunning,
        alreadyDoneWeight,
    ) = timerControllers
    val (
        animatedProgressValue,
        animatedProgressColor,
        pauseAnimations,
        _,
        resumeAnimations,
    ) = animationControllers
    var showDescriptionDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val startButtonOnClick: () -> Unit = {
        if (currentStep != null) {
            if (animatedProgressValue.isRunning) {
                coroutineScope.launch { pauseAnimations() }
            } else {
                coroutineScope.launch {
                    if (currentStep.time == null) {
                        changeToNextStep(false)
                    } else {
                        resumeAnimations()
                    }
                }
            }
        } else coroutineScope.launch { changeToNextStep(true) }
    }

    val recipeDescriptionScrollState =
        androidx.wear.compose.foundation.lazy.rememberScalingLazyListState(
            initialCenterItemIndex = 0,
            initialCenterItemScrollOffset = 0,
        )
    val focusRequester = remember { FocusRequester() }
    val animatedBackgroundRadius by animateFloatAsState(
        targetValue = if (isDone) 200f else 1f,
        label = "background animation",
        animationSpec = tween(500, easing = FastOutSlowInEasing),
    )

    LaunchedEffect(showDescriptionDialog) {
        if (showDescriptionDialog) {
            focusRequester.requestFocus()
        }
    }
    ListenKeyEvents { keyCode, event ->
        if (event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_STEM_1,
                KeyEvent.KEYCODE_STEM_2,
                KeyEvent.KEYCODE_STEM_3,
                -> {
                    startButtonOnClick()
                    true
                }

                else -> false
            }
        } else {
            false
        }
    }
    Dialog(
        showDialog = showDescriptionDialog,
        onDismissRequest = { showDescriptionDialog = false },
    ) {
        Alert(
            modifier = Modifier
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        recipeDescriptionScrollState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable(),
            scrollState = recipeDescriptionScrollState,
            contentPadding = PaddingValues(18.dp),
            icon = {
                Icon(
                    painter = painterResource(id = recipe.recipeIcon.icon),
                    contentDescription = "",
                )
            },
            title = { Text(text = recipe.name) },
        ) {
            item {
                Text(text = recipe.description)
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
    AmbientAware(isTimerRunning) { ambientStateUpdate ->
        val isAmbient = ambientStateUpdate.ambientState is AmbientState.Ambient

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(
                modifier = Modifier,
                onDraw = {
                    val radialGradient = Brush.radialGradient(
                        center = Offset(0f, 0f),
                        radius = animatedBackgroundRadius,
                        colors = listOf(Color.DarkGray, Color.Transparent),
                        tileMode = TileMode.Clamp,
                    )
                    drawCircle(
                        center = Offset(0f, 0f),
                        radius = animatedBackgroundRadius,
                        brush = radialGradient,
                    )
                },
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                progress = if (isDone) 1f else animatedProgressValue.value,
                indicatorColor = if (isAmbient) {
                    Color.White
                } else {
                    animatedProgressColor.value
                },
                trackColor = if (isAmbient) {
                    MaterialTheme.colors.onBackground.copy(alpha = 0.1f)
                } else {
                    animatedProgressColor.value.copy(alpha = 0.2f)
                },
                startAngle = 300f,
                endAngle = 240f,
                strokeWidth = 5.dp,
            )
            Column(
                modifier = Modifier.fillMaxRectangle(),
                Arrangement.SpaceBetween,
                Alignment.CenterHorizontally,
            ) {
                AnimatedContent(
                    targetState = Pair(currentStep, isDone), label = "Timer Content",
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.animateContentSize(),
                ) { (currentStep, isDone) ->
                    when {
                        isDone -> {
                            Column(
                                modifier = Modifier.animateContentSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.timer_enjoy),
                                    color = MaterialTheme.colors.onSurface,
                                    maxLines = 2,
                                    style = MaterialTheme.typography.title1,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_coffee),
                                    contentDescription = "",
                                )
                            }
                        }

                        currentStep != null -> {
                            Column {
                                TimeText(
                                    currentStep = currentStep,
                                    animatedProgressValue = animatedProgressValue.value * timeMultiplier,
                                    color = MaterialTheme.colors.onSurface,
                                    maxLines = 2,
                                    style = MaterialTheme.typography.title2,
                                    paddingHorizontal = 2.dp,
                                    showMillis = false,
                                )
                                AnimatedContent(currentStep, label = "Step label") {
                                    Text(
                                        text = if (it.time != null) {
                                            stringResource(
                                                id = R.string.timer_step_name_time,
                                                it.name,
                                                ((it.time!! * timeMultiplier) / 1000).toStringShort(),
                                            )
                                        } else {
                                            it.name
                                        },
                                        color = MaterialTheme.colors.onSurface,
                                        style = MaterialTheme.typography.title3.copy(
                                            textMotion = TextMotion.Animated,
                                        ),
                                        textAlign = TextAlign.Center,
                                        maxLines = if (it.time != null) {
                                            1
                                        } else {
                                            2
                                        },
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.testTag("timer_name"),
                                    )
                                }
                                TimerValue(
                                    currentStep = currentStep,
                                    animatedProgressValue = animatedProgressValue.value,
                                    weightMultiplier = weightMultiplier,
                                    alreadyDoneWeight = alreadyDoneWeight,
                                    color = MaterialTheme.colors.onSurface,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.title1,
                                )
                            }
                        }

                        else -> {
                            Box(contentAlignment = Alignment.Center) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = recipe.name,
                                        color = MaterialTheme.colors.onSurface,
                                        maxLines = if (recipe.description.isNotBlank()) 1 else 2,
                                        textAlign = TextAlign.Center,
                                        style = if (recipe.description.isNotBlank())
                                            MaterialTheme.typography.title2 else
                                            MaterialTheme.typography.title1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    if (recipe.description.isNotBlank()) {
                                        val sizeOfText = with(LocalDensity.current) {
                                            val fontScale = this.fontScale
                                            val textSize = 14 / fontScale
                                            textSize.sp
                                        }
                                        OutlinedButton(
                                            onClick = { showDescriptionDialog = true },
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .height(ButtonDefaults.ExtraSmallButtonSize)
                                                .fillMaxWidth(),
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.recipe_details_read_description),
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                fontSize = sizeOfText,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    modifier = Modifier.animateContentSize(),
                    visible = !isAmbient, enter = fadeIn(), exit = fadeOut(),
                ) {
                    Spacer(Modifier.height(12.dp))
                    StartFAB(isTimerRunning, startButtonOnClick)
                }
            }
        }
    }
}
