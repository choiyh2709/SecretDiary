package com.photocard.secretdiary.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.photocard.secretdiary.data.UserInfo
import io.realm.Realm

class SaveInfoViewPagerAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager) {
    private val mSaveUserFragment = SaveUserFragment()
    private val mSaveTargetFragment = SaveTargetFragment()
    private val mRealm = Realm.getDefaultInstance()

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> mSaveUserFragment
            else -> mSaveTargetFragment
        }
    }

    fun saveUserInfo(){
        mRealm.beginTransaction()

        val userInfo = mRealm.createObject(UserInfo::class.java)
        userInfo.userName = mSaveUserFragment.getUserName()
        userInfo.userProfileImg = mSaveUserFragment.getUserProfile()
        userInfo.targetName = mSaveTargetFragment.getTargetName()
        userInfo.targetProfileImg = mSaveTargetFragment.getTargetProfile()
        userInfo.day = mSaveTargetFragment.getDay()

        mRealm.commitTransaction()
    }

    override fun getCount(): Int {
        return 2
    }
}