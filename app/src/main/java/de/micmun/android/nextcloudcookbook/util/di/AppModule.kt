package de.micmun.android.nextcloudcookbook.util.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val application: Application) {

    @Provides
    @ApplicationScope
    fun providesApplication(): Application = application

    @Provides
    @ApplicationScope
    fun providesApplicationContext(): Context = application

}