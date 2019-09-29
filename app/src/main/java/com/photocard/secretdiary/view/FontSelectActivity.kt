package com.photocard.secretdiary.view

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.photocard.secretdiary.R
import com.photocard.secretdiary.custom.BaseActivity
import com.photocard.secretdiary.data.FontInfo
import com.photocard.secretdiary.data.UserInfo
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_font_select.*
import kotlin.system.exitProcess

class FontSelectActivity : BaseActivity(), RewardedVideoAdListener {
    private val mRealm = Realm.getDefaultInstance()
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private lateinit var mUserInfo: UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_font_select)
        MobileAds.initialize(this, "ca-app-pub-8610461591818507~1511747866")
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this
        loadRewardedVideoAd()

        setView()
    }

    override fun onPause() {
        super.onPause()
        mRewardedVideoAd.pause(this)
    }

    override fun onResume() {
        super.onResume()
        mRewardedVideoAd.resume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRewardedVideoAd.destroy(this)
    }

    private fun setView(){
        mUserInfo = mRealm.where(UserInfo::class.java).findFirst()!!
        val bmFont = mRealm.where(FontInfo::class.java).equalTo("idx", "1").findFirst()
        var gfFont = mRealm.where(FontInfo::class.java).equalTo("idx", "2").findFirst()

        if (gfFont == null){
            mRealm.executeTransaction {
                gfFont = mRealm.createObject(FontInfo::class.java)
                gfFont?.idx = "2"
                gfFont?.possession = false
                gfFont?.adCnt = 0
            }
        }

        tv_ad_cnt.text = "${gfFont?.adCnt}/3"

        tv_nanum.setOnClickListener {
            mRealm.executeTransaction {
                changeFont("0")
            }
        }

        tv_bm.setOnClickListener {

            mRealm.executeTransaction {
                if (bmFont == null) {
                    if (mUserInfo?.point!! >= 15) {
                        mUserInfo.point = mUserInfo.point - 15
                        val font = mRealm.createObject(FontInfo::class.java)
                        font.idx = "1"
                        font.possession = true
                        font.adCnt = 0
                        changeFont("1")
                    } else {
                        Toast.makeText(this, "마음이 부족합니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    changeFont("1")
                }
            }
        }

        tv_gf.setOnClickListener {
            mRealm.executeTransaction {
                if (gfFont?.possession!!){
                    changeFont("2")
                }else{
                    if (gfFont?.adCnt!! < 3){
                        if (mRewardedVideoAd.isLoaded) {
                            mRewardedVideoAd.show()
                        }else{
                            Toast.makeText(this, "광고를 불러오는 중입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun changeFont(idx: String){
        mUserInfo.font = idx.toInt()
        Toast.makeText(this, "글꼴이 변경되었습니다.", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ exitProcess(0) }, 100)
    }

    private fun loadRewardedVideoAd() {
        val testId = "ca-app-pub-3940256099942544/5224354917"
        val unitId = "ca-app-pub-8610461591818507/5791950791"
        mRewardedVideoAd.loadAd(unitId,
            AdRequest.Builder().build())
    }

    override fun onRewardedVideoAdClosed() {
        loadRewardedVideoAd()
    }

    override fun onRewardedVideoAdLeftApplication() {
    }

    override fun onRewardedVideoAdLoaded() {
    }

    override fun onRewardedVideoAdOpened() {
    }

    override fun onRewardedVideoCompleted() {
    }

    override fun onRewarded(p0: RewardItem?) {
        var gfFont = mRealm.where(FontInfo::class.java).equalTo("idx", "2").findFirst()

        mRealm.executeTransaction {
            if (gfFont == null) {
                gfFont = mRealm.createObject(FontInfo::class.java)
                gfFont?.idx = "2"
                gfFont?.possession = false
                gfFont?.adCnt = 0
            } else {
                gfFont?.adCnt = gfFont?.adCnt?.plus(1)!!
                if (gfFont?.adCnt == 3){
                    gfFont?.possession = true
                    changeFont("2")
                }
            }
        }

        tv_ad_cnt.text = "${gfFont?.adCnt}/3"
    }

    override fun onRewardedVideoStarted() {
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
    }
}
