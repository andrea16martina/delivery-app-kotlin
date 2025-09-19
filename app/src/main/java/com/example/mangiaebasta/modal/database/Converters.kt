package com.example.mangiaebasta.modal.database

import androidx.room.TypeConverter
import com.example.mangiaebasta.modal.Location
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromLocation(location: Location?): String? {
        return Gson().toJson(location)
    }

    @TypeConverter
    fun toLocation(locationString: String?): Location? {
        return Gson().fromJson(locationString, Location::class.java)
    }
}