package com.shunlai.ui.moveRv

/**
 * @author Liu
 * @Date   2021/4/7
 * @mobile 18711832023
 */
interface MoveInterface {
    fun onItemMove(formPosition:Int,toPosition:Int)

    fun onItemRemove(position:Int)
}
