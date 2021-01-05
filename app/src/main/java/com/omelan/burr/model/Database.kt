package com.omelan.burr.model

import android.content.Context
import androidx.room.*

@Database(entities = [Recipe::class, Step::class], version = 1)
@TypeConverters(StepTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
    abstract fun recipeDao(): RecipeDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "burr-database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                return instance
            }
        }
    }
}