package com.happydroid.happytodo.data.local

import androidx.room.TypeConverter
import java.util.Date

/**
 * This class provides utility methods for date conversion.
 */
class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}
