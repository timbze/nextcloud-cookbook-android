/*
 * RecipeViewModel.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.micmun.android.nextcloudcookbook.db.DbRecipeRepository
import de.micmun.android.nextcloudcookbook.db.model.DbRecipe

/**
 * ViewModel for one Recipe.
 *
 * @author MicMun
 * @version 1.1, 07.04.21
 */
class RecipeViewModel(name: String, application: Application) :
   AndroidViewModel(application) {
   private val repository = DbRecipeRepository.getInstance(application)
   val recipe: LiveData<DbRecipe?> = repository.getRecipe(name)
}
