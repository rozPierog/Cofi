package com.omelan.cofi.wearos.presentation.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import com.omelan.cofi.share.Recipe
import com.omelan.cofi.share.RecipeIcon
import com.omelan.cofi.share.RecipeViewModel
import com.omelan.cofi.wearos.R
import com.omelan.cofi.wearos.presentation.components.RecipeListItem

@Composable
fun RecipeList(goToDetails: (Recipe) -> Unit) {
    val recipeViewModel: RecipeViewModel = viewModel()
    val recipes by recipeViewModel.getAllRecipes().observeAsState(initial = emptyList())
    RecipeList(recipes = recipes, goToDetails = goToDetails)
}

@Composable
fun RecipeList(recipes: List<Recipe>, goToDetails: (Recipe) -> Unit = {}) {
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        autoCentering = AutoCenteringParams(itemIndex = 1, itemOffset = 0),
    ) {
        item {
            Text(text = "Cofi")
        }
        items(recipes) {
            RecipeListItem(recipe = it) {
                goToDetails(it)
            }
        }
    }

}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    val recipes = listOf(
        Recipe(
            id = 1,
            name = stringResource(R.string.prepopulate_v60_name),
            description = stringResource(R.string.prepopulate_v60_description),
            recipeIcon = RecipeIcon.V60,
        ),
        Recipe(
            id = 2,
            name = stringResource(R.string.prepopulate_frenchPress_name),
            description = stringResource(R.string.prepopulate_frenchPress_description),
            recipeIcon = RecipeIcon.FrenchPress,
        ),
        Recipe(
            id = 3,
            name = stringResource(R.string.prepopulate_chemex_name),
            description = stringResource(R.string.prepopulate_chemex_description),
            recipeIcon = RecipeIcon.Chemex,
        ),
        Recipe(
            id = 4,
            name = stringResource(R.string.prepopulate_aero_name),
            description = stringResource(R.string.prepopulate_aero_description),
            recipeIcon = RecipeIcon.Aeropress,
        ),
    )
    RecipeList(recipes)
}
