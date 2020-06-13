/*
 * RecipeIngredientsAdapter.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.micmun.android.nextcloudcookbook.databinding.InstructionsItemBinding

/**
 * Adapter for recipe ingredients.
 *
 * @author MicMun
 * @version 1.0, 02.06.20
 */
class RecipeInstructionsAdapter(private val instructions: Array<String>) :
   RecyclerView.Adapter<RecipeInstructionsAdapter.InstructionsViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionsViewHolder {
      return InstructionsViewHolder.from(parent)
   }

   override fun getItemCount() = instructions.size

   override fun onBindViewHolder(holder: InstructionsViewHolder, position: Int) {
      val ingredient = instructions[position].trim()
      holder.bind(ingredient)
   }

   class InstructionsViewHolder private constructor(val binding: InstructionsItemBinding) :
      RecyclerView.ViewHolder(binding.root) {
      /**
       * Binds the data to the views.
       *
       * @param instruction Instruction String to show in view.
       */
      fun bind(instruction: String) {
         binding.instruction = instruction
         binding.executePendingBindings()
      }

      companion object {
         fun from(parent: ViewGroup): InstructionsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = InstructionsItemBinding.inflate(layoutInflater, parent, false)
            return InstructionsViewHolder(binding)
         }
      }
   }
}
