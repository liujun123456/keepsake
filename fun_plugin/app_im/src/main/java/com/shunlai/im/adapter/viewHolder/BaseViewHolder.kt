package com.shunlai.im.adapter.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Liu
 * @Date   2020/10/23
 * @mobile 18711832023
 */
abstract class BaseViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {


    open fun onViewDetachedFromWindow(){

    }

    abstract fun setData(msg: Any, position: Int)
}
