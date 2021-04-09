/*
 * MainApplication.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

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
}
