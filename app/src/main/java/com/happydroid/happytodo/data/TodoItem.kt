package com.happydroid.happytodo.data

import android.util.Log
import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class TodoItem(
    val id: String,
    val text: String,
    val priority: Priority,
    val deadline: Date?,
    val isDone: Boolean,
    val createdDate: Date,
    val modifiedDate: Date?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        Priority.valueOf(parcel.readString()!!),
        parcel.readSerializable() as Date?,
        parcel.readByte() != 0.toByte(),
        parcel.readSerializable() as Date,
        parcel.readSerializable() as Date?
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(text)
        parcel.writeString(priority.name)
        parcel.writeSerializable(deadline)
        parcel.writeByte(if (isDone) 1 else 0)
        parcel.writeSerializable(createdDate)
        parcel.writeSerializable(modifiedDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TodoItem> {
        override fun createFromParcel(parcel: Parcel): TodoItem {
            return TodoItem(parcel)
        }

        override fun newArray(size: Int): Array<TodoItem?> {
            return arrayOfNulls(size)
        }
    }

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