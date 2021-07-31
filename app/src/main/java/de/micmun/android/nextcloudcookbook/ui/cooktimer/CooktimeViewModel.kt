/*
 * CooktimeViewModel.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.cooktimer

import android.app.Application
import androidx.lifecycle.*
import de.micmun.android.nextcloudcookbook.db.DbRecipeRepository
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * ViewModel for the cooktime fragment.
 *
 * @author MicMun
 * @version 1.0, 30.07.21
 */
class CooktimeViewModel(id: Long, app: Application) : AndroidViewModel(app) {
   private val repository = DbRecipeRepository.getInstance(app)
   val recipe = repository.getRecipe(id)

   internal var total: Long? = null // total milliseconds

   // current milliseconds remaining time
   private val _currentMillis = MutableLiveData<Long>()
   internal val currentMillis: LiveData<Long>
      get() = _currentMillis

   // state of the timer
   private val _state = MutableLiveData<CooktimeState>()
   val state: LiveData<CooktimeState>
      get() = _state

   private var executor = Executors.newSingleThreadScheduledExecutor()

   init {
      this._currentMillis.value = 0L
      _state.value = CooktimeState.NOT_STARTED
   }

   /**
    * Starts the timer.
    */
   internal fun startTimer() {
      executor.shutdownNow()
      executor = Executors.newSingleThreadScheduledExecutor()
      executor.scheduleAtFixedRate({
                                      this._currentMillis.postValue(
                                         this.currentMillis.value!! + 1000)
                                      _state.postValue(
                                         when {
                                            this.currentMillis.value!! < total!! -> CooktimeState.RUNNING
                                            else -> CooktimeState.FINISHED
                                         })
                                   }, 0, 1000, TimeUnit.MILLISECONDS)
   }

   /**
    * Stops the timer.
    */
   internal fun stopTimer() {
      executor.shutdownNow()
      _state.value = CooktimeState.PAUSED
   }

   /**
    * Sets timer back to zero.
    */
   internal fun resetTimer() {
      _currentMillis.value = 0L
      _state.value = CooktimeState.NOT_STARTED
   }

   /**
    * Sets the current milliseconds.
    *
    * @param millis current milliseconds.
    */
   internal fun setCurrentMillis(millis: Long) {
      _currentMillis.value = millis
   }

   override fun onCleared() {
      executor.shutdownNow()
   }

   /**
    * Enumeration for state of Timer.
    *
    * @author MicMun
    * @version 1.0, 28.07.21
    */
   enum class CooktimeState {
      RUNNING,
      PAUSED,
      FINISHED,
      NOT_STARTED
   }
}

/**
 * Factory for ViewModel.
 *
 * @author MicMun
 * @version 1.0, 28.07.21
 */
class CooktimeViewModelFactory(private val id: Long, private val application: Application)
   : ViewModelProvider.Factory {
   override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(CooktimeViewModel::class.java)) {
         @Suppress("UNCHECKED_CAST")
         return CooktimeViewModel(id, application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
   }
}
