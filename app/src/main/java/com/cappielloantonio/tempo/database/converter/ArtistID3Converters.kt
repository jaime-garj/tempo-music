package com.cappielloantonio.tempo.database.converter

import androidx.room.TypeConverter
import com.cappielloantonio.tempo.subsonic.models.ArtistID3
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ArtistID3Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromArtistList(value: List<ArtistID3>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toArtistList(value: String?): List<ArtistID3>? {
        val type = object : TypeToken<List<ArtistID3>>() {}.type
        return gson.fromJson(value, type)
    }
}