/*
 * JsonRecipeRepository.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.db

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.data.SortValue
import de.micmun.android.nextcloudcookbook.db.model.DbRecipe
import de.micmun.android.nextcloudcookbook.db.model.DbRecipeKeywordRelation
import de.micmun.android.nextcloudcookbook.db.model.DbRecipePreview
import de.micmun.android.nextcloudcookbook.db.model.DbRecipeStar

/**
 * Repository for recipes.
 *
 * @author MicMun
 * @version 1.3, 24.04.21
 */
class DbRecipeRepository private constructor(application: Application) {
   private var mRecipeDao: RecipeDataDao = RecipeDatabase.getDatabase(application).recipeDataDao()
   // we prepend 'recipes.' to resolve name ambiguities (e.g. column 'id')
   private val dbPreviewFields = DbRecipePreview.DbFields.split(", ").joinToString(", ") { "recipes.$it" }

   companion object {
      @Volatile
      private var INSTANCE: DbRecipeRepository? = null

      fun getInstance(application: Application): DbRecipeRepository {
         synchronized(this) {
            var instance = INSTANCE

            if (instance == null) {
               instance = DbRecipeRepository(application)
               INSTANCE = instance
            }

            return instance
         }
      }
   }

   fun getAllRecipePreviews() = mRecipeDao.getAllRecipePreviews()

   fun getRecipe(id: Long) = mRecipeDao.getById(id)

   fun filterCategory(sort: SortValue, category: String, recipeFilter: RecipeFilter? = null): LiveData<List<DbRecipePreview>> {
      var select = "SELECT $dbPreviewFields FROM recipes WHERE recipeCategory = '${category}' "
      if (recipeFilter != null && recipeFilter.type != RecipeFilter.QueryType.QUERY_INGREDIENTS) {
         select += " AND " + getWhereClause(recipeFilter)
      } else if (recipeFilter != null) {
         select =
            "SELECT $dbPreviewFields FROM recipes INNER JOIN ingredients ON recipes.id = ingredients.recipeId" +
            " WHERE recipeCategory REGEXP '(^|,)\\s*${category} AND " + getWhereClause(recipeFilter)
      }
      select += " ORDER BY " + getOrderBy(sort)

      val args = if (recipeFilter != null) arrayOf(recipeFilter.query) else null
      val query = SimpleSQLiteQuery(select, args)
      return mRecipeDao.filterRecipes(query)
   }

   fun filterUncategorized(sort: SortValue, recipeFilter: RecipeFilter? = null): LiveData<List<DbRecipePreview>> {
      var select = "SELECT $dbPreviewFields FROM recipes WHERE recipeCategory = ''"
      if (recipeFilter != null && recipeFilter.type != RecipeFilter.QueryType.QUERY_INGREDIENTS) {
         select += " AND " + getWhereClause(recipeFilter)
      } else if (recipeFilter != null) {
         select =
            "SELECT $dbPreviewFields FROM recipes INNER JOIN ingredients ON recipes.id = ingredients.recipeId" +
            " WHERE recipeCategory = '' AND " + getWhereClause(recipeFilter)
      }

      select += " ORDER BY " + getOrderBy(sort)

      val args = if (recipeFilter != null) arrayOf(recipeFilter.query) else null

      val query = SimpleSQLiteQuery(select, args)
      return mRecipeDao.filterRecipes(query)
   }

   fun filterAll(sort: SortValue, recipeFilter: RecipeFilter): LiveData<List<DbRecipePreview>> {
      var select = when (recipeFilter.type) {
         RecipeFilter.QueryType.QUERY_KEYWORD -> "SELECT $dbPreviewFields FROM recipes" +
                                        " INNER JOIN recipeXKeywords x ON x.recipeId = recipes.id" +
                                        " INNER JOIN keywords k ON k.id = x.keywordId" +
                                        " WHERE " + getWhereClause(recipeFilter)
         RecipeFilter.QueryType.QUERY_INGREDIENTS -> "SELECT $dbPreviewFields FROM recipes INNER JOIN ingredients ON recipes.id = ingredients.recipeId" +
                                                     " WHERE " + getWhereClause(recipeFilter)
         else -> "SELECT $dbPreviewFields FROM recipes WHERE " + getWhereClause(recipeFilter)
      }

      select += " ORDER BY " + getOrderBy(sort)

      val args = arrayOf(recipeFilter.query)

      val query = SimpleSQLiteQuery(select, args)
      return mRecipeDao.filterRecipes(query)
   }

   fun getKeywords() = mRecipeDao.getAllKeywords()

   fun sort(sort: SortValue): LiveData<List<DbRecipePreview>> {
      return when (sort) {
         SortValue.NAME_A_Z -> mRecipeDao.sortByName(true)
         SortValue.NAME_Z_A -> mRecipeDao.sortByName(false)
         SortValue.DATE_ASC -> mRecipeDao.sortByDate(true)
         SortValue.DATE_DESC -> mRecipeDao.sortByDate(false)
         SortValue.TOTAL_TIME_ASC -> mRecipeDao.sortByTotalTime(true)
         SortValue.TOTAL_TIME_DESC -> mRecipeDao.sortByTotalTime(false)
      }
   }

   fun getCategories(): LiveData<List<String>> = mRecipeDao.getCategories()

