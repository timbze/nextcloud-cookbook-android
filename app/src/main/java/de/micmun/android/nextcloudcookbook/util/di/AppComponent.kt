package de.micmun.android.nextcloudcookbook.util.di

import android.app.Application
import android.content.Context
import dagger.Component
import de.micmun.android.nextcloudcookbook.MainApplication
import de.micmun.android.nextcloudcookbook.db.RecipeDatabase
import de.micmun.android.nextcloudcookbook.ui.MainActivity
import javax.inject.Scope

@ApplicationScope
@Component(modules = [AppModule::class, DatabaseModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(posApplication: MainApplication)

    fun recipeDatabase(): RecipeDatabase
    fun appContext(): Context
    fun application(): Application

    companion object {
        lateinit var instance: AppComponent
        val isInitialized get() = ::instance.isInitialized
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentScope
