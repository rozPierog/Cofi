package com.omelan.cofi.share.model

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import com.omelan.cofi.share.R
import org.json.JSONArray
import org.json.JSONObject


enum class RecipeIcon(@DrawableRes val icon: Int, @StringRes val nameResId: Int) {
    Grinder(R.drawable.recipe_icon_coffee_grinder, R.string.name_grinder),
    Cup(R.drawable.recipe_icon_cup, R.string.name_cup),
    V60(R.drawable.recipe_icon_drip, R.string.name_drip),
    Chemex(R.drawable.recipe_icon_chemex, R.string.prepopulate_chemex_name),
    AeroPress(R.drawable.recipe_icon_aeropress, R.string.prepopulate_aero_name),
    VietnamesePress(R.drawable.recipe_icon_vietnamese_press, R.string.name_vietnamese_press),
    FrenchPress(R.drawable.recipe_icon_french_press, R.string.prepopulate_frenchPress_name),
    Mokapot(R.drawable.recipe_icon_mokapot, R.string.name_mokapot),
    Espresso(R.drawable.recipe_icon_espresso, R.string.name_espresso),
    ColdBrew(R.drawable.recipe_icon_cold_brew, R.string.name_cold_brew),
    Siphon(R.drawable.recipe_icon_siphon, R.string.name_siphon),
    Bripe(R.drawable.recipe_icon_bripe, R.string.name_bripe),
    Cezve(R.drawable.recipe_icon_cezve, R.string.name_cezve),
    Tea(R.drawable.recipe_icon_tea, R.string.name_tea),
    Teapot(R.drawable.recipe_icon_teapot, R.string.name_teapot),
}

open class RecipeIconTypeConverter {
    @TypeConverter
    open fun recipeIconToString(type: RecipeIcon) = type.name.lowercase()

    @TypeConverter
    open fun stringToRecipeIcon(type: String) =
        RecipeIcon.entries.find { type.lowercase() == it.name.lowercase() } ?: RecipeIcon.Grinder
}

@Dao
abstract class RecipeDao {

    @Query("SELECT * FROM recipe ORDER BY last_finished DESC")
    abstract fun getAll(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE ID is :id")
    abstract fun get(id: Int): LiveData<Recipe>

    @Insert
    abstract suspend fun insertAll(vararg recipes: Recipe)

    @Insert
    abstract suspend fun insertAll(recipes: List<Recipe>)

    @Insert
    abstract suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    abstract suspend fun updateRecipe(recipe: Recipe)

    @Query("DELETE FROM recipe")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM recipe WHERE id = :recipeId")
    abstract suspend fun deleteById(recipeId: Int)

    @Transaction
    open suspend fun deleteAndCreate(recipes: List<Recipe>) {
        deleteAll()
        insertAll(recipes)
    }
}

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val dao = db.recipeDao()

    fun getRecipe(id: Int) = dao.get(id)

    fun getAllRecipes() = dao.getAll()
}

private const val jsonName = "name"
private const val jsonId = "id"
private const val jsonDescription = "description"
private const val jsonRecipeIcon = "recipeIcon"
private const val jsonLastFinished = "lastFinished"
const val jsonSteps = "steps"

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    @ColumnInfo(name = "last_finished") val lastFinished: Long = 0L,
    @ColumnInfo(name = "icon") val recipeIcon: RecipeIcon = RecipeIcon.Grinder,
) : SharedData {
    override fun serialize(): JSONObject = serialize(null, true)
    fun serialize(steps: List<Step>?, withLastFinished: Boolean = false): JSONObject =
        JSONObject().run {
            put(jsonId, id)
            put(jsonName, name)
            put(jsonDescription, description)
            put(jsonRecipeIcon, recipeIcon.name)
            put(jsonRecipeIcon, recipeIcon.name)
            if (withLastFinished) {
                put(jsonLastFinished, lastFinished)
            }
            put(jsonSteps, steps?.serialize())
        }
}


fun JSONObject.toRecipe(withId: Boolean = false) = if (withId) {
    Recipe(
        id = getInt(jsonId),
        name = getString(jsonName),
        description = getString(jsonDescription),
        lastFinished = optLong(jsonLastFinished, 0L),
        recipeIcon = RecipeIconTypeConverter().stringToRecipeIcon(getString(jsonRecipeIcon)),
    )
} else {
    Recipe(
        name = getString(jsonName),
        description = getString(jsonDescription),
        lastFinished = optLong(jsonLastFinished, 0L),
        recipeIcon = RecipeIconTypeConverter().stringToRecipeIcon(getString(jsonRecipeIcon)),
    )
}

fun JSONArray.toRecipes(withId: Boolean = false): List<Recipe> {
    var recipies = listOf<Recipe>()
    for (i in 0 until length()) {
        recipies = recipies.plus(getJSONObject(i).toRecipe(withId))
    }
    return recipies
}
