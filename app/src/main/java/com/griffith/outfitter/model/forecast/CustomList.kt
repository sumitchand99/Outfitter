package com.griffith.outfitter.model.forecast

import com.griffith.outfitter.model.weather.Clouds
import com.griffith.outfitter.model.weather.Main
import com.griffith.outfitter.model.weather.Sys
import com.griffith.outfitter.model.weather.Weather
import com.griffith.outfitter.model.weather.Wind
import com.google.gson.annotations.SerializedName

data class CustomList(
    @SerializedName("dt") var dt: Int? = null,
    @SerializedName("main") var main: Main? = Main(),
    @SerializedName("weather") var weather: ArrayList<Weather> = arrayListOf(),
    @SerializedName("clouds") var clouds: Clouds? = Clouds(),
    @SerializedName("wind") var wind: Wind? = Wind(),
    @SerializedName("visibility") var visibility: Int? = null,
    @SerializedName("pop") var pop: Double? = null,
    @SerializedName("sys") var sys: Sys? = Sys(),
    @SerializedName("dt_txt") var dtTxt: String? = null
)
