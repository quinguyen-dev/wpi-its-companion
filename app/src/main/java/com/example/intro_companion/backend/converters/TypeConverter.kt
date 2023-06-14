package com.example.intro_companion.backend.converters

import androidx.room.TypeConverter
import java.util.Date

class TypeConverter {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }
}