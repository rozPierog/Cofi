/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.omelan.cofi.wearos.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.omelan.cofi.share.RecipeIcon
import com.omelan.cofi.share.RecipeShared
import com.omelan.cofi.wearos.R
import com.omelan.cofi.wearos.presentation.components.RecipeListItem
import com.omelan.cofi.wearos.presentation.theme.CofiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp("Android")
        }
    }
}

@Composable
fun WearApp(greetingName: String) {
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
    CofiTheme {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            autoCentering = AutoCenteringParams(itemIndex = 1 , itemOffset = 0)

        ) {
            item {
                Text(text = "Cofi")
            }
            items(recipes) {
                RecipeListItem(recipe = it) {
                    
                }
            }

        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.secondary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}
