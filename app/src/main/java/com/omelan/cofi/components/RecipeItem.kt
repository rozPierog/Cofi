package com.omelan.cofi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.omelan.cofi.model.Recipe
import com.omelan.cofi.ui.card
import com.omelan.cofi.ui.shapes

@ExperimentalAnimatedInsets
@Composable
fun RecipeItem(recipe: Recipe, onPress: (recipeId: Int) -> Unit) {
    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        shape = shapes.card,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { onPress(recipe.id) },
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                )
                .padding(horizontal = 10.dp)
                .height(75.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painterResource(id = recipe.recipeIcon.icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .size(30.dp)
            )
            Column(
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = recipe.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (recipe.description.isNotBlank()) {
                    Text(
                        text = recipe.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@ExperimentalAnimatedInsets
@Preview
@Composable
fun PreviewRecipeItem() {
    RecipeItem(
        recipe = Recipe(
            id = 0,
            name = "Ultimate V60",
            description = "Recipe by Hoffman",
        ),
        onPress = {}
    )
}