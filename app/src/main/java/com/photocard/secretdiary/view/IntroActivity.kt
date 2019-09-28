package com.photocard.secretdiary.view

import android.content.Intent
import android.os.Bundle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.photocard.secretdiary.R
import com.photocard.secretdiary.custom.BaseActivity
import com.photocard.secretdiary.data.UserInfo
import io.realm.Realm

class IntroActivity : BaseActivity() {
    private val mRealm = Realm.getDefaultInstance()
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-8610461591818507/6847835021"
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                mInterstitialAd.show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                nextActivity()
            }

            override fun onAdClosed() {
                nextActivity()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    private fun nextActivity(){
        val userInfo = mRealm.where(UserInfo::class.java).findFirst()

        if (userInfo != null){
            if (userInfo.isLock){
                startActivity(Intent(this@IntroActivity, LockActivity::class.java))
            }else {
                startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            }
        }else {
            startActivity(Intent(this@IntroActivity, InfoSaveActivity::class.java))
        }
        overridePendingTransition(0, (R.anim.fadeout))
        finish()
    }
}
