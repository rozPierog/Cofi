package com.omelan.cofi.wearos.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.gms.wearable.Wearable
import com.omelan.cofi.share.RecipeIcon
import com.omelan.cofi.share.RecipeShared
import com.omelan.cofi.wearos.R
import com.omelan.cofi.wearos.presentation.components.RecipeListItem
import com.omelan.cofi.wearos.presentation.theme.CofiTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }

    override fun onResume() {
        super.onResume()
        val channelClient = Wearable.getChannelClient(this)
        val ioScope = CoroutineScope(Dispatchers.IO + Job())
        ioScope.launch {
            val nodes = Wearable.getNodeClient(applicationContext).connectedNodes.await()
            nodes.forEach { node ->
                val channel = channelClient.openChannel(node.id, "sync").await()
                val inputStream = channelClient.getInputStream(channel).await()
                Log.e("TEST", inputStream.toString())
                channelClient.close(channel)
            }
        }
    }
}

@Composable
fun WearApp() {
    val recipes = arrayOf(
        RecipeShared(
            id = 1,
            name = stringResource(R.string.prepopulate_v60_name),
            description = stringResource(R.string.prepopulate_v60_description),
            recipeIcon = RecipeIcon.V60,
        ),
        RecipeShared(
            id = 2,
            name = stringResource(R.string.prepopulate_frenchPress_name),
            description = stringResource(R.string.prepopulate_frenchPress_description),
            recipeIcon = RecipeIcon.FrenchPress,
        ),
        RecipeShared(
            id = 3,
            name = stringResource(R.string.prepopulate_chemex_name),
            description = stringResource(R.string.prepopulate_chemex_description),
            recipeIcon = RecipeIcon.Chemex,
        ),
        RecipeShared(
            id = 4,
            name = stringResource(R.string.prepopulate_aero_name),
            description = stringResource(R.string.prepopulate_aero_description),
            recipeIcon = RecipeIcon.Aeropress,
        ),
    )

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
                    val recipe = recipes.find { recipeShared -> recipeShared.id == id }
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
    WearApp()
}
