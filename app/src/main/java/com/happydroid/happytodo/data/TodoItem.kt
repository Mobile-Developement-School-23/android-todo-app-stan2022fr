package com.happydroid.happytodo.data

import android.util.Log
import java.util.Date

data class TodoItem(
    val id: String,
    val text: String,
    val priority: Priority,
    val deadline: Date?,
    val isDone: Boolean,
    val createdDate: Date,
    val modifiedDate: Date?
) {
    enum class Priority {
        NORMAL, LOW,  HIGH;

        companion object {
            fun fromString(value: String): Priority {
                Log.i("happyy", value)
                return when (value) {

                    "@string/priority_none" -> NORMAL
                    "@string/priority_low" -> LOW
                    "@string/priority_high" -> HIGH
                    else -> NORMAL
                }
            }
        }
    }
}