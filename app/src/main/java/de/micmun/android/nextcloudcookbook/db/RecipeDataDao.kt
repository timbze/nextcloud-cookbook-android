/*
 * RecipeDataDao.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import de.micmun.android.nextcloudcookbook.db.model.*

/**
 * Interface for database operations.
 *
 * @author MicMun
 * @version 1.1, 24.04.21
 */
@Dao
interface RecipeDataDao {
   @Transaction
   @Query("SELECT * FROM recipes ORDER BY name ASC")
   fun getAllRecipes(): LiveData<List<DbRecipe>>

   @Transaction
   @Query("SELECT * FROM recipes ORDER BY " +
          "CASE WHEN :isAsc = 1 THEN name END ASC," +
          "CASE WHEN :isAsc = 0 THEN name END DESC")
   fun sortByName(isAsc: Boolean): LiveData<List<DbRecipe>>

   @Transaction
   @Query("SELECT * FROM recipes ORDER BY " +
          "CASE WHEN :isAsc = 1 THEN datePublished END ASC," +
          "CASE WHEN :isAsc = 0 THEN datePublished END DESC")
   fun sortByDate(isAsc: Boolean): LiveData<List<DbRecipe>>

   @Transaction
   @Query("SELECT * FROM recipes ORDER BY " +
          "CASE WHEN :isAsc = 1 THEN totalTime END ASC," +
          "CASE WHEN :isAsc = 0 THEN totalTime END DESC")
   fun sortByTotalTime(isAsc: Boolean): LiveData<List<DbRecipe>>

   @Transaction
   @Query("SELECT * FROM recipes WHERE name = :n")
   fun getByName(n: String): LiveData<DbRecipe?>

   @Transaction
   @Query("SELECT * FROM recipes WHERE name = :n")
   fun findByName(n: String): DbRecipe?

   @Transaction
   @Query("SELECT * FROM keywords WHERE keyword IN(:n)")
   fun findKeywords(n: List<String>): List<DbKeyword>?

   @Transaction
   @Query("SELECT * FROM keywords ORDER BY keyword")
   fun getAllKeywords(): LiveData<List<DbKeyword>>

   @Transaction
   @RawQuery(observedEntities = [DbRecipeCore::class, DbInstruction::class, DbIngredient::class, DbTool::class,
      DbReview::class, DbKeyword::class, DbRecipeKeywordRelation::class])
   fun filterRecipes(query: SupportSQLiteQuery): LiveData<List<DbRecipe>>

   @Transaction
   @Query("SELECT DISTINCT recipeCategory FROM recipes WHERE recipeCategory != '' ORDER BY recipeCategory")
   fun getCategories(): LiveData<List<String>>

   @Insert
   fun insert(recipe: DbRecipeCore): Long

   @Insert
   fun insertTools(tool: List<DbTool>)

   @Insert
   fun insertReviews(review: List<DbReview>)

   @Insert
   fun insertInstructions(instruction: List<DbInstruction>)

   @Insert
   fun insertIngredients(ingredient: List<DbIngredient>)

   @Insert(onConflict = OnConflictStrategy.IGNORE)
   fun insertKeywords(keywords: List<DbKeyword>)

   @Insert(onConflict = OnConflictStrategy.IGNORE)
   fun insertKeywordRefs(keywords: List<DbRecipeKeywordRelation>)

   @Update
   fun update(recipe: DbRecipeCore)

   @Update
   fun updateTools(tool: List<DbTool>)

   @Update
   fun updateReviews(review: List<DbReview>)

   @Update
   fun updateInstructions(instruction: List<DbInstruction>)

   @Update
   fun updateIngredients(ingredient: List<DbIngredient>)

   @Delete
   fun delete(recipe: DbRecipeCore)

   @Delete
   fun deleteTools(tool: List<DbTool>)

   @Delete
   fun deleteReviews(review: List<DbReview>)

   @Delete
   fun deleteInstructions(instruction: List<DbInstruction>)

   @Delete
   fun deleteIngredients(ingredient: List<DbIngredient>)
}
