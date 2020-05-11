/*
 * RecipeAdapter.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.databinding.RecipeListRowBinding

/**
 * RecyclerViewAdapter for the list of recipes.
 *
 * @author MicMun
 * @version 1.0, 22.03.20
 */
class RecipeAdapter(private val clickListener: RecipeListener) :
   ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
      return RecipeViewHolder.from(parent)
   }

   override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
      val recipe = getItem(position)
      holder.bind(clickListener, recipe)
   }

   class RecipeViewHolder private constructor(private val binding: RecipeListRowBinding) :
      RecyclerView.ViewHolder(binding.root) {
      /**
       * Binds the data to the views.
       *
       * @param recipe DbRecipeOverview data.
       */
      fun bind(clickListener: RecipeListener, recipe: Recipe) {
         binding.recipe = recipe
         binding.clickListener = clickListener
         binding.executePendingBindings()
      }

      companion object {
         fun from(parent: ViewGroup): RecipeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RecipeListRowBinding.inflate(layoutInflater, parent, false)
            return RecipeViewHolder(
                  binding)
         }
      }
   }
}

class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
   override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
      return oldItem.recipeId == newItem.recipeId
   }

   override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
      return oldItem == newItem
   }

}

class RecipeListener(val clickListener: (recipeId: Long) -> Unit) {
   fun onClick(recipe: Recipe) = clickListener(recipe.recipeId)
}
