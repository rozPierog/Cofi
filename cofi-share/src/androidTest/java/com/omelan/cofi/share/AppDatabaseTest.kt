package com.omelan.cofi.share

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.omelan.cofi.share.model.*
import com.omelan.cofi.share.utils.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
            context,
            AppDatabase::class.java,
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
        val recipe = Recipe(name = "Test recipe1", description = "")
        val recipe2 = Recipe(name = "Test recipe2", description = "")
        runBlocking {
            recipeDao.insertAll(recipe, recipe2)
        }
        val recipeById = recipeDao.get(2)
        assertEquals(recipe2.name, recipeById.getOrAwaitValue().name)
    }

    @Test
    fun testGettingStepsForRecipe() {
        val recipe = Recipe(name = "Test recipe1", description = "")
        val recipe2 = Recipe(name = "Test recipe2", description = "")
        runBlocking {
            recipeDao.insertAll(recipe, recipe2)
        }
        val allRecipes = recipeDao.getAll()
        val firstRecipe = allRecipes.getOrAwaitValue().first()
        val lastRecipe = allRecipes.getOrAwaitValue().last()
        val stepsForFirst = StepType.entries.toTypedArray().mapIndexed { index, type ->
            Step(
                name = "$index",
                recipeId = firstRecipe.id,
                time = 5000,
                type = type,
                orderInRecipe = index,
            )
        }
        val stepsForSecond = StepType.entries.toTypedArray().mapIndexed { index, type ->
            Step(
                name = "$index",
                recipeId = lastRecipe.id,
                time = 5000,
                type = type,
                orderInRecipe = index,
            )
        }
        runBlocking {
            stepDao.insertAll(stepsForFirst + stepsForSecond)
        }
        val allStepsForRecipe1 = stepDao.getStepsForRecipe(firstRecipe.id).getOrAwaitValue()
        assertEquals(StepType.entries.size, allStepsForRecipe1.size)
        val typesOfStepsInDB: Array<StepType> = allStepsForRecipe1.map { it.type }.toTypedArray()
        assertArrayEquals(StepType.entries.toTypedArray(), typesOfStepsInDB)
    }
}
