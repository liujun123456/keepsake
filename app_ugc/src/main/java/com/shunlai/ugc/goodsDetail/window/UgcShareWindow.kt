package com.shunlai.ugc.goodsDetail.window

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import com.shunlai.ugc.R
import kotlinx.android.synthetic.main.share_ugc_window_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
@SuppressLint("UseCompatLoadingForDrawables")
class UgcShareWindow(var mContext:Context, var mListener:UgcShareWindowListener):PopupWindow(){
    init {
        val view= View.inflate(mContext,R.layout.share_ugc_window_layout,null)
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


    private fun initView(view:View){
        view.tv_cancel_share.setOnClickListener {
            dismiss()
        }
        view.tv_share_we_chat.setOnClickListener {
            mListener.onWeChatShare()
        }
        view.tv_share_circle.setOnClickListener {
            mListener.onCircleShare()
        }
        view.tv_copy_url.setOnClickListener {
            mListener.onCopyUrl()
        }
        view.tv_build_img.setOnClickListener {
            mListener.onBuildImg()
        }
        view.tv_complaint.setOnClickListener {
            mListener.onComplaint()
        }
        view.tv_block.setOnClickListener {
            mListener.onBlock()
        }
        view.tv_delete.setOnClickListener {
            mListener.onDelete()
        }
        view.tv_share_qq.setOnClickListener {
            mListener.onQQShare()
        }
        view.tv_share_qq_zone.setOnClickListener {
            mListener.onQQZONEShare()
        }
    }

    fun show(isSelf:Boolean){
        showAtLocation((mContext as FragmentActivity).window.decorView, Gravity.NO_GRAVITY,0,0)
        if (isSelf){
            contentView.tv_complaint.visibility=View.GONE
            contentView.tv_block.visibility=View.GONE
        }else{
            contentView.tv_delete.visibility=View.GONE
        }
    }

    interface UgcShareWindowListener{
        fun onWeChatShare()
        fun onCircleShare()
        fun onQQShare()
        fun onQQZONEShare()
        fun onCopyUrl()
        fun onBuildImg()
        fun onBlock()
        fun onComplaint()
        fun onDelete()
    }

}
