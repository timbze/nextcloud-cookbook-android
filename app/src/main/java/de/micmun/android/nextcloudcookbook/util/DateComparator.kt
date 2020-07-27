/*
 * DateComparator.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import de.micmun.android.nextcloudcookbook.data.model.Recipe

/**
 * Comparator with date published.
 *
 * @author MicMun
 * @version 1.0, 26.07.20
 */
class DateComparator(private val asc: Boolean) : Comparator<Recipe> {
   override fun compare(o1: Recipe, o2: Recipe): Int {
      val date1 = if (asc) o1.datePublished else o2.datePublished
      val date2 = if (asc) o2.datePublished else o1.datePublished

      return if (date1 == null && date2 == null)
         0
      else if (date1 == null && date2 != null) {
         -1
      } else if (date1 != null && date2 == null) {
         1
      } else {
         date1!!.compareTo(date2)
      }
   }
}
