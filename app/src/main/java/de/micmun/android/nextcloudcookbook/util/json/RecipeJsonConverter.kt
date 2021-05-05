/*
 * RecipeJsonConverter.kt
 *
 * Copyright 2021 by Leafar
 */
package de.micmun.android.nextcloudcookbook.util.json

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import de.micmun.android.nextcloudcookbook.json.*
import de.micmun.android.nextcloudcookbook.json.model.Recipe
import java.io.StringReader
import java.lang.ClassCastException

/**
 * Convert between Recipe objects and their json representation.
 *
 * @author MicMun
 * @version 1.0, 14.02.21
 */
class RecipeJsonConverter {
    companion object {
        fun write(recipe: Recipe): String {
            return getKlaxon().toJsonString(recipe)
        }

        fun parse(json: String): Recipe? {
            return getKlaxon().parse<Recipe>(json)
        }

        fun parseFromWeb(json: String): Recipe? {
            // websites may provide multiple ld-json in one script tag as an array
            try {
                val jsArray = Klaxon().parseJsonArray(StringReader(json))
                for (obj in jsArray) {
                    // could also check js["@context"] == "http://schema.org"
                    if (obj is JsonObject && obj["@type"] == "Recipe") {
                        return getKlaxon().parseFromJsonObject<Recipe>(obj)
                    }
                }
                return null
            } catch (e: ClassCastException) {
            }

            val jsonObject = Klaxon().parseJsonObject(StringReader(json))
            if (jsonObject["@type"] == "Recipe") {
                return getKlaxon().parseFromJsonObject<Recipe>(jsonObject)
            }
            return null
        }

        private fun getKlaxon(): Klaxon {
            return Klaxon()
                    .fieldConverter(RecipeDate::class, DateConverter())
                    .fieldConverter(RecipeStringList::class, ListConverter())
                    .fieldConverter(RecipeListString::class, List2StringConverter())
                    .fieldConverter(RecipeNutrition::class, NutritionConverter())
                    .fieldConverter(Recipe2String::class, Value2StringConverter())
                    .fieldConverter(RecipeAuthor::class, AuthorConverter())
                    .fieldConverter(RecipeImage::class, ImageURLConverter())
        }
    }
}