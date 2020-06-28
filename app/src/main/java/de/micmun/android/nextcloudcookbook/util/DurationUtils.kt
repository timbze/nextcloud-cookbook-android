/*
 * DurationUtils.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.util.*

/**
 * Utilities for the duration format.
 *
 * @author MicMun
 * @version 1.0, 27.06.20
 */
class DurationUtils {
   companion object {
      /**
       * Returns the better formatted String from the iso 8601 format.
       *
       * @param isoString Duration in iso 8601 format.
       * @return formatted String to display.
       */
      fun formatStringToDuration(isoString: String): String {
         var displayString = ""
         displayString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getDisplayString(isoString)
         } else {
            getLegacyDisplayString(isoString)
         }

         return displayString
      }

      /**
       * Returns the formatted string from the iso 8601 string (>= API 26).
       *
       * @param isoString Duration in iso 8601 format.
       * @return formatted string.
       */
      @RequiresApi(Build.VERSION_CODES.O)
      private fun getDisplayString(isoString: String): String {
         val duration = Duration.parse(isoString)
         var minutes = duration.toMinutes()
         var hours = 0

         while (minutes >= 60) {
            hours += 1
            minutes -= 60
         }

         return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
      }

      /**
       * Returns the formatted string from the iso 8601 string (< API 26).
       *
       * @param isoString Duration in iso 8601 format.
       * @return formatted string.
       */
      private fun getLegacyDisplayString(isoString: String): String {
         val start = isoString.indexOf("T") + 1
         val reduced = isoString.substring(start, isoString.length)
         val hourIndex = reduced.indexOf("H")
         val minuteIndex = reduced.indexOf("M")
         var hours = 0
         var minutes = 0

         if (hourIndex != -1) {
            hours = reduced.substring(0, hourIndex).toInt()
         }
         if (minuteIndex != -1) {
            minutes = reduced.substring(if (hourIndex != -1) hourIndex + 1 else 0, minuteIndex).toInt()
         }

         return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
      }
   }
}
