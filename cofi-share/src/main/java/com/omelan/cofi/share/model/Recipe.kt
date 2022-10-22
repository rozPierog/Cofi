package com.omelan.cofi.share

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import com.omelan.cofi.share.model.AppDatabase
import org.json.JSONArray
import org.json.JSONObject


enum class RecipeIcon(@DrawableRes val icon: Int) {
    V60(R.drawable.ic_drip),
    FrenchPress(R.drawable.ic_french_press),
    Grinder(R.drawable.ic_coffee_grinder),
    Chemex(R.drawable.ic_chemex),
    Aeropress(R.drawable.ic_aeropress),
}

open class RecipeIconTypeConverter {
    @TypeConverter
    open fun recipeIconToString(type: RecipeIcon): String {
        return type.name
    }

    @TypeConverter
    open fun stringToRecipeIcon(type: String): RecipeIcon {
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

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe ORDER BY last_finished DESC")
    fun getAll(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE ID is :id")
    fun get(id: Int): LiveData<Recipe>

    @Insert
    suspend fun insertAll(vararg recipes: Recipe)

    @Insert
    suspend fun insertAll(recipes: List<Recipe>)

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

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    @ColumnInfo(name = "last_finished") val lastFinished: Long = 0L,
    @ColumnInfo(name = "icon") val recipeIcon: RecipeIcon = RecipeIcon.Grinder,
)

private const val jsonName = "name"
private const val jsonDescription = "description"
private const val jsonRecipeIcon = "recipeIcon"
const val jsonSteps = "steps"

fun Recipe.serialize(steps: List<Step>? = null): JSONObject = JSONObject().run {
    put(jsonName, name)
    put(jsonDescription, description)
    put(jsonRecipeIcon, recipeIcon.name)
    put(jsonSteps, steps?.serialize())
}

fun JSONObject.toRecipe() =
    Recipe(
        name = getString(jsonName),
        description = getString(jsonDescription),
        recipeIcon = RecipeIconTypeConverter().stringToRecipeIcon(getString(jsonRecipeIcon)),
    )

fun JSONArray.toRecipes(): List<Recipe> {
    var recipies = listOf<Recipe>()
    for (i in 0 until length()) {
        recipies = recipies.plus(getJSONObject(i).toRecipe())
    }
    return recipies
}
