/*
 * StorageManager.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.SimpleStorage

/**
 * Manages the storage access.
 *
 * @author MicMun
 * @version 1.0, 10.01.21
 */
class StorageManager private constructor() {
   var storage: SimpleStorage? = null

   companion object {
      @Volatile
      private var INSTANCE: StorageManager? = null

      fun getInstance(): StorageManager {
         synchronized(StorageManager::class) {
            var instance = INSTANCE

            if (instance == null) {
               instance = StorageManager()
               INSTANCE = instance
            }
            return instance
         }
      }

      fun getDocumentFromString(context: Context, path: String): DocumentFile? {
         return DocumentFile.fromTreeUri(context, Uri.parse(path))
      }
   }
}
