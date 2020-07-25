/*
 * Recipe.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data.model

import android.net.Uri
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.RecipeDateDeserializer
import de.micmun.android.nextcloudcookbook.data.RecipeStringArrayDeserializer
import java.util.*

/**
 * Data class for a recipe.
 *
 * @author MicMun
 * @version 1.6, 25.07.20
 */
data class Recipe(
   @JsonIgnore
   var recipeId: Long,
   // name of the recipe (obligatory)
   var name: String,
   // description of the recipe
   var description: String = "",
   // url to the recipe on a website
   var url: String = "",
   @JsonProperty("@context")
   var context: String = "http://schema.org",
   @JsonProperty("@type")
   var type: String = "Recipe",
   var author: Author? = Author(""),
   var cookTime: String? = "",
   @JsonDeserialize(using = RecipeDateDeserializer::class)
   @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
   var datePublished: Date? = null,
   @JsonProperty("image")
   var imageUrl: String = "",
   @JsonDeserialize(using = RecipeStringArrayDeserializer::class)
   var recipeIngredient: Array<String> = emptyArray(),
   var interactionStatistic: InteractionStatistic = InteractionStatistic(),
   var nutrition: Nutrition? = Nutrition(),
   var prepTime: String? = "",
   var totalTime: String? = "",
   @JsonDeserialize(using = RecipeStringArrayDeserializer::class)
   var recipeInstructions: Array<String> = emptyArray(),
   var recipeYield: String = "",
   var suitableForDiet: String = "",
   var cookingMethod: Array<String>? = emptyArray(),
   var recipeCategory: Array<String>? = emptyArray(),
   var keywords: String = "",
   var recipeCuisine: Array<String> = emptyArray(),
   var tool: Array<String> = emptyArray(),
   var yield: String = "",
   var estimatedCost: String = "",
   @JsonIgnore var thumbImage: Uri? = null
) {
   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Recipe

      if (name != other.name) return false
      if (description != other.description) return false
      if (url != other.url) return false
      if (context != other.context) return false
      if (type != other.type) return false
      if (author != other.author) return false
      if (cookTime != other.cookTime) return false
      if (datePublished != other.datePublished) return false
      if (imageUrl != other.imageUrl) return false

      return true
   }

   override fun hashCode(): Int {
      var result = name.hashCode()
      result = 31 * result + description.hashCode()
      result = 31 * result + url.hashCode()
      result = 31 * result + context.hashCode()
      result = 31 * result + type.hashCode()
      result = 31 * result + author.hashCode()
      result = 31 * result + cookTime.hashCode()
      result = 31 * result + datePublished.hashCode()
      result = 31 * result + imageUrl.hashCode()
      return result
   }
}

data class InteractionStatistic(
   @JsonProperty("@type")
   var type: String = "InteractionCounter",
   var interactionType: String = "http://schema.org/Comment",
   var userInteractionCount: Int = 0
)

data class Nutrition(
   @JsonProperty("@type")
   var type: String = "NutritionInformation",
   var calories: String = "",
   var carbohydrateContent: String = "",
   var cholesterolContent: String = "",
   var fatContent: String = "",
   var fiberContent: String = "",
   var proteinContent: String = "",
   var saturatedFatContent: String = "",
   var servingSize: String = "",
   var sodiumContent: String = "",
   var sugarContent: String = "",
   var transFatContent: String = "",
   var unsaturatedFatContent: String = ""
) {
   /**
    * Returns a map with the nutritions which are set.
    *
    * @return Map with nutritions which are set.
    */
   fun toMap(): Map<Int, String> {
      val map = mutableMapOf<Int, String>()

      if (calories.isNotEmpty())
         map[R.string.nutrition_calories] = calories
      if (carbohydrateContent.isNotEmpty())
         map[R.string.nutrition_carbohydrate_content] = carbohydrateContent
      if (cholesterolContent.isNotEmpty())
         map[R.string.nutrition_cholesterol_content] = cholesterolContent
      if (fatContent.isNotEmpty())
         map[R.string.nutrition_fat_content] = fatContent
      if (fiberContent.isNotEmpty())
         map[R.string.nutrition_fiberContent] = fiberContent
      if (proteinContent.isNotEmpty())
         map[R.string.nutrition_protein_content] = proteinContent
      if (saturatedFatContent.isNotEmpty())
         map[R.string.nutrition_saturated_fat_content] = saturatedFatContent
      if (servingSize.isNotEmpty())
         map[R.string.nutrition_serving_size] = servingSize
      if (sodiumContent.isNotEmpty())
         map[R.string.nutrition_sodium_content] = sodiumContent
      if (sugarContent.isNotEmpty())
         map[R.string.nutrition_sugar_content] = sugarContent
      if (transFatContent.isNotEmpty())
         map[R.string.nutrition_trans_fat_content] = transFatContent
      if (unsaturatedFatContent.isNotEmpty())
         map[R.string.nutrition_unsaturated_fat_content] = unsaturatedFatContent

      return map
   }
}

data class Author @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(var name: String = "")
