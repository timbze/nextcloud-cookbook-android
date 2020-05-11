/*
 * BindingUtils.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import de.micmun.android.nextcloudcookbook.data.model.Recipe

/**
 * Utilities for binding data to view.
 *
 * @author MicMun
 * @version 1.0, 31.03.20
 */

// Overview list
@BindingAdapter("recipeImage")
fun ImageView.setRecipeImage(item: Recipe?) {
   item?.let {
      if (it.thumbImage != null) {
         setImageURI(it.thumbImage)
      }
   }
}

@BindingAdapter("recipeName")
fun TextView.setRecipeName(item: Recipe?) {
   item?.let { text = it.name }
}

@BindingAdapter("recipeDescription")
fun TextView.setRecipeDesc(item: Recipe?) {
   item?.let { text = it.description }
}

@BindingAdapter("recipeIncredients")
fun TextView.setRecipeIncredients(item: Recipe?) {
   item?.let {
      text = it.recipeIngredient.joinToString("\n- ", "- ")
   }
}

@BindingAdapter("recipeHeaderImage")
fun ImageView.setRecipeHeaderImage(item: Recipe?) {
   item?.let {
      if (it.imageUrl.isNotEmpty()) {
         setImageURI(Uri.parse(it.imageUrl))
      }
   }
}
