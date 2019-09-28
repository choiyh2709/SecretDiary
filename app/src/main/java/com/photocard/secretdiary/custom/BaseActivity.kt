package com.photocard.secretdiary.custom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.photocard.secretdiary.R

open class BaseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppThemeBmJua)
    }
}