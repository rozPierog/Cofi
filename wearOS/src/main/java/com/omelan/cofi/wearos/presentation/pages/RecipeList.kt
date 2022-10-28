@file:OptIn(ExperimentalAnimationApi::class)

package com.omelan.cofi.wearos.presentation.pages

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import androidx.wear.compose.material.dialog.Confirmation
import com.omelan.cofi.share.Recipe
import com.omelan.cofi.share.RecipeIcon
import com.omelan.cofi.share.RecipeViewModel
import com.omelan.cofi.wearos.R
import com.omelan.cofi.wearos.presentation.components.RecipeListItem
import com.omelan.cofi.wearos.presentation.utils.WearUtils
import com.omelan.cofi.wearos.presentation.utils.getActivity

@Composable
fun RecipeList(goToDetails: (Recipe) -> Unit) {
    val recipeViewModel: RecipeViewModel = viewModel()
    val recipes by recipeViewModel.getAllRecipes().observeAsState(initial = emptyList())
    RecipeList(recipes = recipes, goToDetails = goToDetails)
}

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    goToDetails: (Recipe) -> Unit = {},
) {
    var hasPhoneApp by remember { mutableStateOf(false) }
    var showConfirmation by remember { mutableStateOf(false) }
    val mainActivity = LocalContext.current.getActivity()
    WearUtils.ObserveIfPhoneAppInstalled {
        hasPhoneApp = it
    }

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
        if (!hasPhoneApp && mainActivity != null) {
            item {
                Card(
                    onClick = {
                        WearUtils.openAppInStoreOnPhone(
                            mainActivity,
                            onSuccess = { showConfirmation = true },
                        )
                    },
                ) {
                    Row {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = R.drawable.common_full_open_on_phone),
                            contentDescription = "",
                        )
                        Text(text = "Install Cofi on your phone")
                    }
                }
            }
        }
        items(recipes) {
            RecipeListItem(recipe = it) {
                goToDetails(it)
            }
        }
    }
    AnimatedVisibility(
        visible = showConfirmation,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        Confirmation(
            onTimeout = { showConfirmation = false },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.common_full_open_on_phone),
                    contentDescription = "",
                )
            },
        ) {
            Text(text = stringResource(id = R.string.common_open_on_phone))
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
