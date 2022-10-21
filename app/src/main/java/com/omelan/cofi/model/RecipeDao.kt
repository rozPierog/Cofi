package com.omelan.cofi.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import com.omelan.cofi.share.RecipeIcon
import com.omelan.cofi.share.RecipeShared
import com.omelan.cofi.share.RecipeIconTypeConverter as RecipeIconTypeConverterShared

class RecipeIconTypeConverter : RecipeIconTypeConverterShared() {
    @TypeConverter
    override fun stringToRecipeIcon(type: String): RecipeIcon {
        return super.stringToRecipeIcon(type)
    }

    @TypeConverter
    override fun recipeIconToString(type: RecipeIcon): String {
        return super.recipeIconToString(type)
    }
}

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val name: String,
    override val description: String,
    @ColumnInfo(name = "last_finished") override val lastFinished: Long = 0L,
    @ColumnInfo(name = "icon") override val recipeIcon: RecipeIcon = RecipeIcon.Grinder,
) : RecipeShared(
    id = id,
    name = name,
    description = description,
    lastFinished = lastFinished,
    recipeIcon = recipeIcon,
)

fun RecipeShared.toDBRecipe() = Recipe(id, name, description, lastFinished, recipeIcon)

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
