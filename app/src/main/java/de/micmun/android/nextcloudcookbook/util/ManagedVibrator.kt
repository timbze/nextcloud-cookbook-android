package de.micmun.android.nextcloudcookbook.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * Vibration for cooking timer.
 *
 * @author MicMun
 * @version 1.0, 28.07.21
 */
class ManagedVibrator constructor(context: Context) {

   private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

   var isVibrating = false
      private set

   /**
    * Start vibrating with pattern.
    *
    * @param pattern pattern for vibrating.
    */
   fun vibrate(pattern: LongArray) {
      if (!isVibrating) {
         isVibrating = true
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
         } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, 0)
         }
      }
   }

   /**
    * Stop vibrating.
    */
   fun stop() {
      vibrator.cancel()
      isVibrating = false
   }
}
