package com.photocard.secretdiary.data

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class WriteInfo() : RealmObject(), Parcelable {
    @PrimaryKey
    var idx: Int? = 0
    var title: String? = null
    var date: String? = null
    var tag: String? = null
    var weather: Int? = 0
    var content: String? = null
    var insDate: String? = null
    var date2: Date? = null

    constructor(parcel: Parcel) : this() {
        idx = parcel.readValue(Int::class.java.classLoader) as? Int
        title = parcel.readString()
        date = parcel.readString()
        tag = parcel.readString()
        weather = parcel.readValue(Int::class.java.classLoader) as? Int
        content = parcel.readString()
        insDate = parcel.readString()
        date2 = parcel.readValue(Date::class.java.classLoader) as? Date
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(idx)
        parcel.writeString(title)
        parcel.writeString(date)
        parcel.writeString(tag)
        parcel.writeValue(weather)
        parcel.writeString(content)
        parcel.writeString(insDate)
        parcel.writeValue(date2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WriteInfo> {
        override fun createFromParcel(parcel: Parcel): WriteInfo {
            return WriteInfo(parcel)
        }

        override fun newArray(size: Int): Array<WriteInfo?> {
            return arrayOfNulls(size)
        }
    }

}