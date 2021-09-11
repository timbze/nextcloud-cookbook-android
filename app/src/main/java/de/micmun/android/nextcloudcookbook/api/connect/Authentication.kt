package de.micmun.android.nextcloudcookbook.api.connect

import android.util.Log
import de.micmun.android.nextcloudcookbook.api.apibase.TokenApi
import de.micmun.android.nextcloudcookbook.api.exception.ApiException
import de.micmun.android.nextcloudcookbook.util.di.ApplicationScope
import okhttp3.Credentials
import javax.inject.Inject

@ApplicationScope
class Authentication @Inject constructor(
    private val apiBasics: ApiBasics
) {

    suspend fun getToken(username: String, password: String): String? {
        Log.d(TAG, "GetToken username = $username")
        val api = tokenApi ?: throw ApiException("The TokenApi could not be created. Has the server address been saved?")
        return try {
            val basicAuthHeader = Credentials.basic(username, password)
            api.getToken(basicAuthHeader)?.ocs?.data?.apppassword
        } catch (e: Exception) {
            throw ApiException("There was an exception trying to get token from server", e)
        }
    }

    private val tokenApi: TokenApi? by lazy {
        apiBasics.retrofit()?.create(TokenApi::class.java)
    }

    companion object {
        private const val TAG = "Authentication"
    }
}