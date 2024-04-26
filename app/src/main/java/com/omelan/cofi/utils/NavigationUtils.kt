package com.omelan.cofi.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry

const val tweenDuration = 300
private fun offsetCalculation(width: Int) = width / 4
fun AnimatedContentTransitionScope<NavBackStackEntry>.slideIn(
    towards: AnimatedContentTransitionScope.SlideDirection,
) =
    fadeIn(tween(tweenDuration, easing = LinearOutSlowInEasing)) +
        slideIntoContainer(
            towards,
            animationSpec = tween(tweenDuration, easing = FastOutSlowInEasing),
            initialOffset = { fullWidth -> -(offsetCalculation(fullWidth)) },
        )

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOut(
    towards: AnimatedContentTransitionScope.SlideDirection,
) = slideOutOfContainer(
    towards,
    animationSpec = tween(tweenDuration, easing = FastOutSlowInEasing),
    targetOffset = { fullWidth -> offsetCalculation(fullWidth) },
) + fadeOut(tween(tweenDuration, easing = LinearOutSlowInEasing))
