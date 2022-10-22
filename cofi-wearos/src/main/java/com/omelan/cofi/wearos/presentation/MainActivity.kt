package com.omelan.cofi.wearos.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.omelan.cofi.share.Recipe
import com.omelan.cofi.share.RecipeIcon
import com.omelan.cofi.share.RecipeViewModel
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.toRecipes
import com.omelan.cofi.wearos.R
import com.omelan.cofi.wearos.presentation.components.RecipeListItem
import com.omelan.cofi.wearos.presentation.theme.CofiTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channelClient = Wearable.getChannelClient(this)
        val ioScope = CoroutineScope(Dispatchers.IO + Job())
        val db = AppDatabase.getInstance(this)
        channelClient.registerChannelCallback(
            object : ChannelClient.ChannelCallback() {
                override fun onChannelOpened(channel: ChannelClient.Channel) {
                    super.onChannelOpened(channel)
                    ioScope.launch {
                        val inputStream = channelClient.getInputStream(channel).await()
                        val jsonString = String(inputStream.readBytes(), StandardCharsets.UTF_8)
                        db.recipeDao().insertAll(JSONArray(jsonString).toRecipes())
                        withContext(Dispatchers.IO) {
                            inputStream.close()
                        }
                        channelClient.close(channel)
                    }
                }
            },
        )
        setContent {
            val recipeViewModel: RecipeViewModel = viewModel()
            val recipes by recipeViewModel.getAllRecipes().observeAsState(initial = emptyList())
            WearApp(recipes)
        }
    }

}

@Composable
fun WearApp(recipes: List<Recipe>) {
    val navController = rememberSwipeDismissableNavController()
    CofiTheme {
        Box {
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "recipe_list",
            ) {
                composable("recipe_list") {
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
                                navController.navigate(route = "recipe_details/${it.id}")
                            }
                        }
                    }
                }
                composable(
                    "recipe_details/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) {
                    val id = it.arguments?.getInt("id")
                    val recipe = recipes.find { Recipe -> Recipe.id == id }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxSize(),
                            progress = 0.5f,
                        )
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(text = recipe?.name ?: "wat")
                            Button(onClick = { /*TODO*/ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_gavel),
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }
            }
            TimeText()
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
    WearApp(recipes)
}
