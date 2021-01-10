package com.omelan.cofi.model

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class AppDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var recipeDao: RecipeDao
    private lateinit var stepDao: StepDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        recipeDao = db.recipeDao()
        stepDao = db.stepDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testAutoNumeration() {
        val recipe: Recipe = Recipe(name = "Test recipe1", description = "")
        val recipe2: Recipe = Recipe(name = "Test recipe2", description = "")
        runBlocking {
            recipeDao.insertAll(recipe, recipe2)
        }
        val recipeById = recipeDao.get(2)
        assertEquals(recipe2.name, recipeById.getOrAwaitValue().name)
    }

    @Test
    fun testGettingStepsForRecipe() {
        val recipe: Recipe = Recipe(name = "Test recipe1", description = "")
        val recipe2: Recipe = Recipe(name = "Test recipe2", description = "")
        runBlocking {
            recipeDao.insertAll(recipe, recipe2)
        }
        val allRecipes = recipeDao.getAll()
        val firstRecipe = allRecipes.getOrAwaitValue().first()
        val lastRecipe = allRecipes.getOrAwaitValue().last()
        val stepsForFirst = StepType.values().mapIndexed { index, type ->
            Step(name = "$index", recipeId = firstRecipe.id, time = 5000, type = type)
        }
        val stepsForSecond = StepType.values().mapIndexed { index, type ->
            Step(name = "$index", recipeId = lastRecipe.id, time = 5000, type = type)
        }
        runBlocking {
            stepDao.insertAll(stepsForFirst + stepsForSecond)
        }
        val allStepsForRecipe1 = stepDao.getStepsForRecipe(firstRecipe.id).getOrAwaitValue()
        assertEquals(StepType.values().size, allStepsForRecipe1.size)
        val typesOfStepsInDB: Array<StepType> = allStepsForRecipe1.map { it.type }.toTypedArray()
        assertArrayEquals(StepType.values(), typesOfStepsInDB)
    }
}

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    afterObserve.invoke()

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}