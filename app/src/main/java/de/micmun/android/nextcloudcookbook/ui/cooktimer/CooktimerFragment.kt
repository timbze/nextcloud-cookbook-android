/*
 * CooktimerFragment.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.cooktimer

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import de.micmun.android.nextcloudcookbook.MainApplication
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.databinding.FragmentCooktimerBinding
import de.micmun.android.nextcloudcookbook.db.model.DbRecipe
import de.micmun.android.nextcloudcookbook.services.CooktimerService
import de.micmun.android.nextcloudcookbook.services.RemainReceiver
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModel
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModelFactory
import de.micmun.android.nextcloudcookbook.ui.widget.BlinkAnimation
import de.micmun.android.nextcloudcookbook.util.DurationUtils
import de.micmun.android.nextcloudcookbook.util.ManagedAlarmPlayer
import de.micmun.android.nextcloudcookbook.util.ManagedVibrator
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Fragment for cook timer.
 *
 * @author MicMun
 * @version 1.1, 28.08.21
 */
class CooktimerFragment : Fragment() {
   private lateinit var binding: FragmentCooktimerBinding
   private lateinit var viewModel: CooktimeViewModel
   private lateinit var settingViewModel: CurrentSettingViewModel

   private val animation = BlinkAnimation()
   private lateinit var alarmPlayer: ManagedAlarmPlayer
   private lateinit var vibrator: ManagedVibrator

