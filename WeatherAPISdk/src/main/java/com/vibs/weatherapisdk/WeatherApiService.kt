package com.vibs.weatherapisdk

import androidx.lifecycle.LiveData
import com.vibs.weatherapisdk.models.ResponseForecast
import com.vibs.weatherapisdk.models.ResponseWeather

import retrofit2.http.*

/**
 * REST API access points
 */
interface WeatherApiService {

    @GET("data/2.5/weather")
    fun getWeatherDetails(@Query("q") city: String, @Query("units") units: String): LiveData<ApiResponse<ResponseWeather>>?

    @GET("data/2.5/forecast")
    fun getForecastDetails(@Query("lat") lat: Double, @Query("lon") lon: Double): LiveData<ApiResponse<ResponseForecast>>?

}
