package com.shunlai.mine.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import com.shunlai.mine.R
import kotlinx.android.synthetic.main.share_star_window_layout.view.*

/**
 * @author Liu
 * @Date   2021/9/2
 * @mobile 18711832023
 */
class TokenStarShareWindow(var mContext: Context, var mListener:TokenStarShareWindowListener):PopupWindow() {

    init {
        val view= View.inflate(mContext, R.layout.share_star_window_layout,null)
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
    }

    fun showWindow(){
        showAtLocation((mContext as FragmentActivity).window.decorView, Gravity.NO_GRAVITY,0,0)
    }

    interface TokenStarShareWindowListener{
        fun onWeChatShare()
        fun onCircleShare()
        fun onCopyUrl()
        fun onBuildImg()
    }

}
