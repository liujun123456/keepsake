package com.shunlai.mine.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.shunlai.mine.R
import kotlinx.android.synthetic.main.dialog_leave_message_error.*

/**
 * @author Liu
 * @Date   2021/7/16
 * @mobile 18711832023
 */
class LeaveMessageFailDialog(var mContext: Context, var themeResId:Int):Dialog(mContext,themeResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_leave_message_error)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        tv_confirm.setOnClickListener {
            dismiss()
        }
    }
}
