/*
 * TotalTimeComparator.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import de.micmun.android.nextcloudcookbook.db.model.DbRecipe

/**
 * Comparator for total time of recipe.
 *
 * @author MicMun
 * @version 1.1, 21.03.21
 */
class TotalTimeComparator(private val asc: Boolean) : Comparator<DbRecipe> {
   override fun compare(o1: DbRecipe, o2: DbRecipe): Int {
      val dur1 = DurationUtils.durationInMinutes(o1.recipeCore.totalTime)
      val dur2 = DurationUtils.durationInMinutes(o2.recipeCore.totalTime)
      return if (asc) dur1 - dur2 else dur2 - dur1
   }
}
