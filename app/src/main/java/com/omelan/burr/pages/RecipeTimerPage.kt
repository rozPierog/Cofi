package com.omelan.burr.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getColor
import com.omelan.burr.R
import com.omelan.burr.model.Recipe

@Composable
fun RecipeTimerPage(recipe: Recipe) {
//        val progress: MutableState<Float> = remember { mutableStateOf(0.0f) }
//        val animatedProgress = animatedFloat(
//                initVal = progress.value,
//
//        )
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = recipe.name, color = Color.Black)
        Text(text = recipe.description, color = Color.Black)
        val progressModifier = Modifier.fillMaxWidth().aspectRatio(1f).align(Alignment.CenterHorizontally)
        CircularProgressIndicator(progress = 1.0f, modifier = progressModifier, color = Color.Cyan)
    }

}

@Preview(showBackground = true)
@Composable
fun RecipeTimerPagePreview() {
    RecipeTimerPage(Recipe("V60", "Ble ble"))
}