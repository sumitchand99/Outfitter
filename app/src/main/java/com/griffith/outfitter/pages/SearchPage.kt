import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.griffith.outfitter.Database.DatabaseHelper
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(onBackClick: (() -> Unit)? = null) {
    val context = LocalContext.current
    var location by remember { mutableStateOf("") }
    var weatherDetailsList by remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val databaseHelper = remember { DatabaseHelper(context) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search Weather", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0288D1)
                )
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Enter location", color = Color(0xFF0288D1)) },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF0288D1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF0288D1),
                            unfocusedBorderColor = Color(0xFF0288D1),
                            cursorColor = Color(0xFF0288D1),
                            focusedLabelColor = Color(0xFF0288D1),
                            unfocusedLabelColor = Color(0xFF0288D1)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    val weatherResponse = fetchWeather(context, location, databaseHelper)
                                    weatherDetailsList = weatherDetailsList.toMutableList().apply {
                                        add(location to weatherResponse)
                                    }
                                    location = ""
                                } catch (e: Exception) {
                                    errorMessage = "Failed to fetch weather: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0288D1),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Get Weather")
                    }

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 16.dp),
                            color = Color(0xFF0288D1)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    weatherDetailsList.forEachIndexed { index, pair ->
                        WeatherCard(
                            location = pair.first,
                            details = pair.second,
                            onClose = {
                                weatherDetailsList = weatherDetailsList.toMutableList().apply {
                                    removeAt(index)
                                }
                            }
                        )
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color(0xFF590404),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun WeatherCard(location: String, details: String, onClose: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color(0xFF0288D1))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Location: $location",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF0288D1)
                )
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF0288D1),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF0288D1)
                )
            }
        }
    }
}

suspend fun fetchWeather(context: android.content.Context, location: String, databaseHelper: DatabaseHelper): String {
    val apiKey = "991bea2c527b6db79604971ad6764f00"
    val apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=$location&appid=$apiKey"

    return withContext(Dispatchers.IO) {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonResponse = JSONObject(response)

                val main = jsonResponse.getJSONObject("main")
                val tempCelsius = main.getDouble("temp") - 273.15
                val feelsLikeCelsius = main.getDouble("feels_like") - 273.15
                val humidity = main.getInt("humidity")

                val sys = jsonResponse.getJSONObject("sys")
                val sunriseTime = convertUnixToTime(sys.getDouble("sunrise"))
                val sunsetTime = convertUnixToTime(sys.getDouble("sunset"))
                val country = sys.getString("country")

                // Insert into database
                databaseHelper.insertIntoDatabase(
                    location = location,
                    tempCelsius = tempCelsius,
                    feelsLikeCelsius = feelsLikeCelsius,
                    sunriseTime = sunriseTime,
                    sunsetTime = sunsetTime
                )

                """
                    Temperature: ${String.format("%.2f", tempCelsius)} °C
                    Feels Like: ${String.format("%.2f", feelsLikeCelsius)} °C
                    Humidity: $humidity%
                    Country: $country
                    Sunrise: $sunriseTime
                    Sunset: $sunsetTime
                """.trimIndent()
            } else {
                throw Exception("Error: ${connection.responseMessage}")
            }
        } finally {
            connection.disconnect()
        }
    }
}

fun convertUnixToTime(unixTimestamp: Double): String {
    val instant = Instant.ofEpochSecond(unixTimestamp.toLong())
    val formatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}
