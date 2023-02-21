import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.fillMaxRectangle
import com.omelan.cofi.share.*
import com.omelan.cofi.share.R
import com.omelan.cofi.share.components.StepNameText
import com.omelan.cofi.share.components.TimeText
import com.omelan.cofi.share.components.TimerValue
import com.omelan.cofi.share.timer.Timer
import com.omelan.cofi.share.timer.TimerControllers
import com.omelan.cofi.wearos.presentation.LocalAmbientModeProvider
import com.omelan.cofi.wearos.presentation.components.ListenKeyEvents
import com.omelan.cofi.wearos.presentation.components.StartButton
import kotlinx.coroutines.launch

@Composable
fun TimerPage(
    timerControllers: TimerControllers,
    allSteps: List<Step>,
    recipe: Recipe,
    dataStore: DataStore,
    weightMultiplier: Float,
    timeMultiplier: Float,
) {
    val (
        currentStep,
        isDone,
        isTimerRunning,
        _,
        animatedProgressValue,
        animatedProgressColor,
        pauseAnimations,
        _,
        startAnimations,
        changeToNextStep,
    ) = timerControllers
    val combineWeightState by dataStore.getWeightSetting()
        .collectAsState(initial = COMBINE_WEIGHT_DEFAULT_VALUE)
    val coroutineScope = rememberCoroutineScope()
    val ambientController = LocalAmbientModeProvider.current
    val isAmbient = remember {
        derivedStateOf {
            ambientController?.isAmbient ?: false
        }
    }

    val alreadyDoneWeight by Timer.rememberAlreadyDoneWeight(
        indexOfCurrentStep = allSteps.indexOf(currentStep.value),
        allSteps = allSteps,
        combineWeightState = combineWeightState,
        weightMultiplier = weightMultiplier,
    )
    val startButtonOnClick: () -> Unit = {
        if (currentStep.value != null) {
            if (animatedProgressValue.isRunning) {
                coroutineScope.launch { pauseAnimations() }
            } else {
                coroutineScope.launch {
                    if (currentStep.value?.time == null) {
                        changeToNextStep(false)
                    } else {
                        startAnimations()
                    }
                }
            }
        } else
            coroutineScope.launch { changeToNextStep(true) }
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            progress = if (isDone) 1f else animatedProgressValue.value,
            indicatorColor = if (isAmbient.value) {
                Color.White
            } else {
                animatedProgressColor.value
            },
            trackColor = if (isAmbient.value) {
                MaterialTheme.colors.onBackground.copy(alpha = 0.1f)
            } else {
                animatedProgressColor.value.copy(alpha = 0.2f)
            },
            startAngle = 300f,
            endAngle = 240f,
        )
        Column(
            modifier = Modifier
                .fillMaxRectangle()
                .animateContentSize(),
            Arrangement.SpaceBetween,
            Alignment.CenterHorizontally,
        ) {
            AnimatedVisibility(visible = isDone, enter = fadeIn(), exit = fadeOut()) {
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
            AnimatedVisibility(
                visible = currentStep.value == null && !isDone,
                modifier = Modifier.weight(1f, true),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = recipe.name,
                        color = MaterialTheme.colors.onSurface,
                        maxLines = 2,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.title1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            AnimatedVisibility(
                visible = currentStep.value != null && !isDone,
            ) {
                Column {
                    if (currentStep.value != null) {
                        TimeText(
                            currentStep = currentStep.value!!,
                            animatedProgressValue = animatedProgressValue.value * timeMultiplier,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 2,
                            style = MaterialTheme.typography.title2,
                            paddingHorizontal = 2.dp,
                            showMillis = false,
                        )
                        StepNameText(
                            currentStep = currentStep.value!!,
                            color = MaterialTheme.colors.onSurface,
                            style = MaterialTheme.typography.title3,
                            maxLines = 1,
                            paddingHorizontal = 2.dp,
                        )
                        TimerValue(
                            currentStep = currentStep.value!!,
                            animatedProgressValue = animatedProgressValue.value,
                            weightMultiplier = weightMultiplier,
                            alreadyDoneWeight = alreadyDoneWeight,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 1,
                            style = MaterialTheme.typography.title1,
                        )
                    }
                }
            }
            AnimatedVisibility(visible = !isAmbient.value) {
                Spacer(Modifier.height(12.dp))
                StartButton(isTimerRunning, startButtonOnClick)
            }
        }
    }
}
