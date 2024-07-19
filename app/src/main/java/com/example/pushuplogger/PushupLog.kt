package com.example.pushuplogger

import android.os.Parcel
import android.os.Parcelable

data class PushupLog(val count: Int, val date: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(count)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PushupLog> {
        override fun createFromParcel(parcel: Parcel): PushupLog {
            return PushupLog(parcel)
        }

        override fun newArray(size: Int): Array<PushupLog?> {
            return arrayOfNulls(size)
        }
    }
}
