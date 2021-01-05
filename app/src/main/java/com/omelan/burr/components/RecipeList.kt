package com.omelan.burr.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.viewModel
import com.omelan.burr.model.Recipe
import com.omelan.burr.model.RecipeViewModel

@Composable
fun RecipeList(
    navigateToRecipe: (recipeId: Int) -> Unit,
    addNewRecipe: () -> Unit,
    recipeViewModel: RecipeViewModel = viewModel(),
) {
    val listOfRecipesAndSteps = recipeViewModel.getAllRecipesWithSteps().observeAsState(initial = listOf())
    val listOfRecipe = listOfRecipesAndSteps.value.map { it.recipe }
    LazyColumn {
        items(listOfRecipe) { recipe ->
            RecipeItem(
                recipe = recipe,
                onPress = navigateToRecipe,
            )
        }
        item {
            RecipeItem(
                recipe = Recipe(id = 0, name = "Add new!", description = "Add all new recipe"),
                onPress = { addNewRecipe() },
            )
        }
    }
}

@Preview
@Composable
fun RecipeListPreview() {
    RecipeList(navigateToRecipe = {}, addNewRecipe = {})
}