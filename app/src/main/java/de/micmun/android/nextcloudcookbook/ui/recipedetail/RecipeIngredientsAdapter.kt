/*
 * RecipeIngredientsAdapter.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.micmun.android.nextcloudcookbook.databinding.IngredientsItemBinding

/**
 * Adapter for recipe ingredients.
 *
 * @author MicMun
 * @version 1.0, 02.06.20
 */
class RecipeIngredientsAdapter(private val ingredients: Array<String>) :
   RecyclerView.Adapter<RecipeIngredientsAdapter.IngredientsViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
      return IngredientsViewHolder.from(parent)
   }

   override fun getItemCount() = ingredients.size

   override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
      val ingredient = ingredients[position].trim()
      holder.bind(ingredient)
   }

   class IngredientsViewHolder private constructor(val binding: IngredientsItemBinding) :
      RecyclerView.ViewHolder(binding.root) {
      /**
       * Binds the data to the views.
       *
       * @param ingredient Ingredient String to show in view.
       */
      fun bind(ingredient: String) {
         binding.ingredient = ingredient
         binding.executePendingBindings()
      }

      companion object {
         fun from(parent: ViewGroup): IngredientsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = IngredientsItemBinding.inflate(layoutInflater, parent, false)
            return IngredientsViewHolder(binding)
         }
      }
   }
}