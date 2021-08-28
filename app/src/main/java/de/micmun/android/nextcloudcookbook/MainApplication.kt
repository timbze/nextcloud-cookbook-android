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
import de.micmun.android.nextcloudcookbook.services.RemainReceiver

/**
 * Application of the app.
 *
 * @author MicMun
 * @version 1.1, 28.02.21
 */
class MainApplication : Application(), ViewModelStoreOwner {
   private val appViewModelStore: ViewModelStore by lazy {
      ViewModelStore()
   }

   override fun getViewModelStore(): ViewModelStore = appViewModelStore

   override fun onCreate() {
      super.onCreate()
      Log.i(TAG, "Starting app")
      AppContext = this
   }

   companion object {
      private const val TAG = "MainApplication"
      lateinit var AppContext: MainApplication
   }

   var receiver: RemainReceiver? = null
}
