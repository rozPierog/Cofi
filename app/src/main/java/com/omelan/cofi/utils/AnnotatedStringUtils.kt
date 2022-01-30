package com.omelan.cofi.utils

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration

const val URL_ANNOTATION = "URL"

@Composable
fun linkSpanStyle() = SpanStyle(
    color = MaterialTheme.colorScheme.secondary,
    textDecoration = TextDecoration.Underline
)

@SuppressLint("ComposableNaming")
@Composable
fun AnnotatedString.Builder.addLinkAnnotation(start: Int, text: String, url: String = text) {
    addStringAnnotation(
        tag = URL_ANNOTATION,
        annotation = url,
        start = start,
        end = start + text.length,
    )
    addStyle(
        linkSpanStyle(),
        start = start,
        end = start + text.length
    )
}

@OptIn(ExperimentalTextApi::class)
@SuppressLint("ComposableNaming")
@Composable
fun AnnotatedString.Builder.appendLink(text: String, url: String = text) {
    withStyle(linkSpanStyle()) {
        withAnnotation(
            tag = URL_ANNOTATION,
            annotation = url,
        ) {
            append(text)
        }
    }
}