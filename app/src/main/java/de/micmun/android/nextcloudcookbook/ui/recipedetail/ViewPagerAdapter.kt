/*
 * ViewPagerAdapter.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.model.Nutrition
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.databinding.TabInfosBinding
import de.micmun.android.nextcloudcookbook.databinding.TabIngredientsBinding
import de.micmun.android.nextcloudcookbook.databinding.TabInstructionsBinding
import de.micmun.android.nextcloudcookbook.databinding.TabNutritionsBinding

/**
 * Adapter for the ViewPager2 to present tabs.
 *
 * @author MicMun
 * @version 1.4, 12.07.20
 */
class ViewPagerAdapter(private val recipe: Recipe) :
   RecyclerView.Adapter<RecyclerView.ViewHolder>() {
   companion object {
      const val TYPE_INFO = 0
      const val TYPE_NUTRITIONS = 1
      const val TYPE_INGREDIENTS = 2
      const val TYPE_INSTRUCTIONS = 3
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
      return when (viewType) {
         TYPE_INFO -> InfoViewHolder.from(parent)
         TYPE_INGREDIENTS -> IngredientsViewHolder.from(parent)
         TYPE_INSTRUCTIONS -> InstructionsViewHolder.from(parent)
         TYPE_NUTRITIONS -> NutritionsViewHolder.from(parent)
         else -> InfoViewHolder.from(parent)
      }
   }

   override fun getItemCount(): Int {
      return 4
   }

   override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      when (getItemViewType(position)) {
         TYPE_INFO -> {
            val viewHolder = holder as InfoViewHolder
            viewHolder.bind(recipe)
         }
         TYPE_NUTRITIONS -> {
            val viewHolder = holder as NutritionsViewHolder
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
       * @param recipe Recipe with its data.
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

   class NutritionsViewHolder private constructor(private val binding: TabNutritionsBinding,
                                                  private val resources: Resources) :
      RecyclerView.ViewHolder(binding.root) {
      /**
       * Binds the data to the views.
       *
       * @param recipe Recipe data.
       */
      fun bind(recipe: Recipe) {
         val switcher = binding.nutritionSwitcher

         val nutritions = getNutritionList(recipe.nutrition)

         if (nutritions.isEmpty() && R.id.nutritionEmptyLayout == switcher.nextView.id) {
            switcher.showNext()
         } else {
            binding.nutritionList.adapter = RecipeNutritionAdapter(nutritions)
            binding.executePendingBindings()

            if (R.id.nutritionListLayout == switcher.nextView.id) {
               switcher.showNext()
            }
         }

         binding.executePendingBindings()
      }

      companion object {
         fun from(parent: ViewGroup): NutritionsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TabNutritionsBinding.inflate(layoutInflater, parent, false)
            return NutritionsViewHolder(binding, parent.resources)
         }
      }

      /**
       * Returns the list of nutritions or an empty list.
       *
       * @return list of nutritions or an empty list.
       */
      private fun getNutritionList(nutrition: Nutrition?): List<String> {
         val nutritions = nutrition?.toMap()?.toList()?.map { p -> resources.getString(p.first, p.second) }
         if (nutritions.isNullOrEmpty()) {
            return emptyList()
         }
         return nutritions
      }
   }
}
