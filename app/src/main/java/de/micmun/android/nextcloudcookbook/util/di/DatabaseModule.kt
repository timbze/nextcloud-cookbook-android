package de.micmun.android.nextcloudcookbook.util.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import de.micmun.android.nextcloudcookbook.db.RecipeDatabase

@Module
class DatabaseModule(private val application: Application) {

    @Provides
    @ApplicationScope
    fun appDatabase(): RecipeDatabase =
        RecipeDatabase.instance ?: synchronized(this) {
            var instance = RecipeDatabase.instance

            if (instance == null) {
                instance = Room
                    .databaseBuilder(application, RecipeDatabase::class.java, "recipe-db")
                    .fallbackToDestructiveMigration()
                    .build()
                RecipeDatabase.instance = instance
            }
            return instance
        }
}