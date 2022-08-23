package com.vibs.weatherdemosdk.repository

import androidx.lifecycle.LiveData
import com.vibs.weatherapisdk.*
import com.vibs.weatherapisdk.models.ResponseForecast
import com.vibs.weatherapisdk.models.ResponseWeather
import kotlinx.coroutines.CoroutineScope

class WeatherRepository (val coroutineScope: CoroutineScope) {

    private val restApiService: WeatherApiService? = RetrofitApiService(
        WeatherApiService::class.java
    ).getInterface()

    fun getWeatherDetails(city: String): LiveData<Resource<ResponseWeather>> {
        return object : NetworkBoundResource<ResponseWeather>(coroutineScope) {
            override fun createCall(): LiveData<ApiResponse<ResponseWeather>>? {
                return restApiService?.getWeatherDetails(city, "imperial")
            }
        }.asLiveData()
    }

    fun getForecastDetails(lat: Double, lon: Double): LiveData<Resource<ResponseForecast>> {
        return object : NetworkBoundResource<ResponseForecast>(coroutineScope) {
            override fun createCall(): LiveData<ApiResponse<ResponseForecast>>? {
                return restApiService?.getForecastDetails(lat, lon)
            }
        }.asLiveData()
    }
}