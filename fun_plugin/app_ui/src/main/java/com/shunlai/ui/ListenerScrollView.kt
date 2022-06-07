package com.shunlai.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.ScrollView

/**
 * @author Liu
 * @Date   2021/4/13
 * @mobile 18711832023
 */
class ListenerScrollView:ScrollView{
    constructor(mContext:Context):super(mContext)
    constructor(mContext:Context, attrs: AttributeSet):super(mContext,attrs)
    constructor(mContext:Context, attrs: AttributeSet, defStyleAttr: Int):super(mContext,attrs,defStyleAttr)

    override fun onScrollChanged(left: Int, top: Int, oldleft: Int, oldtop: Int) {
        super.onScrollChanged(left, top, oldleft, oldtop)
        mListener?.let {
            it.onScroll(top)
        }
    }
    private var mListener:TopScrollListener?=null


    fun setTopScrollListener(mListener:TopScrollListener){
        this.mListener=mListener
    }

    interface TopScrollListener{
        fun onScroll(top:Int)
    }
}
