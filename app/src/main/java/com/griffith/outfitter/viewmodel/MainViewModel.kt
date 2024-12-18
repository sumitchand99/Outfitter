package com.griffith.outfitter.viewmodel

import com.griffith.outfitter.network.RetrofitClient
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griffith.outfitter.model.MyLatLng
import com.griffith.outfitter.model.forecast.ForecastResult
import com.griffith.outfitter.model.weather.WeatherResult
import kotlinx.coroutines.launch

enum class STATE {
    LOADING,
    SUCCESS,
    FAILED
}

class MainViewModel: ViewModel() {
    var state by mutableStateOf(STATE.LOADING)
    var weatherResponse : WeatherResult by mutableStateOf(WeatherResult())
    var forecastResponse : ForecastResult by mutableStateOf(ForecastResult())

    var errorMessage : String by mutableStateOf("")

    fun getWeatherByLocation(latLng: MyLatLng) {
        viewModelScope.launch {
            state = STATE.LOADING
            val apiService = RetrofitClient.getInstance()

            try{
                val apiResponse = apiService.getWeather(latLng.lat, latLng.lon)
                weatherResponse = apiResponse
                state = STATE.SUCCESS
            }catch (e: Exception){
                errorMessage = e.message!!.toString()
                state = STATE.FAILED
            }
        }
    }

    fun getForecastByLocation(latLng: MyLatLng) {
        viewModelScope.launch {
            state = STATE.LOADING
            val apiService = RetrofitClient.getInstance()
            try{
                val apiResponse = apiService.getForecast(latLng.lat, latLng.lon)
                forecastResponse = apiResponse
                state = STATE.SUCCESS
            }catch (e: Exception){
                errorMessage = e.message!!.toString()
                state = STATE.FAILED
            }
        }
    }
}