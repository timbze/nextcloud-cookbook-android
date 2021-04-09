/*
 * DateComparator.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import de.micmun.android.nextcloudcookbook.db.model.DbRecipe

/**
 * Comparator with date published.
 *
 * @author MicMun
 * @version 1.1, 21.03.21
 */
class DateComparator(private val asc: Boolean) : Comparator<DbRecipe> {
   override fun compare(o1: DbRecipe, o2: DbRecipe): Int {
      val date1 = if (asc) o1.recipeCore.datePublished else o2.recipeCore.datePublished
      val date2 = if (asc) o2.recipeCore.datePublished else o1.recipeCore.datePublished

      return if (date1.isEmpty() && date2.isEmpty())
         0
      else if (date1.isEmpty() && date2.isNotEmpty()) {
         -1
      } else if (date1.isNotEmpty() && date2.isEmpty()) {
         1
      } else {
         date1.compareTo(date2)
      }
   }
}
