package com.shunlai.mine.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.getui.gs.sdk.GsManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.mine.R
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.dialog_bind_phone_layout.*
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class BindPhoneDialog(var mContext: Context, var themeResId:Int):Dialog(mContext,themeResId) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_bind_phone_layout)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        tv_un_accept.setOnClickListener {
            dismiss()
            sensorsTrack(false)
            RouterManager.startActivityWithParams(BundleUrl.HOME_ACTIVITY,mContext as FragmentActivity)
            (mContext as FragmentActivity).finish()
        }

        tv_accept.setOnClickListener {
            dismiss()
            sensorsTrack(true)
            RouterManager.startActivityWithParams(BundleUrl.BIND_PHONE_ACTIVITY,mContext as FragmentActivity)
            (mContext as FragmentActivity).finish()
        }
    }

    //神策采集-----是否授权绑定手机号
    private fun sensorsTrack(boolean: Boolean){
        val params= JSONObject()
        params.put("page_name",(mContext as BaseActivity).screenUrl)
        params.put("authbindmobile_button_name",if (boolean) "allow" else "reject")
        GsManager.getInstance().onEvent("AuthBindMobile",params)
    }
}
