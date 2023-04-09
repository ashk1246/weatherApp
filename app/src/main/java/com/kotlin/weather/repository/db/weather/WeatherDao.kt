package com.kotlin.weather.repository.db.weather

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.example.Weather
import com.kotlin.weather.repository.model.weather.HumidityModel

/**
 * Created by Ashok
 */

/**
 * Abstracts access to the news database
 */
@Dao
interface WeatherDao {

    /**
     * Insert Weather into the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(articles: List<Weather>): List<Long>

    /**
     * Get all the Weather from database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHumidity(humidity: HumidityModel)

    @Query("SELECT * FROM weather_table")
    fun getWeatherDatas(): LiveData<List<Weather>>

    @Query("DELETE FROM weather_table")
    abstract fun deleteAllWeather()
}
