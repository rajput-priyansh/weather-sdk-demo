package com.vibs.weatherapisdk.models

data class Error(
    val errorCode: Int,
    val errorUserMessage: String,
    val errorMessage: String,
    val httpErrorCode: Int
    )