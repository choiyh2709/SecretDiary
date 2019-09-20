package com.photocard.secretdiary.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photocard.secretdiary.R
import com.photocard.secretdiary.custom.CustomViewHolder
import com.photocard.secretdiary.data.WriteInfo
import io.realm.RealmResults


class TimelineAdapter(private var mWriteList: RealmResults<WriteInfo>, private val onTimelineHeaderListener: TimelineHeaderViewHolder.OnTimelineHeaderListener) : RecyclerView.Adapter<CustomViewHolder<RealmResults<WriteInfo>>>(){
    private val HEADER = 0
    private val ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder<RealmResults<WriteInfo>> {
        return when(viewType){
            HEADER -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline_header, parent, false)
                TimelineHeaderViewHolder(v, onTimelineHeaderListener)
            }
            else -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline_write, parent, false)
                TimelineWriteViewHolder(v)
            }
        }

    }

    override fun getItemCount(): Int {
        return mWriteList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> HEADER
            else -> ITEM
        }
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder<RealmResults<WriteInfo>>, position: Int) {
        when (getItemViewType(position)) {
            HEADER -> (viewHolder as TimelineHeaderViewHolder).bind(mWriteList, position)
            ITEM -> (viewHolder as TimelineWriteViewHolder).bind(mWriteList, position - 1)
        }
    }

    fun setData(data: RealmResults<WriteInfo>){
        mWriteList = data
    }
}