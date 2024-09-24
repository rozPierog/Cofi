package com.omelan.cofi.utils

import android.annotation.SuppressLint
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import java.util.regex.Matcher
import java.util.regex.Pattern

fun linkSpanStyle(color: Color) = TextLinkStyles(
    SpanStyle(
        color = color,
        textDecoration = TextDecoration.Underline,
    ),
)

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
                urlMatcher.end(0),
            ),
        )
    }
    return containedUrls
}

@SuppressLint("ComposableNaming")
@Composable
fun buildAnnotatedStringWithUrls(baseText: String) =
    buildAnnotatedStringWithUrls(baseText, MaterialTheme.colorScheme.secondary)

fun buildAnnotatedStringWithUrls(baseText: String, color: Color) =
    buildAnnotatedString {
        val urlsInDescription = extractUrls(baseText)
        append(baseText)
        var lastPosition = 0
        urlsInDescription.forEach {
            val positionOfUrl = baseText.indexOf(it, startIndex = lastPosition)
            lastPosition = positionOfUrl
            addLink(
                LinkAnnotation.Url(
                    it,
                    styles = linkSpanStyle(color),
                ),
                start = positionOfUrl,
                end = positionOfUrl + it.length,
            )
        }
    }
