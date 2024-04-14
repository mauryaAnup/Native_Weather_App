package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.modal.ApiInterface
import com.example.weatherapp.modal.WeatherApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val isConnected = NoInternetUtils.isOnline(this)

        if (isConnected) {
            fetchWeatherData("mumbai")
            searchWeatherDataByCity()
        } else {
            NoInternetUtils.showNoInternet(this)
        }
    }

    private fun searchWeatherDataByCity() {
        val searchText = binding.searchView
        searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchText.windowToken, 0)
                    searchText.setQuery("", false)
                    searchText.clearFocus()
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        LoaderUtils.showDialog(this, false)

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, BuildConfig.OPEN_WEATHER_MAP_APIKEY, "metric")

        response.enqueue(object : Callback<WeatherApi> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherApi>, response: Response<WeatherApi>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {

                    LoaderUtils.hideDialog()

                    val temperature = responseBody.main.temp.toString()
                    val tempMin = responseBody.main.temp_min.toString()
                    val tempMax = responseBody.main.temp_max.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val wind = responseBody.wind.speed.toString()
                    val sunrise = getTime(responseBody.sys.sunrise)
                    val sunset = getTime(responseBody.sys.sunset)
                    val seaLevel = responseBody.main.pressure.toString()
                    val condition = responseBody.weather.firstOrNull()?.main ?: "Unknown"

                    binding.location.text = cityName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    }
                    binding.temperature.text = "$temperature ℃"
                    binding.minTemp.text = "Min: $tempMin ℃"
                    binding.maxTemp.text = "Max: $tempMax ℃"
                    binding.humidity.text = humidity
                    binding.wind.text = wind
                    binding.sunrise.text = sunrise
                    binding.sunset.text = sunset
                    binding.seaLevel.text = seaLevel
                    binding.currentDay.text = getTodayDay()
                    binding.currentDate.text = getDate()
                    binding.weatherType.text = condition
                    binding.conditions.text = condition

                    changeBackground(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApi>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeBackground(condition: String) {
        when(condition) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.mainContainer.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun1)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.mainContainer.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.mainContainer.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.mainContainer.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.mainContainer.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun1)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun getTime(timestamp: Int): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.US)
        val date = Date(timestamp.toLong() * 1000)
        return sdf.format(date)
    }

    fun getTodayDay() : String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun getDate() : String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}