package com.omelan.cofi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omelan.cofi.R
import com.omelan.cofi.share.model.Recipe
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.model.StepType
import com.omelan.cofi.share.utils.toMillis
import com.omelan.cofi.ui.*

enum class ItemShape(val shape: CornerBasedShape) {
    First(firstItem),
    Middle(middleItem),
    Last(lastItem),
    Only(aloneItem), // Used when there is only one item in the list
}

@Composable
fun RecipeListItemBackground(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onClick: () -> Unit,
    shape: ItemShape,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        modifier = modifier,
        shape = shape.shape,
    ) {
        Column(
            modifier
                .clickable(
                    onClick = onClick,
                    role = Role.Button,
                )
                .padding(contentPadding),
            content = content,
        )
    }
}

@Composable
fun LazyGridItemScope.RecipeItem(
    recipe: Recipe,
    shape: ItemShape = ItemShape.Middle,
    allSteps: List<Step> = emptyList(),
    onPress: (recipeId: Int) -> Unit,
) {
    RecipeListItemBackground(
        modifier = Modifier.animateItem(),
        shape = shape,
        onClick = { onPress(recipe.id) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painterResource(id = recipe.recipeIcon.icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = Spacing.small)
                    .size(28.dp),
            )
            Column(
                modifier = Modifier.padding(
                    vertical = Spacing.big,
                    horizontal = Spacing.medium,
                ),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = recipe.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (recipe.description.isNotBlank()) {
                    Text(
                        text = recipe.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        HorizontalDivider(Modifier.padding(horizontal = Spacing.big))
        RecipeInfo(compactStyle = true, steps = allSteps)
    }
}

@Preview
@Composable
fun PreviewRecipeItem() {
    LazyVerticalGrid(columns = GridCells.Fixed(1)) {
        item {
            RecipeItem(
                shape = ItemShape.Middle,
                recipe = Recipe(
                    id = 0,
                    name = "Ultimate V60",
                    description = "Recipe by Hoffman",
                ),
                allSteps = listOf(
                    Step(
                        name = stringResource(R.string.prepopulate_step_coffee),
                        value = 30f,
                        time = 5.toMillis(),
                        type = StepType.ADD_COFFEE,
                    ),
                    Step(
                        name = stringResource(R.string.prepopulate_step_water),
                        value = 60f,
                        time = 5.toMillis(),
                        type = StepType.WATER,
                    ),
                    Step(
                        name = stringResource(R.string.prepopulate_step_swirl),
                        time = 5.toMillis(),
                        type = StepType.OTHER,
                    ),
                    Step(
                        name = stringResource(R.string.prepopulate_step_wait),
                        time = 35.toMillis(),
                        type = StepType.WAIT,
                    ),
                    Step(
                        name = stringResource(R.string.prepopulate_step_water),
                        time = 30.toMillis(),
                        type = StepType.WATER,
                        value = 240f,
                    ),
                ),
                onPress = {},
            )
        }
    }
}
