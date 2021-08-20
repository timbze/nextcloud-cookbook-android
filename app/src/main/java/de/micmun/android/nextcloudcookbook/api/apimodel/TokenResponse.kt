package de.micmun.android.nextcloudcookbook.api.apimodel

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse (val ocs: OcsModel)

@Serializable
data class OcsModel(
    val data: AppPasswordData
)

@Serializable
data class AppPasswordData(
    @Suppress("SpellCheckingInspection")
    val apppassword: String
)