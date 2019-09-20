package com.photocard.secretdiary.custom

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Scroller




class NonSwipeViewPager : ViewPager {

    private var mScroller: FixedSpeedScroller? = null

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    private fun init(){
        try {
            val viewpager = ViewPager::class.java
            val scroller = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            scroller.set(this, FixedSpeedScroller(context))
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private inner class FixedSpeedScroller(context: Context) : Scroller(context) {

        private var mDuration = 1500

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }
    }
}
