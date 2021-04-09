/*
 * NameComparator.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import de.micmun.android.nextcloudcookbook.db.model.DbRecipe

/**
 * Comparator with name.
 *
 * @author MicMun
 * @version 1.1, 21.03.21
 */
class NameComparator(private val asc: Boolean) : Comparator<DbRecipe> {
   override fun compare(o1: DbRecipe, o2: DbRecipe): Int {
      return if (asc) o1.recipeCore.name.compareTo(o2.recipeCore.name) else o2.recipeCore.name.compareTo(
         o1.recipeCore.name)
   }
}
