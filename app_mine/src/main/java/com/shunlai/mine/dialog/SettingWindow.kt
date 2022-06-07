package com.shunlai.mine.dialog

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.mine.R
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.window_setting_layout.view.*

/**
 * @author Liu
 * @Date   2021/5/18
 * @mobile 18711832023
 */
class SettingWindow(var mContext: Context,var mListener:SettingInterface): PopupWindow() {
    init {
        val view= View.inflate(mContext, R.layout.window_setting_layout,null)
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
        view.go_all_order.setOnClickListener {
            mListener.goOrder()
            dismiss()
        }

        view.tv_go_setting.setOnClickListener {
            mListener.goSetting()
            dismiss()
        }

        view.tv_share_to_friend.setOnClickListener {
            dismiss()
        }
        view.ll_close.setOnClickListener {
            dismiss()
        }
    }
    interface SettingInterface{
        fun goSetting()
        fun goOrder()
    }
}
