/*
 * ViewPagerAdapter.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.databinding.TabInfosBinding
import de.micmun.android.nextcloudcookbook.databinding.TabIngredientsBinding
import de.micmun.android.nextcloudcookbook.databinding.TabInstructionsBinding

/**
 * Adapter for the ViewPager2 to present tabs.
 *
 * @author MicMun
 * @version 1.0, 11.06.20
 */
class ViewPagerAdapter(private val recipe: Recipe) :
   RecyclerView.Adapter<RecyclerView.ViewHolder>() {
   companion object {
      const val TYPE_INFO = 0
      const val TYPE_INGREDIENTS = 1
      const val TYPE_INSTRUCTIONS = 2
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
      return when (viewType) {
         TYPE_INFO -> InfoViewHolder.from(parent)
         TYPE_INGREDIENTS -> IngredientsViewHolder.from(parent)
         TYPE_INSTRUCTIONS -> InstructionsViewHolder.from(parent)
         else -> InfoViewHolder.from(parent)
      }
   }

   override fun getItemCount(): Int {
      return 3
   }

   override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      when (getItemViewType(position)) {
         TYPE_INFO -> {
            val viewHolder = holder as InfoViewHolder
            viewHolder.bind(recipe)
         }
         TYPE_INGREDIENTS -> {
            val viewHolder = holder as IngredientsViewHolder
            viewHolder.bind(recipe)
         }
         TYPE_INSTRUCTIONS -> {
            val viewHolder = holder as InstructionsViewHolder
            viewHolder.bind(recipe)
         }
      }
   }

   override fun getItemViewType(position: Int): Int {
      return position
   }

   class InfoViewHolder private constructor(private val binding: TabInfosBinding) :
      RecyclerView.ViewHolder(binding.root) {
      /**
       * Binds the data to the views.
       *
       * @param recipeViewModel ViewModel for recipe data.
       */
      fun bind(recipe: Recipe) {
         binding.recipe = recipe
         binding.executePendingBindings()
      }

      companion object {
         fun from(parent: ViewGroup): InfoViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TabInfosBinding.inflate(layoutInflater, parent, false)
            return InfoViewHolder(binding)
         }
      }
   }

   class IngredientsViewHolder private constructor(private val binding: TabIngredientsBinding) :
      RecyclerView.ViewHolder(binding.root) {
      /**
       * Binds the data to the views.
       *
       * @param recipe Recipe data.
       */
      fun bind(recipe: Recipe) {
         binding.incredientsView.adapter = RecipeIngredientsAdapter(recipe.recipeIngredient)
         binding.executePendingBindings()
      }

      companion object {
         fun from(parent: ViewGroup): IngredientsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TabIngredientsBinding.inflate(layoutInflater, parent, false)
            return IngredientsViewHolder(binding)
         }
      }
   }

   class InstructionsViewHolder private constructor(private val binding: TabInstructionsBinding) :
      RecyclerView.ViewHolder(binding.root) {
      /**
       * Binds the data to the views.
       *
       * @param recipe Recipe data.
       */
      fun bind(recipe: Recipe) {
         binding.instructionsView.adapter = RecipeInstructionsAdapter(recipe.recipeInstructions)
         binding.executePendingBindings()
      }

      companion object {
         fun from(parent: ViewGroup): InstructionsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TabInstructionsBinding.inflate(layoutInflater, parent, false)
            return InstructionsViewHolder(binding)
         }
      }
   }
}
