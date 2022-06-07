package com.shunlai.im.adapter.viewHolder

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.shunlai.im.R

/**
 * @author Liu
 * @Date   2021/8/4
 * @mobile 18711832023
 */
class BlackViewProvider(var mContext: Context) {
    fun getViewHolder(): BaseViewHolder {
        val view = View.inflate(mContext, R.layout.item_chat_black_notice_layout, null)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return BlackViewHolder(view)
    }

    class BlackViewHolder(var mView: View) : BaseChatViewHolder(mView) {
        override fun setData(msg: Any, position: Int) {

        }
    }
}
