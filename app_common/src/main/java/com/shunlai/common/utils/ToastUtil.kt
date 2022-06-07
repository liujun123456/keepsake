package com.shunlai.common.utils

import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.shunlai.common.BaseApplication
import com.shunlai.common.R

/**
 * @author Liu
 * @Date   2021/3/27
 * @mobile 18711832023
 */

fun toast(value:String){
    val view= View.inflate(BaseApplication.mInstance,R.layout.toast_layout,null)
    view.findViewById<TextView>(R.id.tv_toast).text=value
    val mToast=Toast(BaseApplication.mInstance)
    mToast.setGravity(Gravity.CENTER,0,0)
    mToast.duration=Toast.LENGTH_SHORT
    mToast.view=view
    mToast.show()
}

fun toast(@StringRes value: Int){
    val view= View.inflate(BaseApplication.mInstance,R.layout.toast_layout,null)
    view.findViewById<TextView>(R.id.tv_toast).setText(value)
    val mToast=Toast(BaseApplication.mInstance)
    mToast.setGravity(Gravity.CENTER,0,0)
    mToast.duration=Toast.LENGTH_SHORT
    mToast.view=view
    mToast.show()
}
