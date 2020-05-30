/*
 * RecipeListAdapter.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.databinding.RecipeListRowBinding

/**
 * RecyclerViewAdapter for the list of recipes.
 *
 * @author MicMun
 * @version 1.2, 30.05.20
 */
class RecipeListAdapter(private val clickListener: RecipeListListener) :
   RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>() {

   private val recipeSortedList: SortedList<Recipe> = SortedList(Recipe::class.java, RecipeDiffCallback(this))

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
      return RecipeViewHolder.from(parent)
   }

   override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
      val recipe = recipeSortedList[position]
      holder.bind(clickListener, recipe)
   }

   override fun getItemCount(): Int {
      return recipeSortedList.size()
   }

   fun setRecipes(recipes: List<Recipe>) {
      recipeSortedList.replaceAll(recipes)
   }

   class RecipeViewHolder private constructor(private val binding: RecipeListRowBinding) :
      RecyclerView.ViewHolder(binding.root) {
      /**
       * Binds the data to the views.
       *
       * @param recipe DbRecipeOverview data.
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

class RecipeDiffCallback(adapter: RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>) :
   SortedListAdapterCallback<Recipe>(adapter) {
   override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
      return oldItem.recipeId == newItem.recipeId
   }

   override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
      return oldItem == newItem
   }

   override fun compare(o1: Recipe?, o2: Recipe?) = o1!!.name.compareTo(o2!!.name)
}

class RecipeListListener(val clickListener: (recipeId: Long) -> Unit) {
   fun onClick(recipe: Recipe) = clickListener(recipe.recipeId)
}
