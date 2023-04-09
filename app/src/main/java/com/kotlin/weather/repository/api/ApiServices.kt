package com.kotlin.weather.repository.api

import androidx.lifecycle.LiveData
import com.example.example.Search
import com.example.example.WeatherModel
import com.kotlin.weather.repository.api.network.Resource
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * Created by Ashok
 */

interface ApiServices {
    // get weather
    @GET("data/2.5/weather")
    fun getWeatherSource(@QueryMap options: Map<String, String>): LiveData<Resource<WeatherModel>>

    // get lat and lon
    @GET("geo/1.0/direct")
    fun getGeoCode(@QueryMap options: Map<String, String>): LiveData<Resource<List<Search>>>
}
