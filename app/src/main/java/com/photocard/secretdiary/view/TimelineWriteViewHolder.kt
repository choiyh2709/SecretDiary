package com.photocard.secretdiary.view

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.photocard.secretdiary.R
import com.photocard.secretdiary.custom.CustomViewHolder
import com.photocard.secretdiary.data.WriteInfo
import io.realm.RealmResults

class TimelineWriteViewHolder(view: View): CustomViewHolder<RealmResults<WriteInfo>>(view) {
    private val mItemView: LinearLayout = view.findViewById(R.id.ll_write)
    private val mTag: TextView = view.findViewById(R.id.tv_write_tag)
    private val mTitle: TextView = view.findViewById(R.id.tv_write_title)
    private val mLastView: View = view.findViewById(R.id.last_view)
    private val mDate: TextView = view.findViewById(R.id.tv_write_date)
    private val mWeather: ImageView = view.findViewById(R.id.iv_weather)

    override fun bind(item: RealmResults<WriteInfo>, position: Int) {
        val writeInfo = item[position] ?: return

        if (position == item.size - 1){
            mLastView.visibility = View.VISIBLE
        }else{
            mLastView.visibility = View.GONE
        }

        mTag.text = "# ${writeInfo.tag} "
        mTitle.text = writeInfo.title
        mDate.text = writeInfo.date

        when(writeInfo.weather){
            0 -> mWeather.setImageResource(R.drawable.sun)
            1 -> mWeather.setImageResource(R.drawable.cloudy)
            2 -> mWeather.setImageResource(R.drawable.cloud)
            3 -> mWeather.setImageResource(R.drawable.rainy)

        }

        mItemView.setOnClickListener {
            val intent = Intent(itemView.context, ViewerActivity::class.java)
            intent.putExtra("data",writeInfo)
            itemView.context.startActivity(intent)
        }
    }
}