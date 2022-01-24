package com.omelan.cofi.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun AnnotatedString.Builder.addLink(positionOfUrl: Int, text: String, url: String = text) {
    addStringAnnotation(
        tag = "URL",
        annotation = url,
        start = positionOfUrl,
        end = positionOfUrl + text.length,
    )
    addStyle(
        SpanStyle(
            color = MaterialTheme.colorScheme.secondary,
            textDecoration = TextDecoration.Underline
        ),
        positionOfUrl,
        positionOfUrl + text.length
    )
}