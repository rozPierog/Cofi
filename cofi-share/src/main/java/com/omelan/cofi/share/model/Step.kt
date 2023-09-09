package com.omelan.cofi.share.model

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.WorkerThread
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import com.omelan.cofi.share.*
import com.omelan.cofi.share.R
import org.json.JSONArray
import org.json.JSONObject

@Dao
abstract class StepDao {
    @WorkerThread
    @Query("SELECT * FROM step")
    abstract fun getAll(): LiveData<List<Step>>

    @WorkerThread
    @Query("SELECT * FROM step WHERE recipe_id IS :recipeId ORDER BY order_in_recipe ASC")
    abstract fun getStepsForRecipe(recipeId: Int): LiveData<List<Step>>

    @WorkerThread
    @Insert
    abstract suspend fun insertAll(vararg steps: Step)

    @WorkerThread
    @Insert
    abstract suspend fun insertAll(steps: List<Step>)

    @WorkerThread
    @Delete
    abstract suspend fun delete(step: Step)

    @Query("DELETE FROM step")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM step WHERE recipe_id = :recipeId")
    abstract suspend fun deleteAllStepsForRecipe(recipeId: Int)

    @Transaction
    open suspend fun deleteAndCreate(steps: List<Step>) {
        deleteAll()
        insertAll(steps)
    }
}

class StepsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    fun getAllStepsForRecipe(recipeId: Int) = db.stepDao().getStepsForRecipe(recipeId)
    fun getAllSteps() = db.stepDao().getAll()
}

enum class StepType(
    val color: Color,
    val colorNight: Color,
    @StringRes val stringRes: Int,
    @DrawableRes val iconRes: Int,
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
        iconRes = R.drawable.ic_water,
    ),
    WAIT(
        color = green600,
        colorNight = green600,
        stringRes = R.string.step_type_wait,
        iconRes = R.drawable.ic_timer,
    ),
    OTHER(
        color = greyBlue900,
        colorNight = grey300,
        stringRes = R.string.step_type_other,
        iconRes = R.drawable.ic_step_other,
    ),
    ;

    fun isNotWaitStepType(): Boolean = this != WAIT
}

open class StepTypeConverter {
    @TypeConverter
    open fun stepTypeToString(type: StepType): String {
        return type.name
    }

    @TypeConverter
    open fun stringToStepType(type: String): StepType {
        return when (type) {
            StepType.ADD_COFFEE.name -> StepType.ADD_COFFEE
            StepType.WATER.name -> StepType.WATER
            StepType.WAIT.name -> StepType.WAIT
            StepType.OTHER.name -> StepType.OTHER
            else -> StepType.OTHER
        }
    }
}

private const val jsonName = "name"
private const val jsonTime = "time"
private const val jsonType = "type"
private const val jsonOrderInRecipe = "orderInRecipe"
private const val jsonValue = "value"
private const val jsonId = "id"
private const val jsonRecipeId = "recipeId"

@Entity
data class Step(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "recipe_id") val recipeId: Int = 0,
    @ColumnInfo(name = "order_in_recipe") val orderInRecipe: Int? = null,
    val name: String,
    val time: Int? = null,
    val type: StepType,
    val value: Float? = null,
) : SharedData {
    override fun serialize(): JSONObject = JSONObject().let {
        it.put(jsonId, id)
        it.put(jsonRecipeId, recipeId)
        it.put(jsonName, name)
        it.put(jsonTime, time)
        it.put(jsonValue, value)
        it.put(jsonOrderInRecipe, orderInRecipe)
        it.put(jsonType, type.name)
        it
    }

    val isUserInputRequired
        get() = time == null
}

fun List<Step>.serialize() = JSONArray().let {
    forEach { step -> it.put(step.serialize()) }
    it
}

fun JSONObject.getIntOrNull(key: String) = try {
    getInt(key)
} catch (e: Exception) {
    null
}

fun JSONObject.getFloatOrNull(key: String) = try {
    getDouble(key).toFloat()
} catch (e: Exception) {
    null
}

fun JSONObject.toStep(withId: Boolean = false, recipeId: Long? = null) = if (withId) {
    Step(
        id = getInt(jsonId),
        name = getString(jsonName),
        recipeId = recipeId?.toInt() ?: getInt(jsonRecipeId),
        time = getIntOrNull(jsonTime),
        value = getFloatOrNull(jsonValue),
        orderInRecipe = getIntOrNull(jsonOrderInRecipe),
        type = StepTypeConverter().stringToStepType(getString(jsonType)),
    )
} else {
    Step(
        name = getString(jsonName),
        recipeId = recipeId?.toInt() ?: getInt(jsonRecipeId),
        time = getIntOrNull(jsonTime),
        value = getFloatOrNull(jsonValue),
        orderInRecipe = getIntOrNull(jsonOrderInRecipe),
        type = StepTypeConverter().stringToStepType(getString(jsonType)),
    )
}

fun JSONArray.toSteps(withId: Boolean = false, recipeId: Long? = null): List<Step> {
    var steps = listOf<Step>()
    for (i in 0 until length()) {
        steps = steps.plus(getJSONObject(i).toStep(withId, recipeId))
    }
    return steps
}
