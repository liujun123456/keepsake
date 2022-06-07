package com.shunlai.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Liu
 * @Date   2021/7/6
 * @mobile 18711832023
 */
class DisableScrollScrollView:ScrollView {
    constructor(mContext:Context):super(mContext)
    constructor(mContext:Context,attrs: AttributeSet):super(mContext,attrs)
    constructor(mContext:Context,attrs: AttributeSet,defStyleAttr:Int):super(mContext,attrs,defStyleAttr)

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}
