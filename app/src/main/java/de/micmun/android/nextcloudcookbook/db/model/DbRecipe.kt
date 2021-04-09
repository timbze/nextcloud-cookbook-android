/*
 * DbRecipe.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.db.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import de.micmun.android.nextcloudcookbook.R.string.*

/**
 * Database entity for recipe.
 *
 * @author MicMun
 * @version 1.0, 20.03.21
 */
data class DbRecipe(
   @Embedded val recipeCore: DbRecipeCore,
   @Relation(parentColumn = "id", entityColumn = "recipeId")
   val review: List<DbReview>? = null,
   @Relation(parentColumn = "id", entityColumn = "recipeId")
   val tool: List<DbTool>? = null,
   @Relation(parentColumn = "id", entityColumn = "recipeId")
   val recipeIngredient: List<DbIngredient>? = null,
   @Relation(parentColumn = "id", entityColumn = "recipeId")
   val recipeInstructions: List<DbInstruction>? = null
)

@Entity(tableName = "recipes")
data class DbRecipeCore(
   @PrimaryKey(autoGenerate = true) var id: Long = 0L,

   val context: String = "",
   val idUrl: String = "",
   val jsonId: String = "",
   val type: String = "",
   @Embedded(prefix = "agrat_") val aggregateRating: DbAggregateRating?,
   @Embedded(prefix = "author_") val author: DbAuthor?,
   val cookTime: String = "",
   val dateCreated: String = "",
   val dateModified: String = "",
   val datePublished: String = "",
   val description: String = "",
   val image: String = "",
   val imageUrl: String = "",
   val thumbImageUrl: String = "",
   val fullImageUrl: String = "",
   val keywords: String = "",
   val name: String,
   @Embedded(prefix = "nutrition_") val nutrition: DbNutrition?,
   val prepTime: String = "",
   val printImage: String = "",
   val recipeCategory: String = "",
   val recipeYield: String = "",
   val totalTime: String = "",
   val url: String = "",
   val yield: String = "",
   val estimatedCost: String = ""
)

data class DbAggregateRating(
   val type: String = "",
   val ratingCount: String = "",
   val ratingValue: String = "",
   val reviewCount: String = ""
)

data class DbAuthor(
   val type: String = "Person",
   val name: String = ""
)

data class DbNutrition(
   val type: String = "",
   val calories: String = "",
   val carbohydrateContent: String = "",
   val cholesterolContent: String = "",
   val fatContent: String = "",
   val fiberContent: String = "",
   val proteinContent: String = "",
   val sodiumContent: String = "",
   val saturatedFatContent: String = "",
   val servingSize: String = "",
   val sugarContent: String = "",
   val transFatContent: String = "",
   val unsaturatedFatContent: String = ""
) {
   /**
    * Returns a map with the nutritions which are set.
    *
    * @return Map with nutritions which are set.
    */
   fun toMap(): Map<Int, String> {
      val map = mutableMapOf<Int, String>()

      if (calories.isNotEmpty())
         map[nutrition_calories] = calories
      if (carbohydrateContent.isNotEmpty())
         map[nutrition_carbohydrate_content] = carbohydrateContent
      if (cholesterolContent.isNotEmpty())
         map[nutrition_cholesterol_content] = cholesterolContent
      if (fatContent.isNotEmpty())
         map[nutrition_fat_content] = fatContent
      if (fiberContent.isNotEmpty())
         map[nutrition_fiberContent] = fiberContent
      if (proteinContent.isNotEmpty())
         map[nutrition_protein_content] = proteinContent
      if (saturatedFatContent.isNotEmpty())
         map[nutrition_saturated_fat_content] = saturatedFatContent
      if (servingSize.isNotEmpty())
         map[nutrition_serving_size] = servingSize
      if (sodiumContent.isNotEmpty())
         map[nutrition_sodium_content] = sodiumContent
      if (sugarContent.isNotEmpty())
         map[nutrition_sugar_content] = sugarContent
      if (transFatContent.isNotEmpty())
         map[nutrition_trans_fat_content] = transFatContent
      if (unsaturatedFatContent.isNotEmpty())
         map[nutrition_unsaturated_fat_content] = unsaturatedFatContent

      return map
   }
}

@Entity(tableName = "reviews")
data class DbReview(
   @PrimaryKey(autoGenerate = true)
   val id: Long = 0L,
   @ForeignKey(
      entity = DbRecipeCore::class,
      parentColumns = ["id"],
      childColumns = ["recipeId"],
      onDelete = CASCADE
   )
   var recipeId: Long = -1,
   val type: String = "",
   @Embedded(prefix = "author_") val author: DbAuthor? = null,
   val dateCreated: String = "",
   val description: String = "",
   @Embedded(prefix = "itrev_") val itemReviewed: DbItemReviewed? = null
)

data class DbItemReviewed(
   val type: String = "",
   val name: String = ""
)

@Entity(tableName = "tools")
data class DbTool(
   @PrimaryKey(autoGenerate = true)
   val id: Long = 0L,
   @ForeignKey(
      entity = DbRecipeCore::class,
      parentColumns = ["id"],
      childColumns = ["recipeId"],
      onDelete = CASCADE
   )
   var recipeId: Long = -1,
   val tool: String
)

@Entity(tableName = "ingredients")
data class DbIngredient(
   @PrimaryKey(autoGenerate = true)
   val id: Long = 0L,
   @ForeignKey(
      entity = DbRecipeCore::class,
      parentColumns = ["id"],
      childColumns = ["recipeId"],
      onDelete = CASCADE
   )
   var recipeId: Long = -1,
   val ingredient: String
)

@Entity(tableName = "instructions")
data class DbInstruction(
   @PrimaryKey(autoGenerate = true)
   val id: Long = 0L,
   @ForeignKey(
      entity = DbRecipeCore::class,
      parentColumns = ["id"],
      childColumns = ["recipeId"],
      onDelete = CASCADE
   )
   var recipeId: Long = -1,
   val instruction: String
)
