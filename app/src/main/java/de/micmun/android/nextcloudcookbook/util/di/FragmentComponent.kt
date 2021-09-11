package de.micmun.android.nextcloudcookbook.util.di

import android.app.Application
import android.content.Context
import dagger.Component
import de.micmun.android.nextcloudcookbook.MainApplication
import de.micmun.android.nextcloudcookbook.db.RecipeDatabase
import de.micmun.android.nextcloudcookbook.ui.MainActivity
import javax.inject.Scope

@FragmentScope
@Component(dependencies = [AppComponent::class])
interface FragmentComponent {


}
