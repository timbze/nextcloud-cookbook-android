package de.micmun.android.nextcloudcookbook.api.connect

import de.micmun.android.nextcloudcookbook.api.apibase.TokenApi
import de.micmun.android.nextcloudcookbook.api.exception.ApiException

class Authentication {
    companion object {

        suspend fun getToken(username: String, password: String): String? {
            val api = tokenApi ?: throw ApiException("The TokenApi could not be created. Has the server address been saved?")
            return try {
                api.getToken("$username:$password")?.token
            } catch (e: Exception) {
                throw ApiException("There was an exception trying to get token from server", e)
            }
        }

        private val tokenApi: TokenApi? by lazy {
            ApiBasics.retrofit()?.create(TokenApi::class.java)
        }
    }
}