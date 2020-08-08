/*
 * CategoryFilter.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data

/**
 * Filter for category.
 *
 * @author MicMun
 * @version 1.0, 08.08.20
 */
class CategoryFilter(val type: CategoryFilterOption, val categoryId: Int) {

   enum class CategoryFilterOption {
      ALL_CATEGORIES, UNCATEGORIZED, CATEGORY
   }
}
