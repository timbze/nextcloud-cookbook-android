/*
 * Recipe.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data.model

import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Data class for a recipe.
 *
 * @author MicMun
 * @version 1.0, 24.02.20
 */
data class Recipe(
      @Transient
      var recipeId: Long,
      // name of the recipe (obligatory)
      var name: String,
      // description of the recipe
      var description: String = "",
      // url to the recipe on a website
      var url: String = "",
      @SerializedName("@context")
      var context: String = "http://schema.org",
      @SerializedName("@type")
      var type: String = "Recipe",
      var author: Author = Author(
            ""),
      var cookTime: String = "",
      var datePublished: Date? = null,
      @SerializedName("image")
      var imageUrl: String = "",
      var recipeIngredient: Array<String> = emptyArray(),
      var interactionStatistic: InteractionStatistic = InteractionStatistic(),
      var nutrition: Nutrition = Nutrition(),
      var prepTime: String = "",
      var performTime: String = "",
      var recipeInstructions: Array<String> = emptyArray(),
      var recipeYield: String = "",
      var suitableForDiet: String = "",
      var cookingMethod: String = "",
      var recipeCategory: Array<String> = emptyArray(),
      var recipeCuisine: String = "",
      var tool: Array<String> = emptyArray(),
      var yield: String = "",
      var estimatedCost: String = "",
      @Transient var thumbImage: Uri? = null
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
      @SerializedName("@type")
      var type: String = "InteractionCounter",
      var interactionType: String = "http://schema.org/Comment",
      var userInteractionCount: Int = 0
)

data class Nutrition(
      @SerializedName("@type")
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
)

data class Author(var name: String = "")
