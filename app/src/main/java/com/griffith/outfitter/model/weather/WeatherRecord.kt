package com.griffith.outfitter.model.weather


data class WeatherRecord(
    val location: String,
    val temperature: Double,
    val insert_datetime: String
)