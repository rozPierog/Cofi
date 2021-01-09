package com.omelan.burr.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.burr.R
import com.omelan.burr.model.Recipe
import com.omelan.burr.ui.BurrTheme
import com.omelan.burr.ui.card
import com.omelan.burr.ui.shapes

@Composable
fun RecipeItem(recipe: Recipe, onPress: (recipeId: Int) -> Unit) {
    fun onClickRecipe() {
        onPress(recipe.id)
    }
    BurrTheme {
        Card(
            elevation = 2.dp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            shape = shapes.card,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onClickRecipe() })
                    .padding(horizontal = 10.dp)
                    .preferredHeight(75.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    vectorResource(id = R.drawable.ic_coffee_grinder),
                    modifier = Modifier
                        .height(25.dp)
                        .aspectRatio(1f)
                        .align(Alignment.CenterVertically)
                )
                Column(
                    modifier = Modifier.padding(15.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = recipe.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.subtitle1,
                    )
                    if (recipe.description.isNotBlank()) {
                        Text(
                            text = recipe.description,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewRecipeItem() {
    RecipeItem(
        recipe = Recipe(
            id = 0,
            name = "Ultimate V60",
            description = "Recipe by Hoffman",
            iconName = R.drawable.ic_coffee
        ),
        onPress = {}
    )
}