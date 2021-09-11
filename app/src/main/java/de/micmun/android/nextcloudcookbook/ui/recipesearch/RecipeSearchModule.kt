package de.micmun.android.nextcloudcookbook.ui.recipesearch

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import de.micmun.android.nextcloudcookbook.util.ViewModelBuilder
import de.micmun.android.nextcloudcookbook.util.ViewModelKey

@Module
abstract class RecipeSearchModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun recipeSearchFragment(): RecipeSearchFragment

    @Binds
    @IntoMap
    @ViewModelKey(RecipeSearchViewModel::class)
    abstract fun bindViewModel(viewModel: RecipeSearchViewModel): ViewModel
}