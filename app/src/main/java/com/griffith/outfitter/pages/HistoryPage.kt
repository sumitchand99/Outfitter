package com.griffith.outfitter.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.griffith.outfitter.Database.DatabaseHelper
import com.griffith.outfitter.views.HistoryPageViewModel
import com.griffith.outfitter.views.HistoryPageViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage() {
    val context = LocalContext.current
    val databaseHelper = remember { DatabaseHelper(context) }
    val viewModel: HistoryPageViewModel = viewModel(
        factory = HistoryPageViewModelFactory(databaseHelper)
    )

    val weatherData = remember { viewModel.fetchData() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Weather History", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0288D1)
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (weatherData.isEmpty()) {
                item {
                    Text(
                        "No weather data available.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                items(weatherData) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        elevation = cardElevation(defaultElevation = 4.dp),
                        colors = cardColors(containerColor = Color(0xFF81D4FA))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = record.location,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                            Text(
                                text = "${"%.2f".format(record.temperature)} °C",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                            Text(
                                text = formatDate(record.insert_datetime),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateTime = LocalDateTime.parse(dateString, inputFormatter)
        dateTime.format(outputFormatter)
    } catch (e: Exception) {
        dateString
    }
}