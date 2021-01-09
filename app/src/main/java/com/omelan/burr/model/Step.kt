package com.omelan.burr.model

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import com.omelan.burr.R
import com.omelan.burr.ui.blue600
import com.omelan.burr.ui.brown500
import com.omelan.burr.ui.green600
import com.omelan.burr.ui.greyBlue900

enum class StepType {
    ADD_COFFEE {
        override val color: Color
            get() = brown500

        override val stringRes: Int
            get() = R.string.step_type_add_coffee
    },
    WATER {
        override val color: Color
            get() = blue600

        override val stringRes: Int
            get() = R.string.step_type_water
    },
    WAIT {
        override val color: Color
            get() = green600

        override val stringRes: Int
            get() = R.string.step_type_wait
    },
    OTHER {
        override val color: Color
            get() = greyBlue900

        override val stringRes: Int
            get() = R.string.step_type_other
    };

    abstract val color: Color
    abstract val stringRes: Int
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
    val time: Int,
    val type: StepType,
    // TODO: Add ordering of steps
//    val order: Int,
    val value: Int? = null
)

@Dao
interface StepDao {
    @WorkerThread
    @Query("SELECT * FROM step")
    suspend fun getAll(): List<Step>

    @WorkerThread
    @Query("SELECT * FROM step WHERE recipe_id IS :recipeId")
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

    @Update
    suspend fun updateSteps(steps: List<Step>)

    @Query("DELETE FROM step WHERE recipe_id = :recipeId")
    suspend fun deleteAllStepsForRecipe(recipeId: Int)
}

class StepsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    fun getAllStepsForRecipe(recipeId: Int) = db.stepDao().getStepsForRecipe(recipeId)
}