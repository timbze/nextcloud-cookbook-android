/*
 * RecipeListAdapter.kt
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
 * @version 1.4, 26.07.20
 */
class RecipeListAdapter(private val clickListener: RecipeListListener) :
   ListAdapter<Recipe, RecipeListAdapter.RecipeViewHolder>(RECIPE_ITEM_CALLBACK) {

   companion object {
      private val RECIPE_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Recipe>() {
         override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean = oldItem.recipeId == newItem.recipeId
         override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean = oldItem.name == newItem.name
      }
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
      return RecipeViewHolder.from(parent)
   }

   override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
      val recipe = currentList[position]
      holder.bind(clickListener, recipe)
   }

   override fun getItemCount(): Int {
      return currentList.size
   }

   class RecipeViewHolder private constructor(private val binding: RecipeListRowBinding) :
      RecyclerView.ViewHolder(binding.root) {
      /**
       * Binds the data to the views.
       *
       * @param recipe Recipe data.
       */
      fun bind(clickListener: RecipeListListener, recipe: Recipe) {
         binding.recipe = recipe
         binding.clickListener = clickListener
         binding.executePendingBindings()
      }

      companion object {
         fun from(parent: ViewGroup): RecipeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RecipeListRowBinding.inflate(layoutInflater, parent, false)
            return RecipeViewHolder(binding)
         }
      }
   }
}

class RecipeListListener(val clickListener: (recipeId: Long) -> Unit) {
   fun onClick(recipe: Recipe) = clickListener(recipe.recipeId)
}
