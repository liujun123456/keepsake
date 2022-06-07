package com.shunlai.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import com.google.android.material.appbar.AppBarLayout

/**
 * @author Liu
 * @Date   2021/5/17
 * @mobile 18711832023
 */
class ListenerAppbarLayout:LinearLayout, AppBarLayout.OnOffsetChangedListener {

    constructor(mContext: Context):super(mContext)
    constructor(mContext: Context, attrs: AttributeSet):super(mContext,attrs)
    constructor(mContext: Context, attrs: AttributeSet, defStyleAttr: Int):super(mContext,attrs,defStyleAttr)
    private var mAppbar:AppBarLayout?=null
    private var isTop=true
    private var lastY: Float = 0f
    private var mListener:OnAppbarActionListener?=null
    private val interceptOffset = 15

    fun setAppbar(appbar:AppBarLayout){
        mAppbar=appbar
        mAppbar?.addOnOffsetChangedListener(this)
    }

    fun setListener(listener:OnAppbarActionListener){
        mListener=listener
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                requestFocus()
                lastY = ev.rawY
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> return false
            MotionEvent.ACTION_MOVE ->{
                if (isTop && ev.rawY - lastY > interceptOffset) return true
            }

        }
        return false
    }

    private var offsetY = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action){
            MotionEvent.ACTION_MOVE -> {
                offsetY = event.rawY - lastY
                var value = (offsetY / 2.0f).toInt()
                if (offsetY <= 0) value = 0
                mListener?.onScrollPull(value)
            }
            MotionEvent.ACTION_UP ->{
                mListener?.onScrollUp()
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        isTop = verticalOffset==0
        mListener?.onOffsetChanged(verticalOffset)
    }


    interface OnAppbarActionListener{
        fun onOffsetChanged(verticalOffset: Int)

        fun onScrollPull(offset: Int)

        fun onScrollUp()
    }

}