   private lateinit var currentRecipe: DbRecipe
   private var remains: Long = -1L

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cooktimer, container, false)

      // arguments for timer
      val args = CooktimerFragmentArgs.fromBundle(requireArguments())
      val recipeId = args.recipeId

      // view model
      val factory = CooktimeViewModelFactory(recipeId, requireActivity().application)
      viewModel = ViewModelProvider(this, factory).get(CooktimeViewModel::class.java)
      binding.lifecycleOwner = viewLifecycleOwner
      val settingFactory = CurrentSettingViewModelFactory(MainApplication.AppContext)
      settingViewModel =
         ViewModelProvider(MainApplication.AppContext, settingFactory).get(CurrentSettingViewModel::class.java)

      // initialize alarm and vibration
      alarmPlayer = ManagedAlarmPlayer(requireContext())
      vibrator = ManagedVibrator(requireContext())

      // reset timer button
      binding.resetTimerBtn.setOnClickListener {
         // reset timer
         viewModel.stopTimer()
         viewModel.resetTimer()
      }
      setTooltip(binding.resetTimerBtn, R.string.cooktime_reset_btn)

      return binding.root
   }

   override fun onSaveInstanceState(outState: Bundle) {
      outState.putBoolean("ALARM_PLAYING", alarmPlayer.isPlaying)
      outState.putBoolean("VIBRATOR_VIBRATING", vibrator.isVibrating)
      alarmPlayer.stop()
      vibrator.stop()
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      @Suppress("DEPRECATION")
      super.onActivityCreated(savedInstanceState)
      if (savedInstanceState?.getBoolean("ALARM_PLAYING") == true) playAlarm()
      if (savedInstanceState?.getBoolean("VIBRATOR_VIBRATING") == true) vibrate()
   }

   override fun onPause() {
      if (viewModel.state.value == CooktimeViewModel.CooktimeState.RUNNING) {
         startService(viewModel.total!! - viewModel.currentMillis.value!!)
      }
      super.onPause()
   }

   override fun onResume() {
      super.onResume()

      // if service started, take value and stop service
      if (MainApplication.AppContext.receiver != null) {
         remains = MainApplication.AppContext.receiver!!.remains ?: -1
         stopService()
      }
      observeTimerValues()
   }

   /**
    * Observes the recipe, current remaining time and state of the timer.
    */
   private fun observeTimerValues() {
      // observe recipe
      viewModel.recipe.observe(viewLifecycleOwner, { dbRecipe ->
         dbRecipe?.let { recipe ->
            binding.recipe = recipe
            if (viewModel.total == null) {
               viewModel.total = DurationUtils.durationInSeconds(recipe.recipeCore.cookTime) * 1000
               refreshUi(0L)

               // start button
               binding.startTimerBtn.setOnClickListener {
                  viewModel.startTimer()
               }
            }
            currentRecipe = recipe
            setTitle()

            // if it is not the first start, starting with current value again
            if (remains != -1L) {
               viewModel.setCurrentMillis(viewModel.total!! - remains)
               viewModel.startTimer()
            }
         }
      })

      // update current remaining time
      viewModel.currentMillis.observe(viewLifecycleOwner, {
         it?.let { millis ->
            refreshUi(millis)
         }
      })

      // observe state of timer
      viewModel.state.observe(viewLifecycleOwner, {
         it?.let { state ->
            handleState(state)
         }
      })
   }

   private fun startService(remains: Long) {
      if (checkServicePermission()) {
         MainApplication.AppContext.receiver = RemainReceiver()
         LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(MainApplication.AppContext.receiver!!, IntentFilter(RemainReceiver.REMAIN_ACTION))
         val intent = cooktimeService()
         intent.putExtra("RECIPE_ID", currentRecipe.recipeCore.id)
         intent.putExtra("COOK_TIME", remains)
         requireActivity().startService(intent)
      }
   }

   private fun stopService() {
      requireActivity().stopService(cooktimeService())
      MainApplication.AppContext.receiver?.let {
         LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
         MainApplication.AppContext.receiver = null
      }
   }

   /**
    * Returns <code>true</code>, if the app has the permission to start an foreground service.
    *
    * @return <code>true</code>, if the app has the permission to start an foreground service.
    */
   private fun checkServicePermission(): Boolean {
      var result = true

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
         Dexter.withContext(requireContext())
            .withPermissions(Manifest.permission.FOREGROUND_SERVICE)
            .withListener(object : BaseMultiplePermissionsListener() {
               override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                  result = report.areAllPermissionsGranted()
               }
            }).check()
      } else
         result = true

      return result
   }

   /**
    * Returns the intent for the countdown service.
    *
    * @return the intent for the countdown service.
    */
   private fun cooktimeService() = Intent(activity, CooktimerService::class.java)

   /**
    * Handle the current state.
    *
    * @param state current state.
    */
   private fun handleState(state: CooktimeViewModel.CooktimeState) {
      when (state) {
         CooktimeViewModel.CooktimeState.NOT_STARTED -> {
            // animations
            blinkText(false)
            binding.timerView.clearAnimation()
            if (alarmPlayer.isPlaying)
               alarmPlayer.stop()
            if (vibrator.isVibrating)
               vibrator.stop()
            // button
            binding.startTimerBtn.apply {
               setImageResource(R.drawable.ic_start)
               setTooltip(this, R.string.cooktime_start_btn)
               setOnClickListener { viewModel.startTimer() }
            }
         }
         CooktimeViewModel.CooktimeState.PAUSED -> { // timer is paused
            // animations
            blinkText(true)

            // button
            binding.startTimerBtn.apply {
               setImageResource(R.drawable.ic_start)
               setTooltip(this, R.string.cooktime_start_btn)
               setOnClickListener { viewModel.startTimer() }
            }
         }
         CooktimeViewModel.CooktimeState.FINISHED -> { // timer finished (no remaining time)
            // animations
            binding.timerView.startAnimation(animation)
            // alarms
            playAlarm()
            vibrate()
            viewModel.stopTimer()
            viewModel.resetTimer()
         }
         else -> { // running
            // animations
            blinkText(false)
            // button
            binding.startTimerBtn.apply {
               setImageResource(R.drawable.ic_pause)
               setTooltip(this, R.string.cooktime_pause_btn)
               setOnClickListener { viewModel.stopTimer() }
            }
         }
      }
   }

   /**
    * Refresh the ui elements with current time.
    *
    * @param currentMillis current remaining time in milliseconds.
    */
   private fun refreshUi(currentMillis: Long) {
      if (viewModel.total != null) {
         val remainingTime = viewModel.total!! - currentMillis
         binding.textRemainTime.text = DurationUtils.formatDurationSeconds(remainingTime / 1000)

         // progress bar
         if (remainingTime >= 0) {
            if (binding.timerView.max != viewModel.total!!.toInt())
               binding.timerView.max = viewModel.total!!.toInt()
            binding.timerView.setProgress(viewModel.currentMillis.value!!.toFloat(), true, 1000)
         } else if (binding.timerView.max != 1) {
            binding.timerView.setProgress(1f)
            binding.timerView.max = 1
         }
      }
   }

   /**
    * Play the alarm.
    */
   private fun playAlarm() {
      alarmPlayer.play(R.raw.timer_expire_short)
      Executors.newSingleThreadScheduledExecutor().schedule({ alarmPlayer.stop() }, 20, TimeUnit.SECONDS)
   }

   /**
    * Start vibration.
    */
   private fun vibrate() {
      val vibratorPattern = longArrayOf(0, 100, 100, 200, 500)
      vibrator.vibrate(vibratorPattern)
      Executors.newSingleThreadScheduledExecutor().schedule({ vibrator.stop() }, 10, TimeUnit.SECONDS)
   }

   /**
    * Blink text if enabled == <code>true</code>.
    */
   private fun blinkText(enabled: Boolean) {
      if (enabled && binding.textRemainTime.animation == null) {
         binding.textRemainTime.startAnimation(animation)
      } else {
         binding.textRemainTime.clearAnimation()
      }
   }

   /**
    * Sets the tooltip on an ImageButton.
    *
    * @param ib ImageButton.
    * @param resourceId id of string resource.
    */
   private fun setTooltip(ib: ImageButton, resourceId: Int) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         ib.tooltipText = getString(resourceId)
      }
   }

   /**
    * Sets the title in actionbar for the fragment.
    */
   private fun setTitle() {
      (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.cooktime_title)
   }
}
