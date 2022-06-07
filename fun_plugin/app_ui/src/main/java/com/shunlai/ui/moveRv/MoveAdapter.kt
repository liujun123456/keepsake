package com.shunlai.ui.moveRv

import androidx.recyclerview.widget.RecyclerView

/**
 * @author Liu
 * @Date   2021/4/7
 * @mobile 18711832023
 */
abstract class MoveAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>(),MoveInterface {

    override fun onItemMove(fromPosition: Int, toPosition: Int) {

    }

    override fun onItemRemove(position: Int) {

    }
}
