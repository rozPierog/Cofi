package com.omelan.cofi.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.omelan.cofi.ui.card
import com.omelan.cofi.ui.shapes
import com.omelan.cofi.utils.IntentHelpers
import java.util.regex.Matcher
import java.util.regex.Pattern

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

@ExperimentalAnimatedInsets
@Composable
fun Description(modifier: Modifier = Modifier, descriptionText: String) {
    val (isExpanded, setIsExpanded) = remember { mutableStateOf(false) }
    val (showExpandButton, setShowExpandButton) = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val descriptionWithLinks = buildAnnotatedString {
        val urlsInDescription = extractUrls(descriptionText)
        append(descriptionText)
        addStyle(
            SpanStyle(color = MaterialTheme.colorScheme.onSurface),
            0,
            descriptionText.length
        )
        var lastPosition = 0
        urlsInDescription.forEach {
            val positionOfUrl = descriptionText.indexOf(it, startIndex = lastPosition)
            lastPosition = positionOfUrl
            addStringAnnotation(
                tag = "URL",
                annotation = it,
                start = positionOfUrl,
                end = positionOfUrl + it.length,
            )
            addStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    textDecoration = TextDecoration.Underline
                ),
                positionOfUrl,
                positionOfUrl + it.length
            )
        }
    }
    val rotationDegree = remember { Animatable(initialValue = 0f) }
    LaunchedEffect(isExpanded) {
        rotationDegree.animateTo(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
    }
    Surface(modifier = modifier, shape = shapes.card, tonalElevation = 2.dp) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .toggleable(
                    value = isExpanded,
                    onValueChange = {
                        if (showExpandButton) {
                            setIsExpanded(it)
                        }
                    },
                    role = Role.Switch,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                )
                .padding(15.dp)
        ) {
            ClickableText(
                text = descriptionWithLinks,
                maxLines = if (isExpanded) {
                    Int.MAX_VALUE
                } else {
                    2
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.animateContentSize(),
                onClick = {
                    descriptionWithLinks
                        .getStringAnnotations("URL", it, it)
                        .firstOrNull()?.let { stringAnnotation ->
                            IntentHelpers.openUri(context, stringAnnotation.item)
                            return@ClickableText
                        }
                    if (showExpandButton) {
                        setIsExpanded(!isExpanded)
                    }
                },
                onTextLayout = { textLayoutResult ->
                    if (!isExpanded) {
                        setShowExpandButton(textLayoutResult.didOverflowHeight)
                    }
                }
            )
            if (showExpandButton || isExpanded) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Expand",
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .align(Alignment.CenterHorizontally)
                        .rotate(rotationDegree.value)
                )
            }
        }
    }
}

@ExperimentalAnimatedInsets
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DescriptionPreview() {
    Description(
        descriptionText = "Recipe made by Hoffman Weird Coffee Person. Grind mildly" +
                " \n https://www.youtube.com/watch?v=AI4ynXzkSQo "
    )
}

@ExperimentalAnimatedInsets
@Preview
@Composable
fun DescriptionLongPreview() {
    val longDesc = " ULTIMATE V60 TECHNIQUE\n" +
            "https://www.youtube.com/watch?v=AI4ynXzkSQo\n" +
            "Brew ratio: 60 g/L (e.g. 30 g per 500 mL)\n" +
            "Grind size: medium fine\n" +
            "Temperature: the hotter, the better (especially with lighter roasts)\n" +
            "\n" +
            "◉ Grind 30 g of coffee\n" +
            "◉ Rinse paper filter with water just off the boil\n" +
            "\n" +
            "This removes any paper taste and preheats the brewer\n" +
            "\n" +
            "◉ Add coffee grounds to V60\n" +
            "◉ Create well in the middle of the grounds\n" +
            "\n" +
            "This helps to evenly saturate all the grounds during the blooming phase\n" +
            "\n" +
            "◉ Start timer\n" +
            "\n" +
            "t = 0:00\n" +
            "\n" +
            "◉ Add 2x coffee weight = 60 g of bloom water\n" +
            "\n" +
            "Don’t use more than 3x coffee weight\n" +
            "\n" +
            "◉ Swirl the coffee slurry until evenly mixed\n" +
            "\n" +
            "The aim is to wet all the coffee grounds by evenly mixing bloom water and coffee\n" +
            "Swirling is better than using a spoon\n" +
            "\n" +
            "◉ Bloom for up to 45 s\n" +
            "\n" +
            "This allows CO2 to escape which will improve extraction\n" +
            "\n" +
            "t = 0:45\n" +
            "\n" +
            "◉ Add water aiming for 60% of total brew weight = 300 g in the next 30 s\n" +
            "\n" +
            "This phase is critical!\n" +
            "\n" +
            "Since you already added 60 g bloom water, add 240 g in 30 s (flow rate = 8 g/s)\n" +
            "A full V60 is good to maintain high temperatures\n" +
            "\n" +
            "t = 1:15\n" +
            "\n" +
            "P.S.: Don’t worry about pouring directly onto the V60 filter\n" +
            "\n" +
            "◉ Add water aiming for 100% of the total brew weight = 500 g in the next 30 s\n" +
            "\n" +
            "Since you already added 300 g water, add 200 g in 30 s (flow rate = 6.66 g/s)\n" +
            "Poor a little slower than in the first phase, not too aggressively\n" +
            "\n" +
            "t = 1:45\n" +
            "\n" +
            "◉ Stir 1x clockwise and 1x anticlockwise with spoon\n" +
            "\n" +
            "This knocks off grounds from side wall\n" +
            "\n" +
            "◉ Allow V60 to drain a little\n" +
            "◉ Give V60 a gentle swirl\n" +
            "\n" +
            "This helps obtain a flat coffee bed at bottom of V60 for even extraction\n" +
            "\n" +
            "◉ Let brew drawdown\n" +
            "\n" +
            "The higher the temperature, the faster the drawdown\n" +
            "Filter paper also affects drawdown\n" +
            "Aim to finish drawdawn by t = 3:30\n" +
            "\n" +
            "◉ Enjoy!\n" +
            "\n"
    Description(descriptionText = longDesc)
}