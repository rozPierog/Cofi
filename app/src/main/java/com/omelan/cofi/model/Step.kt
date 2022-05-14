package com.omelan.cofi.model

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.WorkerThread
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import com.omelan.cofi.R
import com.omelan.cofi.ui.*

enum class StepType(
    val color: Color,
    val colorNight: Color,
    @StringRes val stringRes: Int,
    @DrawableRes val iconRes: Int
) {
    ADD_COFFEE(
        color = brown500,
        colorNight = brown300,
        stringRes = R.string.step_type_add_coffee,
        iconRes = R.drawable.ic_coffee,
    ),
    WATER(
        color = blue600,
        colorNight = blue600,
        stringRes = R.string.step_type_water,
        iconRes = R.drawable.ic_water_plus,
    ),
    WAIT(
        color = green600,
        colorNight = green600,
        stringRes = R.string.step_type_wait,
        iconRes = R.drawable.ic_progress_clock,
    ),
    OTHER(
        color = greyBlue900,
        colorNight = grey300,
        stringRes = R.string.step_type_other,
        iconRes = R.drawable.ic_playlist_edit
    );

    fun isNotWaitStepType(): Boolean = this != WAIT
}

class StepTypeConverter {
    @TypeConverter
    fun stepTypeToString(type: StepType): String {
        return type.name
    }

    @TypeConverter
    fun stringToStepType(type: String): StepType {
        return when (type) {
            StepType.ADD_COFFEE.name -> StepType.ADD_COFFEE
            StepType.WATER.name -> StepType.WATER
            StepType.WAIT.name -> StepType.WAIT
            StepType.OTHER.name -> StepType.OTHER
            else -> StepType.OTHER
        }
    }
}

@Entity
data class Step(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "recipe_id") val recipeId: Int = 0,
    val name: String,
    val time: Int?,
    val type: StepType,
    @ColumnInfo(name = "order_in_recipe") val orderInRecipe: Int?,
    val value: Int? = null
)

@Dao
interface StepDao {
    @WorkerThread
    @Query("SELECT * FROM step")
    suspend fun getAll(): List<Step>

    @WorkerThread
    @Query("SELECT * FROM step WHERE recipe_id IS :recipeId ORDER BY order_in_recipe ASC")
    fun getStepsForRecipe(recipeId: Int): LiveData<List<Step>>

    @WorkerThread
    @Insert
    suspend fun insertAll(vararg steps: Step)

    @WorkerThread
    @Insert
    suspend fun insertAll(steps: List<Step>)

    @WorkerThread
    @Delete
    suspend fun delete(step: Step)

    @Query("DELETE FROM step WHERE recipe_id = :recipeId")
    suspend fun deleteAllStepsForRecipe(recipeId: Int)
}

class StepsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    fun getAllStepsForRecipe(recipeId: Int) = db.stepDao().getStepsForRecipe(recipeId)
}