/*
 * BindingUtils.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.model.Recipe

/**
 * Utilities for binding data to view.
 *
 * @author MicMun
 * @version 1.2, 20.06.20
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

@BindingAdapter("recipeCategories")
fun TextView.setRecipeCategories(item: Recipe?) {
   item?.let {
      val categories = if (it.recipeCategory!!.isEmpty())
         resources.getString(R.string.text_uncategorized)
      else {
         it.recipeCategory!!.joinToString(", ")
      }
      text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
         Html.fromHtml(resources.getString(R.string.text_categories, categories), Html.FROM_HTML_MODE_LEGACY)
      else
         Html.fromHtml(resources.getString(R.string.text_categories, categories))
   }
}

@BindingAdapter("recipeNutritions")
fun TextView.setRecipeNutritions(item: Recipe?) {
   item?.let { r ->
      var input = ""

      r.nutrition?.let { nutrition ->
         val nmap = nutrition.toMap()
         input = ""

         nmap.entries.forEach { entry ->
            input += " - ${resources.getString(entry.key, entry.value)}<br/>"
         }
      }

      text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
         Html.fromHtml(resources.getString(R.string.text_nutritions, input), Html.FROM_HTML_MODE_LEGACY)
      else
         Html.fromHtml(resources.getString(R.string.text_nutritions, input))
   }
}

@BindingAdapter("recipeAuthor")
fun TextView.setAuthor(item: Recipe?) {
   item?.let {
      if (it.author == null || it.author!!.name.isEmpty()) {
         visibility = View.GONE
      } else {
         text = resources.getString(R.string.text_author, it.author!!.name)
      }
   }
}