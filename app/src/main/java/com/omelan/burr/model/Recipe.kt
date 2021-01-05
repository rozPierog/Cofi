package com.omelan.burr.model

import androidx.annotation.DrawableRes
import androidx.annotation.WorkerThread
import androidx.room.*
import com.omelan.burr.R

val dummySteps = listOf(
    Step(
        name = "Add Coffee",
        value = 30,
        time = 5 * 1000,
        type = StepType.ADD_COFFEE
    ),
    Step(
        name = "Add water",
        value = 60,
        time = 5 * 1000,
        type = StepType.WATER
    ),
    Step( name = "Swirl", time = 5 * 1000, type = StepType.OTHER),
    Step( name = "Wait", time = 35 * 1000, type = StepType.WAIT),
    Step(
        name = "Add Water",
        time = 30 * 1000,
        type = StepType.WATER,
        value = 300
    ),
    Step(
        name = "Add Water",
        time = 30 * 1000,
        type = StepType.WATER,
        value = 200
    ),
    Step(name = "Swirl", time = 5 * 1000, type = StepType.OTHER),
)

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
//    @Ignore
//    val steps: List<Step> = dummySteps,
    @DrawableRes
    @ColumnInfo(name = "icon_name") val iconName: Int = R.drawable.ic_coffee
)

data class RecipesWithSteps(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val steps: List<Step>
)


@Dao
interface RecipeDao {

    @WorkerThread
    @Query("SELECT * FROM recipe")
    suspend fun getAll(): List<Recipe>

    @Transaction
    @WorkerThread
    @Query("SELECT * FROM recipe")
    suspend fun getRecipesWithSteps(): List<RecipesWithSteps>

    @Insert
    @WorkerThread
    suspend fun insertAll(vararg recipes: Recipe)

    @Insert
    @WorkerThread
    suspend fun insertRecipe(recipe: Recipe): Long

    @Delete
    @WorkerThread
    suspend fun delete(recipe: Recipe)
}