package com.omelan.cofi.wearos.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.omelan.cofi.share.Recipe

@Composable
fun RecipeListItem(modifier: Modifier = Modifier, recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        onClick = onClick,
        contentColor = MaterialTheme.colors.onSurface,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = recipe.recipeIcon.icon),
                contentDescription = recipe.name,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(recipe.name)
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND)
@Composable
fun RecipeListItemPreview() {
    RecipeListItem(recipe = Recipe(id = 0, name = "test")) {

    }
}
