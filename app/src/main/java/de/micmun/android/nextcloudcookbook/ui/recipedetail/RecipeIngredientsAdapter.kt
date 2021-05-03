/*
 * RecipeIngredientsAdapter.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.micmun.android.nextcloudcookbook.databinding.IngredientsItemBinding
import de.micmun.android.nextcloudcookbook.db.model.DbIngredient

/**
 * Adapter for recipe ingredients.
 *
 * @author MicMun
 * @version 1.1, 28.02.21
 */
class RecipeIngredientsAdapter(private val ingredients: List<DbIngredient>) :
   RecyclerView.Adapter<RecipeIngredientsAdapter.IngredientsViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
      return IngredientsViewHolder.from(parent)
   }

   override fun getItemCount() = ingredients.size

   override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
      val ingredient = ingredients[position].ingredient.trim()
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
         binding.ingredientsItemText.setOnClickListener {
            binding.ingredientsItemText.paintFlags =
                    binding.ingredientsItemText.paintFlags.xor(Paint.STRIKE_THRU_TEXT_FLAG)
         }
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