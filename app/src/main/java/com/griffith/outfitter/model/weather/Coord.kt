package com.griffith.outfitter.model.weather

import com.google.gson.annotations.SerializedName

class Coord {
    @SerializedName("lon") var lon: Double? = null
    @SerializedName("lat") var lat: Double? = null
}