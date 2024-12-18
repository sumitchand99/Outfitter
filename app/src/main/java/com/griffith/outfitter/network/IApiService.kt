package com.griffith.outfitter.network

import com.griffith.outfitter.constant.Const.Companion.OPENWEATHERAPIKEY
import com.griffith.outfitter.model.forecast.ForecastResult
import com.griffith.outfitter.model.weather.WeatherResult
import retrofit2.http.GET
import retrofit2.http.Query

interface IApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") appId: String = OPENWEATHERAPIKEY
    ): WeatherResult

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") appId: String = OPENWEATHERAPIKEY
    ): ForecastResult
}
