package com.griffith.outfitter.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.griffith.outfitter.constant.Const.Companion.LOADING
import com.griffith.outfitter.constant.Const.Companion.NA
import com.griffith.outfitter.model.weather.WeatherResult
import com.griffith.outfitter.utils.UTILS.Companion.buildIcon
import com.griffith.outfitter.utils.UTILS.Companion.timestampToHumanDate
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIconType
import com.guru.fontawesomecomposelib.FaIcons

@Composable
fun WeatherSection(weatherResponse: WeatherResult) {
    //Title
    var title = ""
    if (!weatherResponse.name.isNullOrEmpty()) {
        weatherResponse.name?.let {
            title = it
        }
    } else {
        weatherResponse.coord?.let {
            title = "${it.lat}/${it.lon}"
        }
    }
    //SubTitle
    var subTitle = ""
    val dateVal = (weatherResponse.dt ?: 0)

    subTitle = if(dateVal == 0) LOADING
    else timestampToHumanDate(dateVal.toLong(), "yyyy-MM-dd")

    //Icon Section
    var icon = ""
    var description = ""
    weatherResponse.weather.let {
        if(it!!.size > 0) {
            description = if (it[0].description == null) LOADING else
                it[0].description!!
            icon  = if (it[0].icon == null) LOADING else it[0].icon!!
        }
    }
    //Temperature
    var temp = ""
    weatherResponse.main?.let{
        temp = "${it.temp} °C"
    }
    //Wind
    var wind = ""
    weatherResponse.wind.let {
        wind = if (it == null) LOADING else "${it.speed}"
    }
    //Cloud
    var clouds = ""
    weatherResponse.clouds.let {
        clouds = if (it == null) LOADING else "${it.all}"
    }
    //Snow
    var snow = ""
    weatherResponse.snow.let {
        snow = if (it!!.d1h == null) NA else "${it.d1h}"
    }

    TitleSection(text = title, subText = subTitle, fontSize = 40.sp)
    WeatherImage(icon = icon)
    WeatherTitleSection(text = temp, subText = description, fontSize = 50.sp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        WeatherInfo(icon = FaIcons.Wind, text = wind)
        WeatherInfo(icon = FaIcons.Cloud, text = clouds)
        WeatherInfo(icon = FaIcons.Snowflake, text = snow)
    }

}

@Composable
fun WeatherInfo(icon: FaIconType.SolidIcon, text: String) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FaIcon(
            faIcon = icon,
            size = 48.dp,
            tint = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


@Composable
fun WeatherImage(icon: String) {
    AsyncImage(model =buildIcon(icon), contentDescription = icon,
        modifier = Modifier
            .width(200.dp)
            .height(200.dp),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
fun WeatherTitleSection(text: String, subText: String, fontSize: TextUnit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, fontSize = fontSize, color = Color.White, fontWeight = FontWeight.Bold)
        Text(subText, fontSize = 14.sp, color = Color.White)
    }
}
@Composable
fun TitleSection(text: String, subText: String, fontSize: TextUnit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = subText,
            fontSize = 14.sp,
            color = Color.White,
            maxLines = 1
        )
    }
}