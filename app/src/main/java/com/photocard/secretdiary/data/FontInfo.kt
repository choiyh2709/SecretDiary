package com.photocard.secretdiary.data

import io.realm.RealmObject

open class FontInfo : RealmObject() {
    var idx: String = "0"
    var possession: Boolean = false
    var adCnt: Int = 0
}