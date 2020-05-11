package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.databinding.FragmentDetailBinding

/**
 * A simple [Fragment] subclass.
 */
class RecipeDetailFragment : Fragment() {
   private lateinit var binding: FragmentDetailBinding
   private lateinit var viewModel: RecipeViewModel

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

      Log.i("RecipeDetailFragment", "ViewModelProvider called")
      val args = RecipeDetailFragmentArgs.fromBundle(requireArguments())
      val viewModelFactory = RecipeViewModelFactory(args.recipeId, requireActivity().application)
      viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeViewModel::class.java)
      binding.recipeViewModel = viewModel
      binding.lifecycleOwner = this

      return binding.root
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      val title = viewModel.recipe.value!!.name
      (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.recipe_title_bar, title)
   }
}
