package com.omelan.burr.components

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.R

@Composable
fun RecipeItem(name: String, description: String, icon: Int) {
//    Surface(border = BorderStroke(color = Color.Cyan, width = 2.dp), elevation = 15.dp, color = Color.LightGray) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(modifier = Modifier.fillMaxWidth().align(Alignment.Start)) {
//                checkNotNull(icon)
//                Image(imageResource(icon))
                Text(text = name)
            }
            Text(text = description)
        }
//    }
}

@Preview
@Composable
fun PreviewRecipeItem() {
    RecipeItem(name = "Ultimate V60", description = "Recipe by Hoffman", icon = R.drawable.ic_drip)
}