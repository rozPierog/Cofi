package com.omelan.cofi.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
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

// val MIGRATION_3_4 = object : Migration(3, 4) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL(
//            "INSERT INTO recipe (id, name, description, last_finished, icon) VALUES" +
//                    "(0, ?, ?, 0, 'V60')", arrayOf(
//                "Ultimate V60", "Recipe created by: James Hoffmann\n" +
//                        "Source: https://www.youtube.com/watch?v=AI4ynXzkSQo\n" +
//                        "Grind size: medium fine \n" +
//                        "Temperature: the hotter, the better (especially with lighter roasts)"
//            )
//        )
//        database.execSQL(
//            "INSERT INTO step (id, recipe_id, name, time, type, order_in_recipe, value) VALUES" +
//                    "(1,0,'Add Coffee',5000,'ADD_COFFEE',0,30)"
//        )
//        database.execSQL(
//            "INSERT INTO step (id, recipe_id, name, time, type, order_in_recipe, value) VALUES" +
//                    "(2,0,'Add water',5000,'WATER',1,60)"
//        )
//        database.execSQL(
//            "INSERT INTO step (id, recipe_id, name, time, type, order_in_recipe) VALUES" +
//                    "(3,0,'Swirl',5000,'OTHER',2)"
//        )
//        database.execSQL(
//            "INSERT INTO step (id, recipe_id, name, time, type, order_in_recipe) VALUES" +
//                    "(4,0,'Wait',35000,'WAIT',3)"
//        )
//        database.execSQL(
//            "INSERT INTO step (id, recipe_id, name, time, type, order_in_recipe, value) VALUES" +
//                    "(5,0,'Add Water',30000,'WATER',4,300)"
//        )
//        database.execSQL(
//            "INSERT INTO step (id, recipe_id, name, time, type, order_in_recipe, value) VALUES" +
//                    "(6,0,'Add Water',30000,'WATER',5,200)"
//        )
//        database.execSQL(
//            "INSERT INTO step (id, recipe_id, name, time, type, order_in_recipe) VALUES" +
//                    "(7,0,'Swirl',5000,'OTHER',6)"
//        )
//    }
// }

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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val data = PrepopulateData(context)
                            data.recipes.forEach {
                                val values = ContentValues().apply {
                                    put("id", it.id)
                                    put("name", it.name)
                                    put("description", it.description)
                                    put("last_finished", it.lastFinished)
                                    put("icon", it.recipeIcon.name)
                                }
                                db.insert("recipe", SQLiteDatabase.CONFLICT_REPLACE, values)
                            }
                            data.steps.forEach {
                                val values = ContentValues().apply {
                                    put("id", it.id)
                                    put("recipe_id", it.recipeId)
                                    put("order_in_recipe", it.orderInRecipe)
                                    put("value", it.value)
                                    put("name", it.name)
                                    put("time", it.time)
                                    put("type", it.type.name)
                                }
                                db.insert("step", SQLiteDatabase.CONFLICT_REPLACE, values)
                            }
                        }
                    })
                    .addMigrations(*ALL_MIGRATIONS)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                return instance
            }
        }
    }
}