package com.photocard.secretdiary.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.photocard.secretdiary.R
import com.photocard.secretdiary.data.UserInfo
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_lock.*

class LockActivity : AppCompatActivity() {
    private var mPasswordPosition = 0
    private var mPassword: String = ""
    private var mComparePassword: String = ""

    private val mImgList = ArrayList<ImageView>()
    private val mRealm = Realm.getDefaultInstance()
    private var mUserInfo: UserInfo? = null
    private var mAction = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        mUserInfo = mRealm.where(UserInfo::class.java).findFirst()
        mAction = intent.getIntExtra("action", 0)

        setView()

        when(mAction){
            //잠금화면
            0 -> {
                tv_lock_title.text = "비 밀 번 호"
            }
            //잠금화면 설정 (비밀번호 없을 때)
            1 -> {
                tv_lock_title.text = "새로운 비밀번호 네자리를 입력해주세요"
            }
            //비밀번호 변경
            2 -> {
                tv_lock_title.text = "기존 비밀번호 네자리를 입력해주세요"
            }
        }
    }

    private fun setView(){
        mImgList.add(iv_num1)
        mImgList.add(iv_num2)
        mImgList.add(iv_num3)
        mImgList.add(iv_num4)

        tv_num0.setOnClickListener { setPassword("0") }
        tv_num1.setOnClickListener { setPassword("1") }
        tv_num2.setOnClickListener { setPassword("2") }
        tv_num3.setOnClickListener { setPassword("3") }
        tv_num4.setOnClickListener { setPassword("4") }
        tv_num5.setOnClickListener { setPassword("5") }
        tv_num6.setOnClickListener { setPassword("6") }
        tv_num7.setOnClickListener { setPassword("7") }
        tv_num8.setOnClickListener { setPassword("8") }
        tv_num9.setOnClickListener { setPassword("9") }
        rv_delete.setOnClickListener { deletePassword() }

    }

    private fun setPassword(num: String){
        if (mPassword.length < 4){
            mPassword += num
        }else{
            mComparePassword += num
        }

        if (mPasswordPosition < 4) {
            mImgList[mPasswordPosition].visibility = View.VISIBLE
            mPasswordPosition ++

            if (mPasswordPosition == 4){
                when (mAction) {
                    0 -> //잠금해제
                        comparePassword()
                    1 -> //비밀번호 확인
                        confirmPassword()
                    2 -> //비밀번호 변경
                        newPassword()

                }
            }
        }
    }

    private fun deletePassword(){
        if (mPasswordPosition > 0){
            mPasswordPosition --
            mImgList[mPasswordPosition].visibility = View.INVISIBLE

            if (mComparePassword == ""){
                mPassword = mPassword.substring(0, mPassword.length.minus(1))
            }else {
                mComparePassword = mComparePassword.substring(0, mComparePassword.length.minus(1))
            }
        }
    }

    private fun clearPassword(){
        for (i in mImgList) {
            i.visibility = View.INVISIBLE
        }
        mPassword = ""
        mPasswordPosition = 0
    }

    private fun comparePassword(){
        if (mUserInfo?.password == mPassword){
            startActivity(Intent(this@LockActivity, MainActivity::class.java))
            overridePendingTransition(0, (R.anim.fadeout))
            finish()
        }else{
            Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
            clearPassword()
        }
    }

    private fun newPassword(){
        if (mUserInfo?.password == mPassword){
            tv_lock_title.text = "새로운 비밀번호 네자리를 입력해주세요"
            mPassword = ""
            mPasswordPosition = 0
            mAction = 1

            for (i in mImgList) {
                i.visibility = View.INVISIBLE
            }
        }else{
            Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
            clearPassword()
        }
    }

    private fun confirmPassword(){
        if (mComparePassword == "") {
            tv_lock_title.text = "비밀번호를 네자리를 확인해주세요"
            mPasswordPosition = 0

            for (i in mImgList) {
                i.visibility = View.INVISIBLE
            }
        }else{
            if (mPassword == mComparePassword){
                mRealm.executeTransaction {
                    mUserInfo?.password = mPassword
                    Toast.makeText(this, "비밀번호가 설정되었습니다.", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }else{
                Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }
}
