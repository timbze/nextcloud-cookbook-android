/*
 * Recipe.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json.model

import com.beust.klaxon.Json
import de.micmun.android.nextcloudcookbook.json.*

/**
 * Generated recipe data classes..
 *
 * @author MicMun
 * @version 1.0, 19.02.21
 */

data class Recipe @JvmOverloads constructor(
   @Json(ignored = true)
   val id: Long = 0L,
   @Json(name = "@context")
   val context: String? = null,
   @Json(name = "@id")
   val idUrl: String? = null,
   @Json(name = "id")
   @Recipe2String
   var jsonId: String? = null,
   @Json(name = "@type")
   val type: String? = null,
   val aggregateRating: AggregateRating? = null,
   @RecipeAuthor
   val author: Author? = null,
   val cookTime: String? = null,
   @RecipeDate
   val dateCreated: String? = null,
   @RecipeDate
   val dateModified: String? = null,
   @RecipeDate
   val datePublished: String? = null,
   val description: String? = null,
   var image: String? = null,
   var imageUrl: String? = null,
   @Json(ignored = true)
   var thumbImageUrl: String? = null,
   @Json(ignored = true)
   var fullImageUrl: String? = null,
   val keywords: String? = null,
   val name: String = "",
   @RecipeNutrition
   val nutrition: Nutrition? = null,
   val prepTime: String? = null,
   @Recipe2String
   val printImage: String? = null,
   @RecipeStringList
   var recipeCategory: List<String>? = null,
   @RecipeStringList
   val recipeIngredient: List<String>? = null,
   @RecipeStringList
   val recipeInstructions: List<String>? = null,
   @Recipe2String
   val recipeYield: String? = null,
   @Json(ignored = true)
   val review: List<Review>? = null,
   @RecipeStringList
   val tool: List<String>? = null,
   val totalTime: String? = null,
   val url: String? = null,
   @Recipe2String
   val yield: String? = null,
   val estimatedCost: String? = null
)

data class AggregateRating(
   @Json(name = "@type")
   val type: String? = null,
   @Recipe2String
   val ratingCount: String? = null,
   @Recipe2String
   val ratingValue: String? = null,
   @Recipe2String
   val reviewCount: String? = null
)

data class Author(
   @Json(name = "@type")
   val type: String? = "Person",
   val name: String? = null
)

data class Nutrition(
   @Json(name = "@type")
   val type: String? = null,
   val calories: String? = null,
   val carbohydrateContent: String? = null,
   val cholesterolContent: String? = null,
   val fatContent: String? = null,
   val fiberContent: String? = null,
   val proteinContent: String? = null,
   val sodiumContent: String? = null
)

data class Review(
   @Json(name = "@type")
   val type: String? = null,
   val author: Author? = null,
   val dateCreated: String? = null,
   val description: String? = null,
   val itemReviewed: ItemReviewed? = null
)

data class ItemReviewed(
   @Json(name = "@type")
   val type: String? = null,
   val name: String? = null
)
