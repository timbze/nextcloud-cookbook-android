/*
 * NameComparator.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import de.micmun.android.nextcloudcookbook.data.model.Recipe

/**
 * Comparator with name.
 *
 * @author MicMun
 * @version 1.0, 26.07.20
 */
class NameComparator(private val asc: Boolean) : Comparator<Recipe> {
   override fun compare(o1: Recipe, o2: Recipe): Int {
      return if (asc) o1.name.compareTo(o2.name) else o2.name.compareTo(o1.name)
   }
}
