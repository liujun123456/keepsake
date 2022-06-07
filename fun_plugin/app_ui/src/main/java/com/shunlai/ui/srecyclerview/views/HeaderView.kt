package com.shunlai.ui.srecyclerview.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.shunlai.ui.R

/**
 * @author  AsureLiu on 2017/11/28.
 */
class HeaderView:FrameLayout{
    var mContext:Context
    constructor(context: Context):super(context,null){
        this.mContext=context
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs,0){
        this.mContext=context
        initView()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        this.mContext=context
        initView()
    }
    fun initView(){
        val header= View.inflate(mContext, R.layout.refresh_header_view,null)
        addView(header)
    }
}
