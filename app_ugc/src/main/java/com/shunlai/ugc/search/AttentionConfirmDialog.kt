package com.shunlai.ugc.search

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.shunlai.ugc.R
import kotlinx.android.synthetic.main.dialog_confirm_layout.*

/**
 * @author Liu
 * @Date   2021/8/24
 * @mobile 18711832023
 */
class AttentionConfirmDialog(var mContext: Context, var themeResId:Int,var mListener:AttentionConfirmListener):Dialog(mContext,themeResId){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm_layout)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        tv_confirm.setOnClickListener {
            mListener.onConfirm()
            dismiss()
        }
        tv_cancel.setOnClickListener {
            dismiss()
        }
    }

    interface AttentionConfirmListener{
        fun onConfirm()
    }
}
