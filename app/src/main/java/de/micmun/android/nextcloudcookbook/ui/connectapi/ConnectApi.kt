package de.micmun.android.nextcloudcookbook.ui.connectapi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import dagger.android.support.DaggerFragment
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.api.connect.ApiBasics
import de.micmun.android.nextcloudcookbook.api.connect.Authentication
import de.micmun.android.nextcloudcookbook.databinding.ConnectApiBinding
import de.micmun.android.nextcloudcookbook.security.Crypto
import de.micmun.android.nextcloudcookbook.util.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConnectApi : DaggerFragment() {
    private lateinit var binding: ConnectApiBinding

    @Inject lateinit var apiBasics: ApiBasics
    @Inject lateinit var authentication: Authentication

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ConnectApiBinding.inflate(inflater, container, false)
        binding.apply {
            loginButton.setOnClickListener {
                val address = "https://" + address.text.toString().trim()
                val username = username.text.toString().trim()
                val password = password.text.toString()
                val prefs = CommonUtils.sharedPreferences

                apiBasics.saveServerAddress(address)

                lifecycleScope.launch(Dispatchers.IO) {
                    val token: String?
                    try {
                        token = authentication.getToken(username, password)
                        token ?: return@launch
                    } catch (e: Exception) {
                        Log.e(TAG, "Could not get token", e)
                        return@launch
                    }

                    prefs.edit {
                        putString("token", Crypto.encryptData(token))
                        apply()
                    }

                    context?.let {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(it, R.string.address_saved, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        return binding.root
    }

    companion object {
        private const val TAG = "ConnectApi"
    }
}