package com.kotlin.weather.repository.repo.weather

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.example.Search
import com.example.example.Weather
import com.example.example.WeatherModel
import com.kotlin.weather.app.AppExecutors
import com.kotlin.weather.repository.api.ApiServices
import com.kotlin.weather.repository.api.network.NetworkAndDBBoundResource
import com.kotlin.weather.repository.api.network.NetworkResource
import com.kotlin.weather.repository.api.network.Resource
import com.kotlin.weather.repository.db.weather.WeatherDao
import com.kotlin.weather.repository.model.weather.HumidityModel
import com.kotlin.weather.utils.ConnectivityUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Waheed on 04,November,2019
 */

/**
 * Repository abstracts the logic of fetching the data and persisting it for
 * offline. They are the data source as the single source of truth.
 *
 */

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherDao: WeatherDao,
    private val apiServices: ApiServices,
    @ApplicationContext val context: Context,
    private val appExecutors: AppExecutors = AppExecutors()
) {

    /**
     * Fetch the news articles from database if exist else fetch from web
     * and persist them in the database
     */
    fun getWeatherRepo(latKey: String, lanKey: String): LiveData<Resource<List<Weather>?>> {
        val data = HashMap<String, String>()
        data["lat"] = latKey
        data["lon"] = lanKey
        data["appid"] = "f4ea6ba6fa0cdf1fe39fc66f29da0110"

        return object : NetworkAndDBBoundResource<List<Weather>, WeatherModel>(appExecutors) {
            override fun saveCallResult(item: WeatherModel) {
                if (item.weather.isNotEmpty()) {
                    weatherDao.deleteAllWeather()
                    weatherDao.insertWeather(item.weather)
                    val humidityModel = HumidityModel(
                        item.weather.get(0).id,
                        item.weather.get(0).main,
                        item.weather.get(0).description,
                        item.weather.get(0).icon,
                        item.main?.temp,
                        item.main?.feelsLike,
                        item.main?.tempMin,
                        item.main?.tempMax,
                        item.main?.pressure,
                        item.main?.humidity,
                        item.main?.seaLevel,
                        item.main?.grndLevel
                    )
                    weatherDao.insertHumidity(humidityModel)
                }
            }

            override fun shouldFetch(data: List<Weather>?) =
                (ConnectivityUtil.isConnected(context))

            override fun loadFromDb() = weatherDao.getWeatherDatas()

            override fun createCall() =
                apiServices.getWeatherSource(data)
        }.asLiveData()
    }

    fun latestWeatherFromServerOnly(countryShortKey: String):
        LiveData<Resource<List<Search>>> {
        val data = HashMap<String, String>()
        data["q"] = countryShortKey
        data["limit"] = 3.toString()
        data["appid"] = "f4ea6ba6fa0cdf1fe39fc66f29da0110"

        return object : NetworkResource<List<Search>>() {
            override fun createCall(): LiveData<Resource<List<Search>>> {
                return apiServices.getGeoCode(data)
            }
        }.asLiveData()
    }
}
