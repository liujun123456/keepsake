package com.shunlai.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.dialog_loading_layout.*

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
class LoadingDialog(mContext: Context, themeResId:Int): Dialog(mContext,themeResId){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading_layout)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }
}
