package com.photocard.secretdiary.custom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.photocard.secretdiary.R
import com.photocard.secretdiary.data.UserInfo
import io.realm.Realm

open class BaseActivity: AppCompatActivity() {
    private val mRealm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userInfo = mRealm.where(UserInfo::class.java).findFirst()

        when(userInfo?.font){
            0 -> setTheme(R.style.AppThemeNaunmGothic)
            1 -> setTheme(R.style.AppThemeBmJua)
            2 -> setTheme(R.style.AppThemeGamjaFlower)
        }

        mRealm.addChangeListener {
            when(userInfo?.font){
                0 -> setTheme(R.style.AppThemeNaunmGothic)
                1 -> setTheme(R.style.AppThemeBmJua)
                2 -> setTheme(R.style.AppThemeGamjaFlower)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }
}