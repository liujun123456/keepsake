package com.shunlai.ui.srecyclerview

import androidx.recyclerview.widget.GridLayoutManager
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter


/**
 * @author Liu
 * @Date   2019-11-27
 * @mobile 18711832023
 */
 abstract class DefaultSpanSizeLookup(var mAdapter: SRecyclerAdapter, var layoutManager: GridLayoutManager):GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return if (position==0||position==(mAdapter?.itemCount!! -1)){
            layoutManager.spanCount
        }else{
            spanSize(position-1)
        }
    }

    abstract fun spanSize(position: Int):Int
}
