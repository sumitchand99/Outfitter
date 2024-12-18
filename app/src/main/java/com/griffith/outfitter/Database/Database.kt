package com.griffith.outfitter.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.griffith.outfitter.model.weather.WeatherRecord


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "weatherapp.db"
        private const val DATABASE_VERSION = 1
    }


    override fun onCreate(db: SQLiteDatabase) { // Adding databas to store the previous serched weather on device.
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS WEATHER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                location TEXT, 
                temperature REAL, 
                feels_like REAL, 
                sunrise TEXT, 
                sunset TEXT,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS WEATHER")
        onCreate(db)
    }

    fun fetchWeatherData(): List<WeatherRecord> {
        val list = mutableListOf<WeatherRecord>()
        readableDatabase.use { db ->
            val cursor = db.rawQuery("SELECT * FROM WEATHER", null)
            cursor.use {
                while (it.moveToNext()) {
                    val record = WeatherRecord(
                        location = it.getString(it.getColumnIndexOrThrow("location")),
                        temperature = it.getDouble(it.getColumnIndexOrThrow("temperature")),
                        insert_datetime = it.getString(it.getColumnIndexOrThrow("timestamp"))
                    )
                    list.add(record)
                }
            }
        }
        return list
    }

    fun insertIntoDatabase(
        location: String,
        tempCelsius: Double,
        feelsLikeCelsius: Double,
        sunriseTime: String,
        sunsetTime: String
    ) {
        val db = writableDatabase
        val values = android.content.ContentValues().apply {
            put("location", location)
            put("temperature", tempCelsius)
            put("feels_like", feelsLikeCelsius)
            put("sunrise", sunriseTime)
            put("sunset", sunsetTime)
        }
        try {
            val result = db.insert("WEATHER", null, values)
            if (result == -1L) {
                Log.e("DatabaseHelper", "Failed to insert data")
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting data", e)
        } finally {
            db.close()
        }
    }
}


