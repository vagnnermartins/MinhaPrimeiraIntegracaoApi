package com.example.minhaprimeiraapi.database.converters

import androidx.room.TypeConverter
import java.util.Date

class DateConverters {

    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimeStamp(date: Date?): Long? {
        return date?.time
    }
}