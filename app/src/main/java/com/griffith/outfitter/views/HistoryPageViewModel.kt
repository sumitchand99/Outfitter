package com.griffith.outfitter.views

import androidx.lifecycle.ViewModel
import com.griffith.outfitter.Database.DatabaseHelper

class HistoryPageViewModel(private val databaseHelper: DatabaseHelper) : ViewModel() {
    fun fetchData() = databaseHelper.fetchWeatherData()
}
