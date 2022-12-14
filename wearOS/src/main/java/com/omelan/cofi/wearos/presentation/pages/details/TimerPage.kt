
import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.omelan.cofi.share.COMBINE_WEIGHT_DEFAULT_VALUE
import com.omelan.cofi.share.DataStore
import com.omelan.cofi.share.Recipe
import com.omelan.cofi.share.Step
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

    val alreadyDoneWeight by Timer.rememberAlreadyDoneWeight(
        indexOfCurrentStep = allSteps.indexOf(currentStep.value),
        allSteps = allSteps,
        combineWeightState = combineWeightState,
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
            progress = animatedProgressValue.value,
            indicatorColor = if (ambientController.isAmbient) {
                Color.White
            } else {
                animatedProgressColor.value
            },
            startAngle = 300f,
            endAngle = 240f,
        )
        Column(
            modifier = Modifier
                .padding(4.dp)
                .aspectRatio(1f)
                .fillMaxSize()
                .animateContentSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally,
        ) {
            AnimatedVisibility(
                visible = currentStep.value == null && !isDone,
            ) {
                Text(
                    text = recipe.name,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 2,
                    style = MaterialTheme.typography.title1,
                )
            }
            AnimatedVisibility(
                visible = currentStep.value != null && !isDone,
            ) {
                Column {
                    if (currentStep.value != null) {
                        TimeText(
                            currentStep = currentStep.value!!,
                            animatedProgressValue = animatedProgressValue.value,
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
                            alreadyDoneWeight = alreadyDoneWeight,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 1,
                            style = MaterialTheme.typography.title1,
                        )
                    }
                }
            }
            AnimatedVisibility(visible = !ambientController.isAmbient) {
                Spacer(Modifier.height(6.dp))
                StartButton(isTimerRunning, startButtonOnClick)
            }
        }
    }
}
