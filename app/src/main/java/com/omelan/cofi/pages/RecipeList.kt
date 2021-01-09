package com.omelan.cofi.pages

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.viewModel
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.RecipeItem
import com.omelan.cofi.model.RecipeViewModel
import com.omelan.cofi.ui.CofiTheme
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues

@Composable
fun RecipeList(
    navigateToRecipe: (recipeId: Int) -> Unit,
    addNewRecipe: () -> Unit,
    goToSettings: () -> Unit,
    recipeViewModel: RecipeViewModel = viewModel(),
) {
    val recipes = recipeViewModel.getAllRecipes().observeAsState(initial = listOf())
    CofiTheme {
        Scaffold(
            topBar = {
                PiPAwareAppBar(
                    actions = {
                        IconButton(onClick = goToSettings) {
                            Icon(Icons.Rounded.Settings)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = addNewRecipe,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        tint = if (isSystemInDarkTheme()) {
                            Color.Black
                        } else {
                            Color.White
                        }
                    )
                }
            },
        ) {
            LazyColumn(
                contentPadding = AmbientWindowInsets.current.navigationBars.toPaddingValues(),
                modifier = Modifier.fillMaxSize()
            ) {
                items(recipes.value) { recipe ->
                    RecipeItem(
                        recipe = recipe,
                        onPress = navigateToRecipe,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RecipeListPreview() {
    RecipeList(navigateToRecipe = {}, addNewRecipe = {}, goToSettings = {})
}