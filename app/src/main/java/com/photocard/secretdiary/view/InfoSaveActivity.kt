package com.photocard.secretdiary.view

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.photocard.secretdiary.R
import kotlinx.android.synthetic.main.activity_info_save.*

class InfoSaveActivity : AppCompatActivity() {
    private val mAdapter = SaveInfoViewPagerAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_save)

        setView()
    }

    private fun setView(){
        vp_save_info.adapter = mAdapter
        rl_next_btn.setOnClickListener {
            if(vp_save_info.currentItem == 0) {
                tv_save_num1.setTextColor(ContextCompat.getColor(this, R.color.grey))
                tv_save_num1.setBackgroundResource(R.drawable.oval_border_gray)
                tv_save_num2.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tv_save_num2.setBackgroundResource(R.drawable.oval_border_blue)

                vp_save_info.currentItem = 1
            }else{
                mAdapter.saveUserInfo()
                startActivity(Intent(this@InfoSaveActivity, MainActivity::class.java))
                overridePendingTransition(0, (R.anim.fadeout))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if(vp_save_info.currentItem == 0) {
            super.onBackPressed()
        }else{
            tv_save_num2.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_save_num2.setBackgroundResource(R.drawable.oval_border_gray)
            tv_save_num1.setTextColor(ContextCompat.getColor(this, R.color.blue))
            tv_save_num1.setBackgroundResource(R.drawable.oval_border_blue)

            vp_save_info.currentItem = 0
        }
    }
}

