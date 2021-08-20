package de.micmun.android.nextcloudcookbook.ui.connectapi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import de.micmun.android.nextcloudcookbook.api.connect.ApiBasics
import de.micmun.android.nextcloudcookbook.api.connect.Authentication
import de.micmun.android.nextcloudcookbook.databinding.ConnectApiBinding
import de.micmun.android.nextcloudcookbook.security.Crypto
import de.micmun.android.nextcloudcookbook.util.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ConnectApi : Fragment() {
    private lateinit var binding: ConnectApiBinding
    private lateinit var viewModel: ConnectApiViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ConnectApiBinding.inflate(inflater, container, false)
        binding.apply {
            loginButton.setOnClickListener {
                val address = "https://" + address.text.toString().trim()
                val username = username.text.toString().trim()
                val password = password.text.toString()
                val prefs = CommonUtils.sharedPreferences

                ApiBasics.saveServerAddress(address)

                GlobalScope.launch(Dispatchers.IO) {
                    val token: String?
                    try {
                        token = Authentication.getToken(username, password)
                        token ?: return@launch
                    } catch (e: Exception) {
                        Log.e(TAG, "Could not get token", e)
                        return@launch
                    }

                    prefs.edit {
                        putString("token", Crypto.encryptData(token))
                        apply()
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