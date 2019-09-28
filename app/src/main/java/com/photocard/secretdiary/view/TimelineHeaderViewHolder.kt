package com.photocard.secretdiary.view

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.photocard.secretdiary.R
import com.photocard.secretdiary.custom.CustomViewHolder
import com.photocard.secretdiary.data.UserInfo
import com.photocard.secretdiary.data.WriteInfo
import io.realm.Realm
import io.realm.RealmResults
import jp.wasabeef.glide.transformations.CropCircleTransformation
import java.util.*

class TimelineHeaderViewHolder(view: View, private val onTimelineHeaderListener: OnTimelineHeaderListener): CustomViewHolder<RealmResults<WriteInfo>>(view) {
    private val mUserProfile: ImageView = view.findViewById(R.id.iv_user_profile)
    private val mTargetProfile: ImageView = view.findViewById(R.id.iv_target_profile)
    private val mDay: TextView = view.findViewById(R.id.tv_user_day)
    private val mDayLayout: LinearLayout = view.findViewById(R.id.ll_user_day)
    private val mContext = view.context
    private val mRealm = Realm.getDefaultInstance()

    interface OnTimelineHeaderListener{
        fun onClickUserProfile()
        fun onClickTargetProfile()
        fun onClickDate()
    }

    override fun bind(item: RealmResults<WriteInfo>, position: Int) {
        val userInfo = mRealm.where(UserInfo::class.java).findFirst() ?: return

        if (userInfo.userProfileImg != null) {
            Glide.with(mContext).load(userInfo.userProfileImg).centerCrop()
                .apply(RequestOptions.bitmapTransform(CropCircleTransformation())).into(mUserProfile)
        }

        if(userInfo.targetProfileImg != null) {
            Glide.with(mContext).load(userInfo.targetProfileImg).centerCrop()
                .apply(RequestOptions.bitmapTransform(CropCircleTransformation())).into(mTargetProfile)
        }

        val day = 60 * 60 * 24 * 1000
        val userDay = userInfo.day
        val userCal = GregorianCalendar(userDay!!.substring(0,4).toInt(), userDay.substring(4,6).toInt() - 1, userDay.substring(6,8).toInt())
        val today = Calendar.getInstance()

        val dday = ((today.timeInMillis - userCal.timeInMillis) / day)
        mDay.text = "D+$dday"

        mUserProfile.setOnClickListener {
            onTimelineHeaderListener.onClickUserProfile()
        }

        mTargetProfile.setOnClickListener {
            onTimelineHeaderListener.onClickTargetProfile()
        }

        mDayLayout.setOnClickListener {
            onTimelineHeaderListener.onClickDate()
        }
    }
}