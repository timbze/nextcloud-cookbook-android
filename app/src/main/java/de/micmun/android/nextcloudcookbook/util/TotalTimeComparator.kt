/*
 * TotalTimeComparator.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import de.micmun.android.nextcloudcookbook.data.model.Recipe

/**
 * Comparator for total time of recipe.
 *
 * @author MicMun
 * @version 1.0, 27.07.20
 */
class TotalTimeComparator(private val asc: Boolean) : Comparator<Recipe> {
   override fun compare(o1: Recipe, o2: Recipe): Int {
      val dur1 = DurationUtils.durationInMinutes(o1.totalTime)
      val dur2 = DurationUtils.durationInMinutes(o2.totalTime)
      return if (asc) dur1 - dur2 else dur2 - dur1
   }
}
