package com.omelan.cofi.model

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import com.omelan.cofi.share.StepShared
import com.omelan.cofi.share.StepType
import com.omelan.cofi.share.StepTypeConverter as StepTypeConverterShared


class StepTypeConverter : StepTypeConverterShared() {
    @TypeConverter
    override fun stepTypeToString(type: StepType): String {
        return super.stepTypeToString(type)
    }

    @TypeConverter
    override fun stringToStepType(type: String): StepType {
        return super.stringToStepType(type)
    }
}

@Entity
data class Step(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    @ColumnInfo(name = "recipe_id") override val recipeId: Int = 0,
    @ColumnInfo(name = "order_in_recipe") override val orderInRecipe: Int? = null,
    override val name: String,
    override val type: StepType,
    override val time: Int?,
    override val value: Int? = null,
) : StepShared(
    id = id,
    recipeId = recipeId,
    orderInRecipe = orderInRecipe,
    name = name,
    type = type,
    time = time,
    value = value,
)

fun StepShared.toDBStep() = Step(id, recipeId, orderInRecipe, name, type, time, value)

@Dao
interface StepDao {
    @WorkerThread
    @Query("SELECT * FROM step")
    fun getAll(): LiveData<List<Step>>

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
    fun getAllSteps() = db.stepDao().getAll()
}
