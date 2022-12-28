@file:OptIn(ExperimentalAnimationApi::class)

package com.omelan.cofi.share.components

import androidx.compose.animation.*

fun <S> slideUpDown(isAboveCheck: (S, S) -> Boolean): AnimatedContentScope<S>.() -> ContentTransform = {
    if (isAboveCheck(targetState, initialState)) {
        slideInVertically { height -> -height } + fadeIn() with
                slideOutVertically { height -> height } + fadeOut()
    } else {
        slideInVertically { height -> height } + fadeIn() with
                slideOutVertically { height -> -height } + fadeOut()
    }.using(
        SizeTransform(clip = false),
    )
}
