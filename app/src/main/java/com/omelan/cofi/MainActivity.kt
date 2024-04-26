package com.omelan.cofi

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kieronquinn.monetcompat.BuildConfig
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.omelan.cofi.model.DataStore
import com.omelan.cofi.pages.addRecipe
import com.omelan.cofi.pages.details.recipeDetails
import com.omelan.cofi.pages.list.recipeList
import com.omelan.cofi.pages.recipeEdit
import com.omelan.cofi.pages.settings.settings
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.pages.Destinations
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val LocalPiPState = staticCompositionLocalOf<Boolean> {
    error("AmbientPiPState value not available.")
}

const val appDeepLinkUrl = "https://rozpierog.github.io"

@ExperimentalMaterial3WindowSizeClassApi
class MainActivity : MonetCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Cofi)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            CoroutineScope(Dispatchers.Main).launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    monet.awaitMonetReady()
                }
            }
        }
        this.setContent(null) {
            MainNavigation()
        }
    }

    private val onTimerRunning: (Boolean) -> Unit = { isRunning ->
        mainActivityViewModel.setCanGoToPiP(isRunning)
        if (isRunning) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            blockPip()
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun blockPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                setPictureInPictureParams(
                    PictureInPictureParams.Builder().setAutoEnterEnabled(false).build(),
                )
            } catch (e: IllegalStateException) {
                if (BuildConfig.DEBUG) {
                    Log.e("blockPip", "Tried to block pip but couldn't ${e.message}")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    fun MainNavigation() {
        val navController = rememberNavController()
        val db = AppDatabase.getInstance(this)
        val isInPiP by mainActivityViewModel.pipState.observeAsState(false)
        val systemUiController = rememberSystemUiController()
        val windowSizeClass = calculateWindowSizeClass(this)
        val intent by mainActivityViewModel.intent.observeAsState()
        LaunchedEffect(intent) {
            navController.handleDeepLink(intent)
        }
        CofiTheme(monet) {
            val darkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = darkIcons,
            )
            if (!isUsingGestures(applicationContext)) {
                systemUiController.setNavigationBarColor(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.8F),
                    darkIcons = darkIcons,
                )
            }
            CompositionLocalProvider(
                LocalPiPState provides isInPiP,
            ) {
                NavHost(
                    navController,
                    startDestination = Destinations.RECIPE_LIST,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    enterTransition = {
                        slideIn(AnimatedContentTransitionScope.SlideDirection.End)
                    },
                    exitTransition = {
                        slideOut(AnimatedContentTransitionScope.SlideDirection.Start)
                    },
                    popEnterTransition = {
                        slideIn(AnimatedContentTransitionScope.SlideDirection.Start)
                    },
                    popExitTransition = {
                        slideOut(AnimatedContentTransitionScope.SlideDirection.End)
                    },
                ) {
                    recipeList(navController = navController)
                    recipeDetails(navController, onTimerRunning, windowSizeClass, db)
                    recipeEdit(navController, db)
                    addRecipe(navController, db)
                    settings(navController)
                }
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        }
        mainActivityViewModel.setIsInPiP(isInPictureInPictureMode)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val isPiPEnabledFlow: Flow<Boolean> = DataStore(this).getPiPSetting()
        var isPiPEnabled: Boolean
        runBlocking {
            isPiPEnabled = isPiPEnabledFlow.first()
        }
        if (mainActivityViewModel.canGoToPiP.value == true && isPiPEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPictureInPictureMode(PictureInPictureParams.Builder().build())
            }
        }
    }

    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        onResumedCompat()
        super.onTopResumedActivityChanged(isTopResumedActivity)
    }

    override fun onResume() {
        onResumedCompat()
        super.onResume()
    }

    private fun onResumedCompat() {
        this.addOnNewIntentListener {
            mainActivityViewModel.setIntent(it)
        }

        val currentPiPStatus = mainActivityViewModel.canGoToPiP.value ?: false
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            currentPiPStatus &&
            !checkPiPPermission(this)
        ) {
            setPictureInPictureParams(
                PictureInPictureParams.Builder().setAutoEnterEnabled(false).build(),
            )
        }
        WearUtils.observeChangesAndSendToWear(this)
    }

    override fun onPause() {
        super.onPause()
        WearUtils.removeObservers(this)
    }
}
