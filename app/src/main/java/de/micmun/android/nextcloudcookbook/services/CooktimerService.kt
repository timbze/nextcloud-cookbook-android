/*
 * CooktimerService.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import de.micmun.android.nextcloudcookbook.MainApplication
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.util.DurationUtils

/**
 * Service for timer.
 *
 * @author MicMun
 * @version 1.1, 28.08.21
 */
class CooktimerService : LifecycleService() {
   private lateinit var viewModel: CooktimerServiceViewModel

   private lateinit var notificationBuilder: NotificationCompat.Builder
   private lateinit var notification: Notification
   private var remains: Long = 10000L
   private var recipeId: Long = -1L
   private var pendingIntent: PendingIntent? = null

   companion object {
      // Channel ID for notification channel
      const val CHANNEL_ID = "nc_cooktimer"
   }

   override fun onCreate() {
      super.onCreate()
      val factory = CooktimerServiceViewModelFactory(application)
      viewModel = ViewModelProvider(MainApplication.AppContext, factory).get(CooktimerServiceViewModel::class.java)
   }

   override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
      super.onStartCommand(intent, flags, startId)

      val remains = intent?.extras?.getLong("COOK_TIME")
      recipeId = intent?.extras?.getLong("RECIPE_ID") ?: -1L

      if (remains != null) {
         this.remains = remains
         viewModel.setTimer(remains)
      }

      if (pendingIntent == null) {
         pendingIntent = buildPendingIntent()
      }
      createNotificationChannel() // creating notification channel (only Android.O+)

      // create notification
      notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
         .setCategory(Notification.CATEGORY_ALARM)
         .setContentTitle(getString(R.string.notification_title))
         .setSmallIcon(R.drawable.ic_timer)
         .setContentIntent(pendingIntent)
         .setOnlyAlertOnce(true)
         .setAutoCancel(true)
         .setOngoing(true)
      notification = notificationBuilder.build()
      NotificationManagerCompat.from(this).notify(1, notification)

      // Observe timer and start it
      viewModel.cooktimer.observe(this, {
         it?.let { timer ->
            viewModel.startTimer(timer)
         }
      })

      // current remaining time
      viewModel.remains.observe(this, {
         it?.let { remains ->
            this.remains = remains
            if (remains > 0L) {
               // update notification
               notificationBuilder.setContentText(
                  getString(R.string.notification_text, DurationUtils.formatDurationSeconds(remains / 1000)))
            }
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(buildIntent(remains))

            pendingIntent = buildPendingIntent()
            notificationBuilder.setContentIntent(pendingIntent)
            notification = notificationBuilder.build()
            NotificationManagerCompat.from(this).notify(1, notification)

            if (remains == 0L) { // timer ends
               viewModel.stopTimer()
               notification.contentIntent.send()

               // stop service
               stopForeground(true)
               stopSelf()
            }
         }
      })

      // start service
      startForeground(1, notification)

      return START_NOT_STICKY
   }

   override fun onDestroy() {
      viewModel.stopTimer()
      stopForeground(true)
      stopSelf()
      super.onDestroy()
   }

   /**
    * Creates a notification channel when Android version >= O (API 26+).
    */
   private fun createNotificationChannel() {
      // Create the NotificationChannel, but only on API 26+ because
      // the NotificationChannel class is new and not in the support library
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         val name = getString(R.string.channel_name)
         val descriptionText = getString(R.string.channel_description)
         val importance = NotificationManager.IMPORTANCE_DEFAULT
         val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
         }
         // Register the channel with the system
         NotificationManagerCompat.from(this).createNotificationChannel(channel)
      }
   }

   /**
    * Returns a pending intent for notification.
    *
    * @return PendingIntent.
    */
   private fun buildPendingIntent(): PendingIntent {
      val bundle = Bundle()
      bundle.putLong("recipeId", recipeId)
      bundle.putBoolean("isServiceStarted", true)

      return NavDeepLinkBuilder(this)
         .setGraph(R.navigation.navigation)
         .setDestination(R.id.recipeDetailFragment)
         .setArguments(bundle)
         .createPendingIntent()
   }

   private fun buildIntent(remains: Long): Intent {
      val intent = Intent()
      intent.action = RemainReceiver.REMAIN_ACTION
      intent.putExtra(RemainReceiver.KEY_REMAINS, remains)
      return intent
   }
}