   fun insertAll(recipes: List<DbRecipe>) {
      RecipeDatabase.databaseWriteExecutor.execute {
         recipes.forEach { recipe ->
            val r = mRecipeDao.findByName(recipe.recipeCore.name)

            if (r == null) {
               val id = mRecipeDao.insert(recipe.recipeCore)
               setIdInLists(recipe, id)

               recipe.tool?.let { mRecipeDao.insertTools(it) }
               recipe.review?.let { mRecipeDao.insertReviews(it) }
               recipe.recipeInstructions?.let { mRecipeDao.insertInstructions(it) }
               recipe.recipeIngredient?.let { mRecipeDao.insertIngredients(it) }
               updateKeywords(recipe, id)
            } else {
               val id = r.recipeCore.id
               recipe.recipeCore.id = id
               setIdInLists(recipe, id)

               mRecipeDao.update(recipe.recipeCore)
               updateStar(recipe.recipeCore.id, r.recipeCore.starred)
               recipe.tool?.let { mRecipeDao.updateTools(it) }
               recipe.review?.let { mRecipeDao.updateReviews(it) }
               recipe.recipeInstructions?.let { mRecipeDao.updateInstructions(it) }
               recipe.recipeIngredient?.let { mRecipeDao.updateIngredients(it) }
               updateKeywords(recipe, id)
            }
         }
      }
   }

   fun insert(recipe: DbRecipe) {
      RecipeDatabase.databaseWriteExecutor.execute {
         val identifier: Long = mRecipeDao.insert(recipe.recipeCore)
         setIdInLists(recipe, identifier)

         recipe.tool?.let { mRecipeDao.insertTools(it) }
         recipe.review?.let { mRecipeDao.insertReviews(it) }
         recipe.recipeInstructions?.let { mRecipeDao.insertInstructions(it) }
         recipe.recipeIngredient?.let { mRecipeDao.insertIngredients(it) }
      }
   }

   fun update(recipe: DbRecipe) {
      RecipeDatabase.databaseWriteExecutor.execute {
         mRecipeDao.update(recipe.recipeCore)
         recipe.tool?.let { mRecipeDao.updateTools(it) }
         recipe.review?.let { mRecipeDao.updateReviews(it) }
         recipe.recipeInstructions?.let { mRecipeDao.updateInstructions(it) }
         recipe.recipeIngredient?.let { mRecipeDao.updateIngredients(it) }
      }
   }

   fun updateStar(recipeId: Long, starred: Boolean) {
      RecipeDatabase.databaseWriteExecutor.execute {
         mRecipeDao.updateStar(DbRecipeStar(recipeId, starred))
      }
   }

   fun delete(recipe: DbRecipe) {
      RecipeDatabase.databaseWriteExecutor.execute {
         recipe.tool?.let { mRecipeDao.deleteTools(it) }
         recipe.review?.let { mRecipeDao.deleteReviews(it) }
         recipe.recipeInstructions?.let { mRecipeDao.deleteInstructions(it) }
         recipe.recipeIngredient?.let { mRecipeDao.deleteIngredients(it) }
         mRecipeDao.delete(recipe.recipeCore)
      }
   }

   private fun getWhereClause(recipeFilter: RecipeFilter): String {
      val upper = if (recipeFilter.ignoreCase) "UPPER(%s) " else "%s "

      var sql = when (recipeFilter.type) {
         RecipeFilter.QueryType.QUERY_NAME -> upper.format("name")
         RecipeFilter.QueryType.QUERY_KEYWORD -> upper.format("keyword")
         RecipeFilter.QueryType.QUERY_YIELD -> upper.format("recipeYield")
         RecipeFilter.QueryType.QUERY_INGREDIENTS -> upper.format("ingredient")
      }
      val operator = if (recipeFilter.exact) "= " else "LIKE '%' || "

      sql += operator
      sql += upper.format("?")

      if (operator != "= ")
         sql += "|| '%' "

      return sql
   }

   private fun getOrderBy(sort: SortValue): String {
      return "starred DESC, " + when (sort) {
         SortValue.NAME_A_Z -> "name asc"
         SortValue.NAME_Z_A -> "name desc"
         SortValue.DATE_ASC -> "datePublished asc"
         SortValue.DATE_DESC -> "datePublished desc"
         SortValue.TOTAL_TIME_ASC -> "totalTime asc"
         SortValue.TOTAL_TIME_DESC -> "totalTime desc"
      }
   }

   /**
    * Sets the recipeId in every relation.
    */
   private fun setIdInLists(recipe: DbRecipe, id: Long) {
      recipe.tool?.let { t ->
         t.forEach { it.recipeId = id }
      }

      recipe.review?.let { reviews ->
         reviews.forEach { it.recipeId = id }
      }
      recipe.recipeInstructions?.let { ins ->
         ins.forEach { it.recipeId = id }
      }
      recipe.recipeIngredient?.let { ing ->
         ing.forEach { it.recipeId = id }
      }
   }

   private fun updateKeywords(recipe: DbRecipe, recipeId: Long) {
      recipe.keywords?.let {
         if (it.isNotEmpty()) {
            mRecipeDao.insertKeywords(it)
            mRecipeDao.findKeywords(it.map { kw -> kw.keyword })?.let {
               mRecipeDao.insertKeywordRefs(
                  it.map { kw -> DbRecipeKeywordRelation(recipeId = recipeId, keywordId = kw.id) })
            }
         }
      }
   }
}