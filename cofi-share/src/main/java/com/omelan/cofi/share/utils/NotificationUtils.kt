package com.omelan.cofi.share.utils

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.work.*
import com.omelan.cofi.share.R
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.Step
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

private const val TIMER_CHANNEL_ID = "cofi_timer_notification"
fun Context.createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            TIMER_CHANNEL_ID,
            "Timer",
            NotificationManager.IMPORTANCE_HIGH,
        )
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }
}

fun Step.toNotificationBuilder(
    context: Context,
    currentProgress: Float,
): NotificationCompat.Builder {
    val step = this
    val builder =
        NotificationCompat.Builder(context, "timer").run {
            setSmallIcon(step.type.iconRes)
            color = ResourcesCompat.getColor(
                context.resources,
                R.color.ic_launcher_background,
                null,
            )
            setColorized(true)
            setContentTitle(step.name)
            if (step.time != null) {
                setProgress(
                    step.time,
                    currentProgress.roundToInt(),
                    false,
                )
            }
            val bundle = Bundle()
            bundle.putFloat("animatedValue", currentProgress)
            bundle.putInt("currentStepId", step.id)

            setExtras(bundle)
        }
    return builder
}

fun postTimerNotification(
    context: Context,
    notificationBuilder: NotificationCompat.Builder,
    id: Int = System.currentTimeMillis().toInt(),
    tag: String = id.toString(),
) {
    NotificationManagerCompat.from(context).apply {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1,
                )
            }
        }
        notify(tag, id, notificationBuilder.build())
    }
}

fun Context.startTimerWorker(
    recipeId: Int,
    currentProgress: Float,
    currentStepId: Int,
) {
    val inputData = Data.Builder().apply {
        putInt("recipeId", recipeId)
        putFloat("currentProgress", currentProgress)
        putInt("currentStepId", currentStepId)
    }.build()
    val timerWorker =
        OneTimeWorkRequest.Builder(TimerWorker::class.java).setInputData(inputData).build()
    val workManager = WorkManager.getInstance(this)
    workManager.enqueue(timerWorker)
}

class TimerWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val valueMap = workerParams.inputData.keyValueMap
        val recipeId = valueMap["recipeId"] as Int
        val startingStepId = valueMap["currentStepId"] as Int
        val currentProgress = valueMap["currentProgress"] as Float
        val db = AppDatabase.getInstance(context)
        withContext(Dispatchers.Main) {
            db.stepDao().getStepsForRecipe(recipeId).observeForever { steps ->
                var currentStep = steps.find { it.id == startingStepId } ?: return@observeForever
                postTimerNotification(
                    context,
                    currentStep.toNotificationBuilder(context, currentProgress),
                    id = 1,
                    tag = "cofi_notification_timer",
                )
                fun startCountDown(step: Step) {
                    fun goToNextStep() {
                        startCountDown(steps[steps.indexOf(step) + 1])
                    }
                    if (step.time == null) {
                        goToNextStep()
                        return
                    }
                    val millisToCount = step.time.toLong() - currentProgress.toLong()
                    val countDownTimer = object : CountDownTimer(millisToCount, 1) {
                        override fun onTick(millisUntilFinished: Long) {
                            postTimerNotification(
                                context,
                                step.toNotificationBuilder(context, millisUntilFinished.toFloat()),
                                id = 1,
                                tag = "cofi_notification_timer",
                            )
                        }

                        override fun onFinish() {
                            if (steps.last().id == step.id) {
                                postTimerNotification(
                                    context,
                                    NotificationCompat.Builder(context, "timer").apply {
                                        setSmallIcon(R.drawable.ic_monochrome)
                                        color = ResourcesCompat.getColor(
                                            context.resources,
                                            R.color.ic_launcher_background,
                                            null,
                                        )
                                        setColorized(true)
                                        setContentTitle(context.getString(R.string.timer_enjoy))
                                    },
                                    id = 1,
                                    tag = "cofi_notification_timer",
                                )
                            }
                            goToNextStep()
                        }
                    }
                    countDownTimer.start()
                }
                startCountDown(currentStep)
            }
        }
        return Result.success()
    }
}
