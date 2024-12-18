package com.griffith.outfitter.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.griffith.outfitter.Database.DatabaseHelper

class HistoryPageViewModelFactory(
    private val databaseHelper: DatabaseHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryPageViewModel(databaseHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
