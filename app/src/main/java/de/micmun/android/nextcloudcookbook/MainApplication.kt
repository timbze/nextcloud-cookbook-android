/*
 * MainApplication.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import de.micmun.android.nextcloudcookbook.security.Crypto
import de.micmun.android.nextcloudcookbook.services.RemainReceiver
import de.micmun.android.nextcloudcookbook.util.di.AppComponent
import de.micmun.android.nextcloudcookbook.util.di.AppModule
import de.micmun.android.nextcloudcookbook.util.di.DaggerAppComponent
import de.micmun.android.nextcloudcookbook.util.di.DatabaseModule

/**
 * Application of the app.
 *
 * @author MicMun
 * @version 1.1, 28.02.21
 */
class MainApplication : DaggerApplication() {
   override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
      DaggerAppComponent.factory().create(applicationContext)

   override fun onCreate() {
      super.onCreate()
      Log.i(TAG, "Starting app")
      AppContext = this

      Crypto.generateSecretKey()
   }

   companion object {
      private const val TAG = "MainApplication"
      lateinit var AppContext: MainApplication
   }

   var receiver: RemainReceiver? = null
}
