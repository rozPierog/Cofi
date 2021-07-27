package com.omelan.cofi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.omelan.cofi.ui.CofiTheme
import com.omelan.cofi.ui.full

@Composable
fun MaterialYouSwitch(
    modifier: Modifier = Modifier,
    isToggled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = modifier
            .height(15.dp)
            .width(30.dp)
            .clickable {
                onToggle(!isToggled)
            },
        shape = MaterialTheme.shapes.full,
        color = MaterialTheme.colors.primary,
    ) {
        ConstraintLayout {
            val constraintRef = createRef()
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.full)
                    .background(MaterialTheme.colors.background)
                    .constrainAs(constraintRef) {
                        if (!isToggled) {
                            start.linkTo(parent.start, 1.dp)
                        } else {
                            end.linkTo(parent.end, 1.dp)
                        }
                    },
            )
        }
    }
}

@ExperimentalAnimatedInsets
@Preview
@Composable
fun PreviewSwitch() {
    val toggle = remember { mutableStateOf(false) }
    CofiTheme {
        MaterialYouSwitch(isToggled = toggle.value, onToggle = { toggle.value = it })
    }
}