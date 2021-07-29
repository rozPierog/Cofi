package com.omelan.cofi.pages

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.RecipeItem
import com.omelan.cofi.components.createLazyColumnPaddings
import com.omelan.cofi.components.createValues
import com.omelan.cofi.model.RecipeViewModel

@ExperimentalAnimatedInsets
@Composable
fun RecipeList(
    navigateToRecipe: (recipeId: Int) -> Unit,
    addNewRecipe: () -> Unit,
    goToSettings: () -> Unit,
    recipeViewModel: RecipeViewModel = viewModel(),
) {
    val recipes = recipeViewModel.getAllRecipes().observeAsState(initial = listOf())
    val (toolbarOffsetHeightPx, nestedScrollConnection) = createValues()
    val (contentPadding, listPadding) = createLazyColumnPaddings()

    Scaffold(
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
                    },
                    contentDescription = null,
                )
            }
        },
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            PiPAwareAppBar(
                actions = {
                    IconButton(onClick = goToSettings) {
                        Icon(Icons.Rounded.Settings, contentDescription = null)
                    }
                },
                firstItemOffset = toolbarOffsetHeightPx,
            )
            LazyColumn(
                contentPadding = contentPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(listPadding),
                ) {
                items(
                    recipes.value.plus(
                        recipes.value.plus(
                            recipes.value.plus(
                                recipes.value.plus(
                                    recipes.value
                                )
                            )
                        )
                    )
                ) { recipe ->
                    RecipeItem(
                        recipe = recipe,
                        onPress = navigateToRecipe,
                    )
                }
            }
        }
    }
}

@ExperimentalAnimatedInsets
@Preview
@Composable
fun RecipeListPreview() {
    RecipeList(navigateToRecipe = {}, addNewRecipe = {}, goToSettings = {})
}