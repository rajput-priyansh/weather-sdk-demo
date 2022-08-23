package com.vibs.weatherapisdk.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse (
    @SerialName("errorCode")
    var errorCode: Int?,

    @SerialName("errorUserMessage")
    var userErrorMessage: String? = "",

    @SerialName("errorMessage")
    var errorMessage: String? = ""
)