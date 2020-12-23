package com.omelan.burr.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ListItem
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.omelan.burr.model.Recipe

@Composable
fun RecipeList(recipes: List<Recipe>) {
        LazyColumn {
            items(recipes, itemContent = { recipe ->
                RecipeItem(
                    name = recipe.name,
                    description = recipe.description,
                    icon = recipe.iconName,
                )
            })
        }
}

@Preview
@Composable
fun RecipeListPreview() {
    val listOfRecipes = listOf<Recipe>(
        Recipe("Ultimate v60", description = "Hoffman"),
        Recipe("Ultimate v60", description = "Hoffman"),
        Recipe("Ultimate v60", description = "Hoffman"),
    )
    RecipeList(listOfRecipes)
}