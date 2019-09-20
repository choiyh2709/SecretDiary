package com.photocard.secretdiary.custom

import androidx.recyclerview.widget.RecyclerView
import android.view.View

abstract class CustomViewHolder<T>(view: View): RecyclerView.ViewHolder(view) {

    abstract fun bind(item: T, position: Int)
}