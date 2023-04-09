package com.kotlin.weather.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.example.Weather
import com.kotlin.weather.repository.api.network.Resource
import com.kotlin.weather.repository.repo.weather.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Ashok
 */

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) : ViewModel() {

    /**
     * Loading news articles from internet and database
     */

    fun getWeatherCache(latKey: String, lanKey: String) = latestweather(latKey, lanKey)

    private fun latestweather(latKey: String, lanKey: String): LiveData<Resource<List<Weather>?>> =
        weatherRepository.getWeatherRepo(latKey, lanKey)

    private fun weatherFromOnlyServer(countryKey: String) =
        weatherRepository.latestWeatherFromServerOnly(countryKey)

    fun searchFromAPI(countryKey: String) = weatherFromOnlyServer(countryKey)
}
