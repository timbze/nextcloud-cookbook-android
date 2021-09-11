package de.micmun.android.nextcloudcookbook.api.connect

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import de.micmun.android.nextcloudcookbook.settings.SettingKey
import de.micmun.android.nextcloudcookbook.util.CommonUtils
import de.micmun.android.nextcloudcookbook.util.di.ApplicationScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import javax.inject.Inject

@ApplicationScope
class ApiBasics @Inject constructor(
    private val prefs: SharedPreferences
) {

    @OptIn(ExperimentalSerializationApi::class)
    fun retrofit(): Retrofit? {
        val server = serverAddress() ?: return null
        return retrofit ?: Retrofit.Builder()
            .baseUrl("https://${server}/")
            .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
            .build()
    }

    fun saveServerAddress(address: String) {
        if (address.isBlank()) {
            Log.w(TAG, "Can not save blank server address")
            return
        }
        var addressSanitized = address.trim()
        addressSanitized = addressSanitized.replace("https://","").replace("http://", "")
        addressSanitized = addressSanitized.trimEnd('/')

        prefs.edit {
            putString(SettingKey.SERVER_ADDRESS, addressSanitized)
            apply()
        }
    }

    private fun serverAddress(): String? {
        return prefs.getString(SettingKey.SERVER_ADDRESS, null)
    }

    private val json by lazy {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    private var retrofit: Retrofit? = null

    companion object {
        private const val TAG = "ApiBasics"
    }
}