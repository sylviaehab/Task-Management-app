package com.example.task_4_sprints.data.db

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String? =
        list?.joinToString(separator = ",")

    @TypeConverter
    fun toStringList(data: String?): List<String> =
        if (data.isNullOrEmpty()) emptyList() else data.split(",")

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }
}