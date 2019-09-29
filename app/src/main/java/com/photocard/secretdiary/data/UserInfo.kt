package com.photocard.secretdiary.data

import io.realm.RealmObject

open class UserInfo : RealmObject() {
    var userProfileImg: String? = null
    var userName: String? = null
    var targetProfileImg: String? = null
    var targetName: String? = null
    var day: String? = null
    var isLock: Boolean = false
    var password: String? = null
    var point: Int = 0
    var font: Int? = 0
}