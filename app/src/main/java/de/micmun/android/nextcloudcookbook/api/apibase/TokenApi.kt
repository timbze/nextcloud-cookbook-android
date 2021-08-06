package de.micmun.android.nextcloudcookbook.api.apibase

import de.micmun.android.nextcloudcookbook.api.apimodel.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {
    /**
     * @param userPassword must be in the format username:password
     * NC docs: This route will return the API token for the authenticated user. If no token exists, one will be generated.
     */
    @POST("api/v1/token")
    suspend fun getToken(@Body userPassword: String): TokenResponse?
}