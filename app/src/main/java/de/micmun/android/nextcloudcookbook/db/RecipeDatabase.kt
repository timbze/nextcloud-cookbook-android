/*
 * RecipeDatabase.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.micmun.android.nextcloudcookbook.db.model.*
import java.util.concurrent.Executors

/**
 * Database for recipes.
 *
 * @author MicMun
 * @version 1.0, 19.02.21
 */
@Database(
   entities = [DbRecipeCore::class, DbInstruction::class, DbIngredient::class,
      DbTool::class, DbKeyword::class, DbRecipeKeywordRelation::class, DbReview::class],
   version = 4, exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
   abstract fun recipeDataDao(): RecipeDataDao

   companion object {
      @Volatile
      private var INSTANCE: RecipeDatabase? = null
      private val NUMBER_OF_THREADS = 4
      val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

      fun getDatabase(context: Context): RecipeDatabase {
         synchronized(this) {
            var instance = INSTANCE

            if (instance == null) {
               instance = Room
                  .databaseBuilder(context.applicationContext, RecipeDatabase::class.java, "recipe-db")
                  .fallbackToDestructiveMigration()
                  .build()
               INSTANCE = instance
            }
            return instance
         }
      }
   }
}
