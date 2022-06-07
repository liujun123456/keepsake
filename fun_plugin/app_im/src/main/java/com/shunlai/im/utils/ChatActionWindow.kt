package com.shunlai.im.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import com.shunlai.im.R
import kotlinx.android.synthetic.main.chat_action_window.view.*


/**
 * @author Liu
 * @Date   2021/5/18
 * @mobile 18711832023
 */
class ChatActionWindow(var mContext: Context, var mListener:ActionListener): PopupWindow() {
    init {
        val view= View.inflate(mContext, R.layout.chat_action_window,null)
        contentView=view
        width= ViewGroup.LayoutParams.MATCH_PARENT
        height=  ViewGroup.LayoutParams.MATCH_PARENT
        isClippingEnabled=false
        isOutsideTouchable = true
        isFocusable=true
        animationStyle= R.style.PopupBottomInAnimation
        initView(view)
        update()
    }

    private fun initView(view: View){
        view.tv_block.setOnClickListener {
            mListener.onBlockAction()
            dismiss()
        }
        view.tv_complaint.setOnClickListener {
            mListener.onComplaintAction()
            dismiss()
        }
        view.tv_cancel.setOnClickListener {
            dismiss()
        }
        view.setOnClickListener {
            dismiss()
        }
    }

    fun showWindow(){
        showAtLocation((mContext as FragmentActivity).window.decorView, Gravity.NO_GRAVITY,0,0)
    }

    interface ActionListener{
        fun onComplaintAction()
        fun onBlockAction()
    }
}
