package com.omelan.burr.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.R
import com.omelan.burr.ui.BurrTheme


@Composable
fun RecipeItem(name: String, description: String, icon: Int, onClick: () -> Unit = {}) {
    BurrTheme() {

        Card(
            elevation = 5.dp,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.padding(10.dp)
        )
        {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.cardBackground))
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().align(Alignment.Start)) {
//                    Image(imageResource(icon))
                        Text(text = name)
                    }
                    Text(text = description)
                }
            }
        }
    }

}

@Preview
@Composable
fun PreviewRecipeItem() {
    RecipeItem(name = "Ultimate V60", description = "Recipe by Hoffman", icon = R.drawable.ic_drip)
}