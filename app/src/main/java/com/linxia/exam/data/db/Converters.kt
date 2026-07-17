package com.linxia.exam.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return value?.let { json.decodeFromString<List<String>>(it) } ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return list?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun fromLongList(value: String?): List<Long> {
        return value?.let { json.decodeFromString<List<Long>>(it) } ?: emptyList()
    }

    @TypeConverter
    fun toLongList(list: List<Long>?): String {
        return list?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun fromMap(value: String?): Map<String, String> {
        return value?.let { json.decodeFromString<Map<String, String>>(it) } ?: emptyMap()
    }

    @TypeConverter
    fun toMap(map: Map<String, String>?): String {
        return map?.let { json.encodeToString(it) } ?: "{}"
    }
}