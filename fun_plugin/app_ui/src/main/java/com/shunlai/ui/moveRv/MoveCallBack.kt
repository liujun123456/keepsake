package com.shunlai.ui.moveRv

import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Liu
 * @Date   2021/4/7
 * @mobile 18711832023
 */
class MoveCallBack(var mAdapter:MoveAdapter): ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags:Int
        dragFlags = if (recyclerView.layoutManager is GridLayoutManager){
            ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.UP or ItemTouchHelper.DOWN
        }else {
            val orientation= (recyclerView.layoutManager as LinearLayoutManager).orientation
            if (orientation==LinearLayout.HORIZONTAL){
                ItemTouchHelper.START or ItemTouchHelper.END
            }else{
                ItemTouchHelper.UP or ItemTouchHelper.DOWN
            }
        }

//        val swipeFlags=ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT  //关闭滑动删除
        return makeMovementFlags(dragFlags,0)
    }

    override fun onMove(recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (viewHolder.itemViewType!=target.itemViewType){
            return false
        }
        mAdapter.onItemMove(viewHolder.adapterPosition,target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemRemove(viewHolder.adapterPosition)
    }

}
