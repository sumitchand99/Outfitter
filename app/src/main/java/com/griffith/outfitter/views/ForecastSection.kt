package com.griffith.outfitter.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import coil.compose.AsyncImage
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.griffith.outfitter.constant.Const.Companion.NA
import com.griffith.outfitter.model.forecast.ForecastResult
import com.griffith.outfitter.utils.UTILS.Companion.buildIcon
import com.griffith.outfitter.utils.UTILS.Companion.timestampToHumanDate
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ForecastTile(temp: String, image: String, time: String) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF42A5F5),
            Color(0xFF1E88E5)
        )
    )

    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .height(180.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(brush = gradientBrush)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = temp.ifEmpty { "N/A" },
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = time.ifEmpty { "N/A" },
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            AsyncImage(
                model = image,
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(75.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ForecastSection(forecastResponse: ForecastResult) {
    return Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        forecastResponse.list.let {
            listForecast ->
            if(listForecast.size > 0) {
                LazyRow(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(listForecast) { currentItem ->
                        currentItem?.let { item ->
                            var temp = ""
                            var icon = ""
                            var time = ""
                            item.main.let { main ->
                                temp = if (main == null) NA else "${main.temp} °C"
                            }
                            item.weather.let { weather ->
                                icon = buildIcon(
                                    weather[0].icon!!,
                                    isBigSize = false)
                            }
                            item.dt.let { dateTime ->
                                time = if (dateTime == null) NA
                                else timestampToHumanDate(dateTime.toLong(), "EEE HH:mm")
                            }
                            ForecastTile(temp = temp, image = icon, time = time)
                        }
                    }
                }
            }
        }
    }
}
