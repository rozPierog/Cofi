package com.omelan.cofi.share

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import com.omelan.cofi.share.model.AppDatabase
import com.omelan.cofi.share.model.SharedData
import org.json.JSONArray
import org.json.JSONObject


enum class RecipeIcon(@DrawableRes val icon: Int, @StringRes val nameResId: Int) {
    Grinder(R.drawable.ic_coffee_grinder, R.string.name_grinder),
    Cup(R.drawable.ic_cup, R.string.name_cup),
    V60(R.drawable.ic_drip, R.string.name_v60),
    Chemex(R.drawable.ic_chemex, R.string.prepopulate_chemex_name),
    AeroPress(R.drawable.ic_aeropress, R.string.prepopulate_aero_name),
    VietnamesePress(R.drawable.ic_vietnamese_press, R.string.name_vietnamese_press),
    FrenchPress(R.drawable.ic_french_press, R.string.prepopulate_frenchPress_name),
    Mokapot(R.drawable.ic_mokapot, R.string.name_mokapot),
    Espresso(R.drawable.ic_espresso, R.string.name_espresso),
    ColdBrew(R.drawable.ic_cold_brew, R.string.name_cold_brew),
    Siphon(R.drawable.ic_siphon, R.string.name_siphon),
    Bripe(R.drawable.ic_bripe, R.string.name_bripe),
    Cezve(R.drawable.ic_cezve, R.string.name_cezve),
}

open class RecipeIconTypeConverter {
    @TypeConverter
    open fun recipeIconToString(type: RecipeIcon) = type.name.lowercase()

    @TypeConverter
    open fun stringToRecipeIcon(type: String) =
        RecipeIcon.values().find { type.lowercase() == it.name.lowercase() } ?: RecipeIcon.Grinder
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
const val jsonSteps = "steps"

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    @ColumnInfo(name = "last_finished") val lastFinished: Long = 0L,
    @ColumnInfo(name = "icon") val recipeIcon: RecipeIcon = RecipeIcon.Grinder,
) : SharedData {
    override fun serialize(): JSONObject = serialize(null)
    fun serialize(steps: List<Step>?): JSONObject = JSONObject().run {
        put(jsonId, id)
        put(jsonName, name)
        put(jsonDescription, description)
        put(jsonRecipeIcon, recipeIcon.name)
        put(jsonSteps, steps?.serialize())
    }
}


fun JSONObject.toRecipe(withId: Boolean = false) = if (withId) {
    Recipe(
        id = getInt(jsonId),
        name = getString(jsonName),
        description = getString(jsonDescription),
        recipeIcon = RecipeIconTypeConverter().stringToRecipeIcon(getString(jsonRecipeIcon)),
    )
} else {
    Recipe(
        name = getString(jsonName),
        description = getString(jsonDescription),
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
