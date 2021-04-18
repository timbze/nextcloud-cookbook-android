/*
 * SearchFormFragment.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.downloadform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.databinding.FragmentDownloadFormBinding

/**
 * Fragment for recipe download form.
 *
 * @author Leafar
 * @version 1.0, 18.04.21
 */
class DownloadFormFragment : Fragment(), DownloadClickListener {
   private lateinit var binding: FragmentDownloadFormBinding

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_download_form, container, false)
      binding.clickListener = this

      return binding.root
   }

   override fun doDownload() {

   }
}

interface DownloadClickListener {
   fun doDownload()
}
