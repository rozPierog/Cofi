package com.omelan.cofi.utils

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import java.util.regex.Matcher
import java.util.regex.Pattern

const val URL_ANNOTATION = "URL"

fun linkSpanStyle(color: Color) = SpanStyle(
    color = color,
    textDecoration = TextDecoration.Underline
)

fun AnnotatedString.Builder.addLinkAnnotation(
    start: Int,
    text: String,
    url: String = text,
    color: Color
) {
    addStringAnnotation(
        tag = URL_ANNOTATION,
        annotation = url,
        start = start,
        end = start + text.length,
    )
    addStyle(
        linkSpanStyle(color),
        start = start,
        end = start + text.length
    )
}

@OptIn(ExperimentalTextApi::class)
fun AnnotatedString.Builder.appendLink(text: String, url: String = text, color: Color) {
    withStyle(linkSpanStyle(color)) {
        withAnnotation(
            tag = URL_ANNOTATION,
            annotation = url,
        ) {
            append(text)
        }
    }
}

private fun extractUrls(text: String): List<String> {
    val containedUrls: MutableList<String> = arrayListOf()
    val urlRegex =
        "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?+-=\\\\.&]*)"
    val pattern: Pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
    val urlMatcher: Matcher = pattern.matcher(text)
    while (urlMatcher.find()) {
        containedUrls.add(
            text.substring(
                urlMatcher.start(0),
                urlMatcher.end(0)
            )
        )
    }
    return containedUrls
}

@SuppressLint("ComposableNaming")
@Composable
fun buildAnnotatedStringWithUrls(baseText: String) =
    buildAnnotatedString {
        val urlsInDescription = extractUrls(baseText)
        append(baseText)
        var lastPosition = 0
        urlsInDescription.forEach {
            val positionOfUrl = baseText.indexOf(it, startIndex = lastPosition)
            lastPosition = positionOfUrl
            addLinkAnnotation(
                start = positionOfUrl,
                text = it,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }

fun buildAnnotatedStringWithUrls(baseText: String, color: Color) =
    buildAnnotatedString {
        val urlsInDescription = extractUrls(baseText)
        append(baseText)
        var lastPosition = 0
        urlsInDescription.forEach {
            val positionOfUrl = baseText.indexOf(it, startIndex = lastPosition)
            lastPosition = positionOfUrl
            addLinkAnnotation(start = positionOfUrl, text = it, color = color)
        }
    }