package com.omelan.burr.model

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.*
import com.omelan.burr.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
    Step(name = "Swirl", time = 5 * 1000, type = StepType.OTHER),
    Step(name = "Wait", time = 35 * 1000, type = StepType.WAIT),
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
//    val steps: List<Step> = listOf(),
    @ColumnInfo(name = "last_finished") val lastFinished: Int = 0,
    @DrawableRes
    @ColumnInfo(name = "icon_name") val iconName: Int = R.drawable.ic_coffee,
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
    @Query("SELECT * FROM recipe ORDER BY last_finished ASC")
    fun getAll(): LiveData<List<Recipe>>

    @WorkerThread
    @Query("SELECT * FROM recipe WHERE ID is :id")
    fun get(id: Int): LiveData<Recipe>

    @Transaction
    @WorkerThread
    @Query("SELECT * FROM recipe ORDER BY last_finished ASC")
    fun getRecipesWithSteps(): LiveData<List<RecipesWithSteps>>

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

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val dao = db.recipeDao()
    fun getAllRecipesWithSteps(): LiveData<List<RecipesWithSteps>> {
        return dao.getRecipesWithSteps()
    }

    fun getRecipe(id: Int) = dao.get(id)

    fun getAllRecipes() = dao.getAll()
}