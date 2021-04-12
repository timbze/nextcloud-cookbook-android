/*
 * Recipe2DbRecipeConverter.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import de.micmun.android.nextcloudcookbook.db.model.*
import de.micmun.android.nextcloudcookbook.json.model.*
import java.util.stream.Collectors

/**
 * Converter for recipe into database recipe pojo.
 *
 * @author MicMun
 * @version 1.0, 19.02.21
 */
class Recipe2DbRecipeConverter(private val recipe: Recipe) {
   fun convert(): DbRecipe {
      val core = DbRecipeCore(
         context = cns(recipe.context),
         idUrl = cns(recipe.idUrl),
         jsonId = cns(recipe.jsonId),
         type = cns(recipe.type),
         cookTime = cns(recipe.cookTime),
         dateCreated = cns(recipe.dateCreated),
         dateModified = cns(recipe.dateModified),
         datePublished = cns(recipe.datePublished),
         description = cns(recipe.description),
         image = cns(recipe.image),
         imageUrl = cns(recipe.imageUrl),
         thumbImageUrl = cns(recipe.thumbImageUrl),
         fullImageUrl = cns(recipe.fullImageUrl),
         name = recipe.name,
         prepTime = cns(recipe.prepTime),
         printImage = cns(recipe.printImage),
         recipeCategory = recipe.recipeCategory?.joinToString(",") ?: "",
         recipeYield = cns(recipe.recipeYield),
         totalTime = cns(recipe.totalTime),
         url = cns(recipe.url),
         yield = cns(recipe.yield),
         estimatedCost = cns(recipe.estimatedCost),
         aggregateRating = getAggregatRating(recipe.aggregateRating),
         author = getAuthor(recipe.author),
         nutrition = getNutrition(recipe.nutrition)
      )

      return DbRecipe(
         core,
         tool = getTools(recipe.tool),
         review = getReview(recipe.review),
         recipeIngredient = getIngredients(recipe.recipeIngredient),
         recipeInstructions = getInstructions(recipe.recipeInstructions),
         keywords = recipe.keywords?.splitToSequence(",")?.map { str -> DbKeyword(keyword = str) }?.toList(),
      )
   }

   private fun cns(value: String?): String {
      return value ?: ""
   }

   private fun getAggregatRating(aggregateRating: AggregateRating?): DbAggregateRating? {
      var aggRat: DbAggregateRating? = null

      if (aggregateRating != null) {
         aggRat = DbAggregateRating(
            cns(aggregateRating.type),
            cns(aggregateRating.ratingCount),
            cns(aggregateRating.ratingValue),
            cns(aggregateRating.reviewCount)
         )
      }

      return aggRat
   }

   private fun getAuthor(author: Author?): DbAuthor? {
      var value: DbAuthor? = null

      if (author != null) {
         value = DbAuthor(cns(author.type), cns(author.name))
      }

      return value
   }

   private fun getNutrition(nutrition: Nutrition?): DbNutrition? {
      var value: DbNutrition? = null

      if (nutrition != null) {
         value = DbNutrition(
            cns(nutrition.type),
            cns(nutrition.calories),
            cns(nutrition.carbohydrateContent),
            cns(nutrition.cholesterolContent),
            cns(nutrition.fatContent),
            cns(nutrition.fiberContent),
            cns(nutrition.proteinContent),
            cns(nutrition.sodiumContent)
         )
      }

      return value
   }

   private fun getReview(reviews: List<Review>?): List<DbReview>? {
      var value: List<DbReview>? = null

      if (reviews != null) {
         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            value = reviews.stream()
               .map { t ->
                  DbReview(
                     type = cns(t.type),
                     dateCreated = cns(t.dateCreated),
                     description = cns(t.description),
                     author = getAuthor(t.author),
                     itemReviewed = getItemReviewed(t.itemReviewed)
                  )
               }
               .collect(Collectors.toList())
         } else {
            value = mutableListOf()
            reviews.forEach { t ->
               val dbReview = DbReview(
                  type = cns(t.type),
                  dateCreated = cns(t.dateCreated),
                  description = cns(t.description),
                  author = getAuthor(t.author),
                  itemReviewed = getItemReviewed(t.itemReviewed)
               )
               value.add(dbReview)
            }
         }
      }

      return value
   }

   private fun getItemReviewed(itemReviewed: ItemReviewed?): DbItemReviewed? {
      var value: DbItemReviewed? = null

      if (itemReviewed != null) {
         value = DbItemReviewed(cns(itemReviewed.type), cns(itemReviewed.name))
      }

      return value
   }

   private fun getTools(tools: List<String>?): List<DbTool>? {
      var value: List<DbTool>? = null

      if (tools != null) {
         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            value = tools.stream()
               .map { DbTool(tool = it) }
               .collect(Collectors.toList())
         } else {
            value = mutableListOf()
            tools.forEach { t ->
               val dbTool = DbTool(tool = t)
               value.add(dbTool)
            }
         }
      }

      return value
   }

   private fun getIngredients(ingredients: List<String>?): List<DbIngredient>? {
      var value: List<DbIngredient>? = null

      if (ingredients != null) {
         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            value = ingredients.stream()
               .map { DbIngredient(ingredient = it) }
               .collect(Collectors.toList())
         } else {
            value = mutableListOf()
            ingredients.forEach { t ->
               val dbIng = DbIngredient(ingredient = t)
               value.add(dbIng)
            }
         }
      }

      return value
   }

   private fun getInstructions(instructions: List<String>?): List<DbInstruction>? {
      var value: List<DbInstruction>? = null

      if (instructions != null) {
         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            value = instructions.stream()
               .map { DbInstruction(instruction = it) }
               .collect(Collectors.toList())
         } else {
            value = mutableListOf()
            instructions.forEach { t ->
               val dbIns = DbInstruction(instruction = t)
               value.add(dbIns)
            }
         }
      }

      return value
   }
}
