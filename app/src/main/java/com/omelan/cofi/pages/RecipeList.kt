package com.omelan.cofi.pages

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.omelan.cofi.R
import com.omelan.cofi.components.PiPAwareAppBar
import com.omelan.cofi.components.RecipeItem
import com.omelan.cofi.components.RecipeListInfoBox
import com.omelan.cofi.components.createAppBarBehavior
import com.omelan.cofi.share.DataStore
import com.omelan.cofi.share.RecipeViewModel
import com.omelan.cofi.share.StepsViewModel
import com.omelan.cofi.ui.Spacing
import com.omelan.cofi.utils.FabType
import com.omelan.cofi.utils.WearUtils
import com.omelan.cofi.utils.WearUtils.ObserveIfWearAppInstalled
import com.omelan.cofi.utils.getDefaultPadding
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeList(
    navigateToRecipe: (recipeId: Int) -> Unit,
    addNewRecipe: () -> Unit,
    goToSettings: () -> Unit,
    recipeViewModel: RecipeViewModel = viewModel(),
    stepsViewModel: StepsViewModel = viewModel(),
) {
    val configuration = LocalConfiguration.current
    val recipes by recipeViewModel.getAllRecipes().observeAsState(initial = emptyList())
    val steps by stepsViewModel.getAllSteps().observeAsState(initial = emptyList())
    val stepsByRecipe = steps.groupBy { it.recipeId }
    val scrollBehavior = createAppBarBehavior()
    val isMultiColumn by remember(configuration.screenWidthDp) {
        derivedStateOf { configuration.screenWidthDp > 600 }
    }
    val activity = LocalContext.current as Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    var wearNodesWithoutApp by remember {
        mutableStateOf(listOf<String>())
    }
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val dataStore = DataStore(activity)
    val dismissedBoxes by dataStore.getDismissedInfoBoxes().collectAsState(initial = null)
    ObserveIfWearAppInstalled {
        wearNodesWithoutApp = it
        if (it.isNotEmpty()) {
            coroutineScope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PiPAwareAppBar(
                actions = {
                    IconButton(onClick = goToSettings) {
                        Icon(Icons.Rounded.Settings, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = addNewRecipe,
                expanded = scrollBehavior.state.collapsedFraction < 0.9,
                modifier = Modifier.navigationBarsPadding(),
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        tint = LocalContentColor.current,
                        contentDescription = null,
                    )
                },
                text = {
                    Text(text = stringResource(R.string.recipe_create_title))
                },
            )
        },
    ) {
        LazyVerticalGrid(
            contentPadding = getDefaultPadding(it, FabType.Normal),
            verticalArrangement = Arrangement.spacedBy(Spacing.normal),
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            columns = if (isMultiColumn) GridCells.Fixed(2) else GridCells.Fixed(1),
            horizontalArrangement = Arrangement.spacedBy(Spacing.normal),
            state = lazyGridState,
        ) {
            if (wearNodesWithoutApp.isNotEmpty() && dismissedBoxes != null &&
                dismissedBoxes?.containsKey("wearOS") == false
            ) {
                item(key = "wearOS") {
                    RecipeListInfoBox(
                        title = {
                            Text(
                                text = stringResource(id = R.string.infoBox_wearOS_title),
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.normal),
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.ic_timer),
                                    contentDescription = "",
                                )
                                Text(text = stringResource(id = R.string.infoBox_wearOS_body))
                            }
                        },
                        onClick = {
                            WearUtils.openPlayStoreOnWearDevicesWithoutApp(
                                lifecycleOwner,
                                activity,
                                wearNodesWithoutApp,
                            )
                        },
                        onDismiss = {
                            val newMap = if (dismissedBoxes != null) {
                                dismissedBoxes!!.toMutableMap()
                            } else {
                                mutableMapOf()
                            }
                            newMap["wearOS"] = true
                            coroutineScope.launch {
                                dataStore.setDismissedInfoBoxes(newMap)
                            }
                        },
                    )
                }
            }
            items(recipes, key = { recipe -> recipe.id }) { recipe ->
                RecipeItem(
                    recipe = recipe,
                    onPress = navigateToRecipe,
                    allSteps = stepsByRecipe[recipe.id] ?: emptyList(),
                )
            }
        }
    }
}

@Preview
@Composable
fun RecipeListPreview() {
    RecipeList(
        navigateToRecipe = {},
        addNewRecipe = {},
        goToSettings = {},
    )
}
