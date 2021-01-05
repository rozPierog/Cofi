package com.omelan.burr.model

import androidx.annotation.WorkerThread
import androidx.compose.ui.graphics.Color
import androidx.room.*
import com.omelan.burr.ui.brown
import com.omelan.burr.ui.green


enum class StepType {
    ADD_COFFEE {
        override fun getColor(): Color {
            return brown
        }
    },
    WATER {
        override fun getColor(): Color {
            return Color.Blue
        }
    },
    WAIT {
        override fun getColor(): Color {
            return green
        }
    },
    OTHER {
        override fun getColor(): Color {
            return Color.Gray
        }
    };

    abstract fun getColor(): Color
}

class StepTypeConverter {
    @TypeConverter
    fun stepTypeToString(type: StepType): String {
        return type.name
    }

    @TypeConverter
    fun stringToStepType(type:String): StepType {
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
    val recipeId: Int = 0,
    val name: String,
    val time: Int,
    val type: StepType,
    val value: Int? = null
)

@Dao
interface StepDao {
    @WorkerThread
    @Query("SELECT * FROM step")
    suspend fun getAll(): List<Step>

    @WorkerThread
    @Insert
    suspend fun insertAll(vararg steps: Step)

    @WorkerThread
    @Insert
    suspend fun insertAll(steps: List<Step>)

    @WorkerThread
    @Delete
    suspend fun delete(step: Step)
}