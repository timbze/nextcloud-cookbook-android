package de.micmun.android.nextcloudcookbook.api.apibase

import de.micmun.android.nextcloudcookbook.api.apimodel.TokenResponse
import retrofit2.http.*

interface TokenApi {
    @Headers("OCS-APIRequest: true", "User-Agent: Nextcloud Cookbook Android")
    @GET("ocs/v2.php/core/getapppassword?format=json")
    suspend fun getToken(@Header("Authorization") basicAuthCredentials: String): TokenResponse?
}