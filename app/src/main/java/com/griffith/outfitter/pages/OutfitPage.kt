import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kotlinjetpackweather.R


import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class OutfitSuggestion(
    val text: String,
    val imageResId: Int
)

@Composable
fun OutfitPage() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var outfitSuggestion by remember {
        mutableStateOf(
            OutfitSuggestion(
                text = "Click on the button for outfit suggestion...",
                imageResId = R.drawable.loading // Placeholder image
            )
        )
    }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Background color
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Display outfit suggestion image
            Image(
                painter = painterResource(id = outfitSuggestion.imageResId),
                contentDescription = "Outfit Image",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display outfit suggestion text
            Text(
                text = outfitSuggestion.text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        fetchLocationAndSuggestOutfit(fusedLocationClient) { suggestion ->
                            outfitSuggestion = suggestion
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0288D1), // Button background color
                    contentColor = Color.White // Button text color
                )
            ) {
                Text("Fetch Outfit Suggestion")
            }
        }
    }
}

@SuppressLint("MissingPermission")
suspend fun fetchLocationAndSuggestOutfit(
    fusedLocationClient: FusedLocationProviderClient,
    onSuggestionFetched: (OutfitSuggestion) -> Unit
) {
    try {
        val location = getLastLocation(fusedLocationClient)
        if (location != null) {
            val weather = fetchWeather(location.latitude, location.longitude)
            val outfit = suggestOutfitBasedOnWeather(weather)
            onSuggestionFetched(outfit)
        } else {
            onSuggestionFetched(
                OutfitSuggestion(
                    text = "Unable to fetch your location. Please enable location services.",
                    imageResId = R.drawable.error // Placeholder error image
                )
            )
        }
    } catch (e: SecurityException) {
        onSuggestionFetched(
            OutfitSuggestion(
                text = "Location permission is not granted. Please allow location access.",
                imageResId = R.drawable.error // Placeholder error image
            )
        )
    } catch (e: Exception) {
        onSuggestionFetched(
            OutfitSuggestion(
                text = "An error occurred: ${e.message}",
                imageResId = R.drawable.error // Placeholder error image
            )
        )
    }
}

@SuppressLint("MissingPermission")
suspend fun getLastLocation(fusedLocationClient: FusedLocationProviderClient): Location? =
    suspendCancellableCoroutine { continuation ->
        val task: Task<Location> = fusedLocationClient.lastLocation
        task.addOnSuccessListener { location ->
            continuation.resume(location)
        }
        task.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        task.addOnCanceledListener {
            continuation.cancel()
        }
    }

suspend fun fetchWeather(lat: Double, lon: Double): JSONObject {
    val apiKey = "991bea2c527b6db79604971ad6764f00" // Replace with your OpenWeatherMap API key
    val apiUrl =
        "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey"

    return withContext(Dispatchers.IO) {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream.bufferedReader().readText()
                JSONObject(inputStream)
            } else {
                throw Exception("Failed to fetch weather data.")
            }
        } finally {
            connection.disconnect()
        }
    }
}

fun suggestOutfitBasedOnWeather(weatherData: JSONObject): OutfitSuggestion {
    val main = weatherData.getJSONObject("main")
    val tempKelvin = main.getDouble("temp")
    val tempCelsius = tempKelvin - 273.15

    return when {
        tempCelsius < 0 -> OutfitSuggestion(
            text = "It's freezing! Wear a heavy jacket, gloves, and a scarf.",
            imageResId = R.drawable.freezing_outfit
        )
        tempCelsius in 0.0..10.0 -> OutfitSuggestion(
            text = "It's quite cold. Wear a coat and warm layers.",
            imageResId = R.drawable.cold_outfit
        )
        tempCelsius in 10.0..20.0 -> OutfitSuggestion(
            text = "It's cool. A light jacket or sweater will do.",
            imageResId = R.drawable.cool_outfit
        )
        tempCelsius in 20.0..30.0 -> OutfitSuggestion(
            text = "The weather is warm. A t-shirt and jeans are perfect.",
            imageResId = R.drawable.warm_outfit
        )
        else -> OutfitSuggestion(
            text = "It's hot! Wear light, breathable clothing like shorts and a tank top.",
            imageResId = R.drawable.hot_outfit
        )
    }
}
