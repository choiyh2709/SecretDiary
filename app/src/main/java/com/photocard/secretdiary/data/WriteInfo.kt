package com.photocard.secretdiary.data

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class WriteInfo() : RealmObject(), Parcelable {
    @PrimaryKey
    var idx: Int? = 0
    var title: String? = null
    var date: String? = null
    var tag: String? = null
    var weather: Int? = 0
    var content: String? = null
    var insDate: String? = null

    constructor(parcel: Parcel) : this() {
        idx = parcel.readValue(Int::class.java.classLoader) as? Int
        title = parcel.readString()
        date = parcel.readString()
        tag = parcel.readString()
        weather = parcel.readValue(Int::class.java.classLoader) as? Int
        content = parcel.readString()
        insDate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(idx)
        parcel.writeString(title)
        parcel.writeString(date)
        parcel.writeString(tag)
        parcel.writeValue(weather)
        parcel.writeString(content)
        parcel.writeString(insDate)
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