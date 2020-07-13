/*
 * RecipeFilter.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import de.micmun.android.nextcloudcookbook.data.model.Recipe

/**
 * Filter class with the definition of a filter and the method to return the filtered list.
 *
 * @author MicMun
 * @version 1.0, 12.07.20
 */
class RecipeFilter(private val type: QueryType, val query: String, private val ignoreCase: Boolean = true,
                   private val exact: Boolean = false) :
   Parcelable {
   constructor(parcel: Parcel) : this(
      QueryType.valueOf(parcel.readString()!!),
      parcel.readString()!!,
      parcel.readByte() != 0.toByte())

   /**
    * Returns a filtered list with current type.
    *
    * @param list List of Recipe.
    * @return filtered list of Recipe.
    */
   fun filter(list: List<Recipe>): List<Recipe> {
      return when (type) {
         QueryType.QUERY_NAME -> list.filter {
            if (exact)
               it.name.equals(query, ignoreCase)
            else
               it.name.contains(query, ignoreCase)
         }
         QueryType.QUERY_KEYWORD -> list.filter {
            if (exact)
               it.keywords.equals(query, ignoreCase)
            else
               it.keywords.contains(query, ignoreCase)
         }
         QueryType.QUERY_INGREDIENTS -> list.filter {
            var result = ""
            for (i: String in it.recipeIngredient) {
               if (exact) {
                  if (i.equals(query, ignoreCase)) {
                     result = i
                     break
                  }
               } else {
                  if (i.contains(query, ignoreCase)) {
                     result = i
                     break
                  }
               }
            }
            result.isNotEmpty()
         }
      }
   }

   /**
    * Types of query.
    */
   enum class QueryType {
      QUERY_NAME,
      QUERY_KEYWORD,
      QUERY_INGREDIENTS
   }

   override fun writeToParcel(dest: Parcel?, flags: Int) {
      dest?.writeString(type.name)
      dest?.writeString(query)
      dest?.writeByte(if (ignoreCase) 1 else 0)
   }

   override fun describeContents(): Int {
      return 0
   }

   companion object CREATOR : Parcelable.Creator<RecipeFilter> {
      override fun createFromParcel(parcel: Parcel): RecipeFilter {
         return RecipeFilter(parcel)
      }

      override fun newArray(size: Int): Array<RecipeFilter?> {
         return arrayOfNulls(size)
      }
   }
}