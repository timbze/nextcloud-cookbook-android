/*
 * BindingUtils.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import de.micmun.android.nextcloudcookbook.data.model.Recipe

/**
 * Utilities for binding data to view.
 *
 * @author MicMun
 * @version 1.1, 02.06.20
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

@BindingAdapter("recipeHeaderImage")
fun TextView.setRecipeHeaderImage(item: Recipe?) {
   item?.let {
      if (it.imageUrl.isNotEmpty()) {
         val image = Drawable.createFromPath(Uri.parse(it.imageUrl).path)
         setCompoundDrawablesRelativeWithIntrinsicBounds(null, image, null, null)
      }
   }
}

@BindingAdapter("recipePrepTime")
fun TextView.setPrepTime(item: Recipe?) {
   item?.let { text = it.prepTime }
}

@BindingAdapter("recipeCookTime")
fun TextView.setCookTime(item: Recipe?) {
   item?.let { text = it.cookTime }
}

@BindingAdapter("recipeTotalTime")
fun TextView.setTotalTime(item: Recipe?) {
   item?.let { text = it.totalTime }
}
