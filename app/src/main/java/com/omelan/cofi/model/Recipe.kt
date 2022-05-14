package com.omelan.cofi.model

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import com.omelan.cofi.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

// val dummySteps = listOf(
//    Step(
//        id = 1,
//        name = "Add Coffee",
//        value = 30,
//        time = 5 * 1000,
//        type = StepType.ADD_COFFEE,
//        orderInRecipe = 0
//    ),
//    Step(
//        id = 2,
//        name = "Add water",
//        value = 60,
//        time = 5 * 1000,
//        type = StepType.WATER,
//        orderInRecipe = 1
//    ),
//    Step(
//        id = 3,
//        name = "Swirl", time = 5 * 1000, type = StepType.OTHER,
//        orderInRecipe = 2
//    ),
//    Step(
//        id = 4,
//        name = "Wait", time = 35 * 1000, type = StepType.WAIT,
//        orderInRecipe = 3
//    ),
//    Step(
//        id = 5,
//        name = "Add Water",
//        time = 30 * 1000,
//        type = StepType.WATER,
//        value = 300,
//        orderInRecipe = 4
//    ),
//    Step(
//        id = 6,
//        name = "Add Water",
//        time = 30 * 1000,
//        type = StepType.WATER,
//        value = 200,
//        orderInRecipe = 5
//    ),
//    Step(
//        id = 7,
//        name = "Swirl", time = 5 * 1000, type = StepType.OTHER,
//        orderInRecipe = 6
//    ),
// )

enum class RecipeIcon(@DrawableRes val icon: Int) {
    V60(R.drawable.ic_drip),
    FrenchPress(R.drawable.ic_french_press),
    Grinder(R.drawable.ic_coffee_grinder),
    Chemex(R.drawable.ic_chemex),
    Aeropress(R.drawable.ic_aeropress)
}

class RecipeIconTypeConverter {
    @TypeConverter
    fun recipeIconToString(type: RecipeIcon): String {
        return type.name
    }

    @TypeConverter
    fun stringToRecipeIcon(type: String): RecipeIcon {
        return when (type) {
            RecipeIcon.V60.name -> RecipeIcon.V60
            RecipeIcon.FrenchPress.name -> RecipeIcon.FrenchPress
            RecipeIcon.Grinder.name -> RecipeIcon.Grinder
            RecipeIcon.Chemex.name -> RecipeIcon.Chemex
            RecipeIcon.Aeropress.name -> RecipeIcon.Aeropress
            else -> RecipeIcon.Grinder
        }
    }
}

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    @ColumnInfo(name = "last_finished") val lastFinished: Long = 0L,
    @ColumnInfo(name = "icon") val recipeIcon: RecipeIcon = RecipeIcon.Grinder,
)

private const val jsonName = "name"
private const val jsonDescription = "description"
private const val jsonRecipeIcon = "recipeIcon"
private const val jsonSteps= "steps"

fun Recipe.serialize(steps: List<Step>? = null): JSONObject = JSONObject().run {
    put(jsonName, name)
    put(jsonDescription, description)
    put(jsonRecipeIcon, recipeIcon.name)
    put(jsonSteps, steps?.serialize())
}


fun List<Recipe>.serialize(): JSONArray {
    return JSONArray().let {
        forEach { recipe ->
            val recipeJSONObject = recipe.serialize()
        }
        it
    }
}

fun JSONObject.toRecipe() =
    Recipe(
        name = getString(jsonName),
        description = getString(jsonDescription),
        recipeIcon = RecipeIconTypeConverter().stringToRecipeIcon(getString(jsonRecipeIcon))
    )


@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe ORDER BY last_finished DESC")
    fun getAll(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE ID is :id")
    fun get(id: Int): LiveData<Recipe>

    @Insert
    suspend fun insertAll(vararg recipes: Recipe)

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Query("DELETE FROM recipe WHERE id = :recipeId")
    suspend fun deleteById(recipeId: Int)
}

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val dao = db.recipeDao()

    fun getRecipe(id: Int) = dao.get(id)

    fun getAllRecipes() = dao.getAll()
}