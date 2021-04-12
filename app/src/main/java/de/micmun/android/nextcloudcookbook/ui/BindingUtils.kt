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
import androidx.documentfile.provider.DocumentFile
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.db.model.DbRecipe
import de.micmun.android.nextcloudcookbook.util.DurationUtils
import java.util.stream.Collectors

/**
 * Utilities for binding data to view.
 *
 * @author MicMun
 * @version 1.9, 09.04.21
 */

// Overview list
@BindingAdapter("recipeImage")
fun ImageView.setRecipeImage(item: DbRecipe?) {
   item?.let {
      if (it.recipeCore.thumbImageUrl.isNotEmpty()) {
         val thumbImage = DocumentFile.fromSingleUri(context, Uri.parse(it.recipeCore.thumbImageUrl))
         setImageURI(thumbImage?.uri)
      }
   }
}

@BindingAdapter("recipeName")
fun TextView.setRecipeName(item: DbRecipe?) {
   item?.let { text = it.recipeCore.name }
}

@BindingAdapter("recipeDescription")
fun TextView.setRecipeDesc(item: DbRecipe?) {
   item?.let { text = it.recipeCore.description }
}

// Detail view
@BindingAdapter("recipeHeaderImage")
fun TextView.setRecipeHeaderImage(item: DbRecipe?) {
   item?.let {
      if (it.recipeCore.fullImageUrl.isNotEmpty()) {
         val image = Drawable.createFromPath(it.recipeCore.fullImageUrl)
         setCompoundDrawablesRelativeWithIntrinsicBounds(null, image, null, null)
      }
   }
}

@BindingAdapter("recipePublishedDate")
fun TextView.setPublishedDate(item: DbRecipe?) {
   item?.let {
      val date = if (it.recipeCore.datePublished.isEmpty()) "-" else it.recipeCore.datePublished
      text = resources.getString(R.string.text_date_published, date)
   }
}

@BindingAdapter("recipePrepTime")
fun TextView.setPrepTime(item: DbRecipe?) {
   item?.let {
      text = if (it.recipeCore.prepTime.isEmpty()) "" else DurationUtils.formatStringToDuration(
         it.recipeCore.prepTime)
   }
}

@BindingAdapter("recipeCookTime")
fun TextView.setCookTime(item: DbRecipe?) {
   item?.let {
      text = if (it.recipeCore.cookTime.isEmpty()) "" else DurationUtils.formatStringToDuration(
         it.recipeCore.cookTime)
   }
}

@BindingAdapter("recipeTotalTime")
fun TextView.setTotalTime(item: DbRecipe?) {
   item?.let {
      text = if (it.recipeCore.totalTime.isEmpty()) "" else DurationUtils.formatStringToDuration(
         it.recipeCore.totalTime)
   }
}

@BindingAdapter("recipeCategories")
fun TextView.setRecipeCategories(item: DbRecipe?) {
   item?.let {
      val categories = if (it.recipeCore.recipeCategory.isEmpty())
         resources.getString(R.string.text_uncategorized)
      else {
         it.recipeCore.recipeCategory
      }
      @Suppress("DEPRECATION")
      text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
         Html.fromHtml(resources.getString(R.string.text_categories, categories), Html.FROM_HTML_MODE_LEGACY)
      else
         Html.fromHtml(resources.getString(R.string.text_categories, categories))
   }
}

@BindingAdapter("keywords")
fun TextView.setKeywords(item: DbRecipe?) {
   item?.let { recipe ->
      val keywords = if (recipe.keywords?.isEmpty() != false)
         resources.getString(R.string.text_no_keywords)
      else
         recipe.keywords.joinToString(transform = {kw -> kw.keyword})
      @Suppress("DEPRECATION")
      text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
         Html.fromHtml(resources.getString(R.string.text_keywords, keywords), Html.FROM_HTML_MODE_LEGACY)
      else
         Html.fromHtml(resources.getString(R.string.text_keywords, keywords))
   }
}

@BindingAdapter("recipeAuthor")
fun TextView.setAuthor(item: DbRecipe?) {
   item?.let {
      if (it.recipeCore.author == null) {
         visibility = View.GONE
      } else {
         text = resources.getString(R.string.text_author, it.recipeCore.author.name)
         visibility = View.VISIBLE
      }
   }
}

@BindingAdapter("url")
fun TextView.setUrl(item: DbRecipe?) {
   item?.let {
      if (it.recipeCore.url.isEmpty()) {
         visibility = View.GONE
      } else {
         text = resources.getString(R.string.text_url, it.recipeCore.url)
         visibility = View.VISIBLE
      }
   }
}

@BindingAdapter("recipeYield")
fun TextView.setRecipeYield(item: DbRecipe?) {
   item?.let {
      if (it.recipeCore.recipeYield.isEmpty()) {
         visibility = View.GONE
      } else {
         @Suppress("DEPRECATION")
         text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(resources.getString(R.string.text_yield, it.recipeCore.recipeYield),
                          Html.FROM_HTML_MODE_LEGACY)
         else
            Html.fromHtml(resources.getString(R.string.text_yield, it.recipeCore.recipeYield))

         visibility = View.VISIBLE
      }
   }
}

@BindingAdapter("recipeTools")
fun TextView.setTools(item: DbRecipe?) {
   item?.let {
      if (it.tool.isNullOrEmpty()) {
         visibility = View.GONE
      } else {
         val tools = it.tool.stream().map { it.tool }.collect(Collectors.toList()).joinToString(", ")
 
         @Suppress("DEPRECATION")
         text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(resources.getString(R.string.text_tools, tools), Html.FROM_HTML_MODE_LEGACY)
         else
            Html.fromHtml(resources.getString(R.string.text_tools, tools))
      }
   }
}
