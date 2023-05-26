@file:OptIn(ExperimentalAnimationApi::class)

package com.omelan.cofi.utils

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry

const val tweenDuration = 250
private fun offsetCalculation(width: Int) = width / 8
fun AnimatedContentScope<NavBackStackEntry>.slideIn(towards: AnimatedContentScope.SlideDirection) =
    fadeIn(tween(tweenDuration)) +
            slideIntoContainer(
                towards,
                animationSpec = tween(tweenDuration),
                initialOffset = { fullWidth -> -(offsetCalculation(fullWidth)) },
            )

fun AnimatedContentScope<NavBackStackEntry>.slideOut(towards: AnimatedContentScope.SlideDirection) =
    fadeOut(tween(tweenDuration)) +
            slideOutOfContainer(
                towards,
                animationSpec = tween(tweenDuration),
                targetOffset = { fullWidth -> offsetCalculation(fullWidth) },
            )

