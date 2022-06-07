package com.shunlai.ugc.goodsDetail.window

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.shunlai.common.utils.FileUtil
import com.shunlai.common.utils.ImageUtil
import com.shunlai.ugc.R
import kotlinx.android.synthetic.main.window_share_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
@SuppressLint("UseCompatLoadingForDrawables")
class ShareWindow(var mContext:Context,var mListener:ShareWindowListener):PopupWindow(),
    PopupWindow.OnDismissListener {
    init {
        val view= View.inflate(mContext,R.layout.window_share_layout,null)
        contentView=view
        width= ViewGroup.LayoutParams.MATCH_PARENT
        height= ViewGroup.LayoutParams.MATCH_PARENT
        initView(view)
        setOnDismissListener(this)
        update()
    }


    private fun initView(view:View){
        view.tv_cancel_share.setOnClickListener {
            dismiss()
        }
        view.iv_cancel_share.setOnClickListener {
            dismiss()
        }
        view.tv_share_we_chat.setOnClickListener {
            mListener.onWeChatShare(FileUtil.createBitmapFromView(view.show_img))
        }
        view.tv_share_circle.setOnClickListener {
            mListener.onCircleShare(FileUtil.createBitmapFromView(view.show_img))
        }
    }

    fun setImageUrl(imgUrl:String?){
        ImageUtil.showRoundImgWithStringAndRadius(contentView.show_img,mContext,imgUrl?:"",3f)
    }


    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        mListener.onShowWindow()
    }

    interface ShareWindowListener{
        fun onWeChatShare(bmp: Bitmap)
        fun onCircleShare(bmp:Bitmap)
        fun onShowWindow()
        fun onDismissWindow()
    }

    override fun onDismiss() {
        mListener.onDismissWindow()
    }
}
