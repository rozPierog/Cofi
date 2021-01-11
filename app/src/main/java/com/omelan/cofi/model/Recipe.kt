package com.omelan.cofi.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*

// val dummySteps = listOf(
//    Step(
//        name = "Add Coffee",
//        value = 30,
//        time = 5 * 1000,
//        type = StepType.ADD_COFFEE
//    ),
//    Step(
//        name = "Add water",
//        value = 60,
//        time = 5 * 1000,
//        type = StepType.WATER
//    ),
//    Step(name = "Swirl", time = 5 * 1000, type = StepType.OTHER),
//    Step(name = "Wait", time = 35 * 1000, type = StepType.WAIT),
//    Step(
//        name = "Add Water",
//        time = 30 * 1000,
//        type = StepType.WATER,
//        value = 300
//    ),
//    Step(
//        name = "Add Water",
//        time = 30 * 1000,
//        type = StepType.WATER,
//        value = 200
//    ),
//    Step(name = "Swirl", time = 5 * 1000, type = StepType.OTHER),
// )

enum class RecipeIcon {
    V60 {
        override val icon: Int
            get() = TODO("Not yet implemented")
    },
    FrenchPress {
        override val icon: Int
            get() = TODO("Not yet implemented")
    },
    Grinder {
        override val icon: Int
            get() = TODO("Not yet implemented")
    },
    Chemex {
        override val icon: Int
            get() = TODO("Not yet implemented")
    },
    Areopress {
        override val icon: Int
            get() = TODO("Not yet implemented")
    };

    abstract val icon: Int
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
            RecipeIcon.Areopress.name -> RecipeIcon.Areopress
            else -> RecipeIcon.Grinder
        }
    }
}

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
//    @Ignore
//    val steps: List<Step> = listOf(),
    @ColumnInfo(name = "last_finished") val lastFinished: Long = 0L,
    @ColumnInfo(name = "icon") val recipeIcon: RecipeIcon = RecipeIcon.Grinder,
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