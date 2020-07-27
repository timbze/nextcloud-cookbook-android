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
import de.micmun.android.nextcloudcookbook.util.DurationUtils

/**
 * Utilities for binding data to view.
 *
 * @author MicMun
 * @version 1.7, 27.07.20
 */

// Overview list
@BindingAdapter("recipeImage")
fun ImageView.setRecipeImage(item: Recipe?) {
   item?.let {
      if (it.thumbImage != null) {
         setImageURI(it.thumbImage)
      } else {
         setImageURI(null)
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

@BindingAdapter("recipePublishedDate")
fun TextView.setPublishedDate(item: Recipe?) {
   item?.let {
      text = if (it.datePublished == null)
         resources.getString(R.string.text_date_published, "-")
      else
         resources.getString(R.string.text_date_published, DurationUtils.formatDate(context, it.datePublished!!))
   }
}

@BindingAdapter("recipePrepTime")
fun TextView.setPrepTime(item: Recipe?) {
   item?.let { text = if (it.prepTime.isNullOrEmpty()) "" else DurationUtils.formatStringToDuration(it.prepTime!!) }
}

@BindingAdapter("recipeCookTime")
fun TextView.setCookTime(item: Recipe?) {
   item?.let { text = if (it.cookTime.isNullOrEmpty()) "" else DurationUtils.formatStringToDuration(it.cookTime!!) }
}

@BindingAdapter("recipeTotalTime")
fun TextView.setTotalTime(item: Recipe?) {
   item?.let { text = if (it.totalTime.isNullOrEmpty()) "" else DurationUtils.formatStringToDuration(it.totalTime!!) }
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

@BindingAdapter("keywords")
fun TextView.setKeywords(item: Recipe?) {
   item?.let { recipe ->
      val keywords = if (recipe.keywords.isEmpty())
         resources.getString(R.string.text_no_keywords)
      else
         recipe.keywords
      text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
         Html.fromHtml(resources.getString(R.string.text_keywords, keywords), Html.FROM_HTML_MODE_LEGACY)
      else
         Html.fromHtml(resources.getString(R.string.text_keywords, keywords))
   }
}

@BindingAdapter("recipeAuthor")
fun TextView.setAuthor(item: Recipe?) {
   item?.let {
      if (it.author == null || it.author!!.name.isEmpty()) {
         visibility = View.GONE
      } else {
         text = resources.getString(R.string.text_author, it.author!!.name)
         visibility = View.VISIBLE
      }
   }
}

@BindingAdapter("url")
fun TextView.setUrl(item: Recipe?) {
   item?.let {
      if (it.url.isEmpty()) {
         visibility = View.GONE
      } else {
         text = resources.getString(R.string.text_url, it.url)
         visibility = View.VISIBLE
      }
   }
}

@BindingAdapter("recipeYield")
fun TextView.setRecipeYield(item: Recipe?) {
   item?.let {
      if (it.recipeYield.isEmpty()) {
         visibility = View.GONE
      } else {
         text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(resources.getString(R.string.text_yield, it.recipeYield), Html.FROM_HTML_MODE_LEGACY)
         else
            Html.fromHtml(resources.getString(R.string.text_yield, it.recipeYield))

         visibility = View.VISIBLE
      }
   }
}
