package com.example.weatherapp

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var cityNameTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var weatherTextView: TextView
    private lateinit var maxTempTextView: TextView
    private lateinit var minTempTextView: TextView
    private lateinit var dayTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var conditionTextView: TextView
    private lateinit var sunriseTextView: TextView
    private lateinit var sunsetTextView: TextView
    private lateinit var seaTextView: TextView
    private lateinit var weatherAnimation: LottieAnimationView
    private lateinit var rootLayout: ScrollView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        rootLayout = findViewById(R.id.main)
        searchView = findViewById(R.id.searchView)
        cityNameTextView = findViewById(R.id.cityname)
        temperatureTextView = findViewById(R.id.temperature)
        weatherTextView = findViewById(R.id.weather)
        maxTempTextView = findViewById(R.id.maxtemp)
        minTempTextView = findViewById(R.id.mintemp)
        dayTextView = findViewById(R.id.day)
        dateTextView = findViewById(R.id.date)
        humidityTextView = findViewById(R.id.humidity)
        windSpeedTextView = findViewById(R.id.windspeed)
        conditionTextView = findViewById(R.id.condition)
        sunriseTextView = findViewById(R.id.sunrise)
        sunsetTextView = findViewById(R.id.sunset)
        seaTextView = findViewById(R.id.sea)
        weatherAnimation = findViewById(R.id.weatherAnimation)


        fetchData("Rawalpindi")
        setCurrentDayAndDate()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { city ->
                    fetchData(city)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


    }

    private fun fetchData(city: String) {
        val apiKey = "ab907743b5bfb1d11ba8ffdf482f70e5"
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"

        val request = JsonObjectRequest(
            com.android.volley.Request.Method.GET,
            url,
            null,
            { response: JSONObject ->
                try {
                    val main = response.getJSONObject("main")
                    val weatherArray = response.getJSONArray("weather")
                    val weather = weatherArray.getJSONObject(0)
                    val wind = response.getJSONObject("wind")
                    val sys = response.getJSONObject("sys")

                    val cityName = response.getString("name")
                    val temperature = main.getDouble("temp")
                    val weatherDescription = weather.getString("description")
                    val maxTemp = main.getDouble("temp_max")
                    val minTemp = main.getDouble("temp_min")
                    val humidity = main.getInt("humidity")
                    val windSpeed = wind.getDouble("speed")
                    val sunrise = sys.getLong("sunrise")
                    val sunset = sys.getLong("sunset")
                    val pressure = main.getInt("pressure")

                    cityNameTextView.text = cityName
                    temperatureTextView.text = "${temperature.roundToInt()} °C"
                    weatherTextView.text = weatherDescription.replaceFirstChar { it.uppercase() }
                    maxTempTextView.text = "Max: ${maxTemp.roundToInt()} °C"
                    minTempTextView.text = "Min: ${minTemp.roundToInt()} °C"
                    humidityTextView.text = "$humidity%"
                    windSpeedTextView.text = "${windSpeed.roundToInt()} m/s"
                    conditionTextView.text = weatherDescription.replaceFirstChar { it.uppercase() }
                    sunriseTextView.text = formatTime(sunrise)
                    sunsetTextView.text = formatTime(sunset)
                    seaTextView.text = "${pressure} hPa"


                    val weatherMain = weather.getString("main")
                    setWeatherAnimation(weatherMain)
                    setBackground(weatherMain)


                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing weather data", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Error fetching weather data", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun setCurrentDayAndDate() {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        dayTextView.text = dayFormat.format(calendar.time)
        dateTextView.text = dateFormat.format(calendar.time)
    }

    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(date)
    }

    private fun setWeatherAnimation(weatherMain: String) {
        when (weatherMain) {
            "Clear" -> weatherAnimation.setAnimation(R.raw.sunny)
            "Clouds" -> weatherAnimation.setAnimation(R.raw.cloudsanimation)
            "Rain" -> weatherAnimation.setAnimation(R.raw.rain)
            "Snow" -> weatherAnimation.setAnimation(R.raw.snow)
            "Thunderstorm" -> weatherAnimation.setAnimation(R.raw.thunderstorm)
            else -> weatherAnimation.setAnimation(R.raw.mist)
        }
        weatherAnimation.playAnimation()
    }

    private fun setBackground(weatherMain: String) {
        when (weatherMain) {
            "Clear" -> rootLayout.setBackgroundResource(R.drawable.sunny_background)
            "Clouds" -> rootLayout.setBackgroundResource(R.drawable.colud_background)
            "Rain" -> rootLayout.setBackgroundResource(R.drawable.rain_background)
            "Snow" -> rootLayout.setBackgroundResource(R.drawable.snow_background)
            "Thunderstorm" -> rootLayout.setBackgroundResource(R.drawable.thunderstorm_background)
            else -> rootLayout.setBackgroundResource(R.drawable.mist)
        }
    }
}