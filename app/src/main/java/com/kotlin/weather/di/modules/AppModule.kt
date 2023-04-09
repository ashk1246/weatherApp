package com.kotlin.weather.di.modules

import android.content.Context
import androidx.room.Room
import com.kotlin.weather.repository.api.ApiServices
import com.kotlin.weather.repository.api.network.LiveDataCallAdapterFactoryForRetrofit
import com.kotlin.weather.repository.db.AppDatabase
import com.kotlin.weather.repository.db.weather.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides ApiServices client for Retrofit
     */
    @Singleton
    @Provides
    fun provideNewsService(): ApiServices {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactoryForRetrofit())
            .build()
            .create(ApiServices::class.java)
    }

    /**
     * Provides app AppDatabase
     */
    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "news-db")
            .fallbackToDestructiveMigration().build()

    /**
     * Provides CountriesDao an object to access Countries table from Database
     */

    @Singleton
    @Provides
    fun provideWeatherDao(db: AppDatabase): WeatherDao = db.weatherDao()
}
