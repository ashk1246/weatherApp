package com.kotlin.weather.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.example.Weather
import com.kotlin.weather.repository.db.weather.WeatherDao
import com.kotlin.weather.repository.model.weather.HumidityModel

/**
 * Created by Ashok
 */

/**
 * App Database
 * Define all entities and access doa's here/ Each entity is a table.
 */
@Database(entities = [Weather::class, HumidityModel::class], version = 8, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
}
