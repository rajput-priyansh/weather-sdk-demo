package com.vibs.weatherdemosdk.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibs.weatherapisdk.Resource
import com.vibs.weatherapisdk.models.ResponseForecast
import com.vibs.weatherapisdk.models.ResponseWeather
import com.vibs.weatherdemosdk.repository.WeatherRepository

class WeatherViewModel : ViewModel() {
    private val authRepository = WeatherRepository(viewModelScope)

    private val _forecastResponse = MutableLiveData<ResponseForecast?>()
    private val _weatherResponse = MutableLiveData<ResponseWeather?>()


    val forecastResponse : LiveData<ResponseForecast?> = _forecastResponse
    val weatherResponse : LiveData<ResponseWeather?> = _weatherResponse

    fun setForecastResponseData(data: ResponseForecast?) {
        _forecastResponse.value = data
    }

    fun setWeatherResponseData(data: ResponseWeather?) {
        _weatherResponse.value = data
    }

    fun getWeatherDetails(city: String): LiveData<Resource<ResponseWeather>> =
        authRepository.getWeatherDetails(city)

    fun getForecastDetails(lat: Double, lon: Double): LiveData<Resource<ResponseForecast>> =
        authRepository.getForecastDetails(lat, lon)
}