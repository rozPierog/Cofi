package com.omelan.cofi.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // SQLite supports a limited operations for ALTER.
        // Create the new table
        database.execSQL(
            "CREATE TABLE recipe_new (" +
                "id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "last_finished INTEGER NOT NULL," +
                "icon TEXT NOT NULL," +
                "PRIMARY KEY(id))"
        )
        // Copy the data
        database.execSQL(
            "INSERT INTO recipe_new (id, name, description, last_finished) " +
                "SELECT id, name, description, last_finished " +
                "FROM recipe"
        )
        // Remove the old table
        database.execSQL("DROP TABLE recipe")
        // Change the table name to the correct one
        database.execSQL("ALTER TABLE recipe_new RENAME TO recipe")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE step ADD COLUMN order_in_recipe INTEGER")
    }
}

val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3)

@Database(entities = [Recipe::class, Step::class], version = 3)
@TypeConverters(StepTypeConverter::class, RecipeIconTypeConverter::class)
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
                    "cofi-database.db"
                )
                    .addMigrations(*ALL_MIGRATIONS)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                return instance
            }
        }
    }
}