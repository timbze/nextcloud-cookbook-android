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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.databinding.FragmentDownloadFormBinding
import de.micmun.android.nextcloudcookbook.ui.recipelist.DownloadRequest
import de.micmun.android.nextcloudcookbook.ui.recipelist.RecipeListViewModel
import de.micmun.android.nextcloudcookbook.ui.recipelist.RecipeListViewModelFactory
import java.net.URL

/**
 * Fragment for recipe download form.
 *
 * @author Leafar
 * @version 1.0, 18.04.21
 */
class DownloadFormFragment : Fragment(), DownloadClickListener {
   private lateinit var binding: FragmentDownloadFormBinding
   private lateinit var recipesViewModel: RecipeListViewModel

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_download_form, container, false)
      binding.clickListener = this

      val recipeListViewModelFactory = RecipeListViewModelFactory(requireActivity().application)
      recipesViewModel = ViewModelProvider(this, recipeListViewModelFactory).get(RecipeListViewModel::class.java)

      recipesViewModel.isDownloading.observe (viewLifecycleOwner, { isDownloading ->
         binding.downloadBtn.isEnabled = !isDownloading
      })

      return binding.root
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      (requireActivity() as AppCompatActivity).supportActionBar
              ?.title = resources.getString(R.string.form_download_title)
   }

   override fun doDownload() {
      // TODO should we enable cleartext traffic (http)?
      val url = URL(binding.recipeUrlTxt.text.toString())
      val request = DownloadRequest(url, binding.recipeOverridePath.text.toString(),
              binding.replaceExistingChkBox.isChecked)
      recipesViewModel.download(request)
   }
}

interface DownloadClickListener {
   fun doDownload()
}
