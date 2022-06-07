package com.shunlai.im



/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
interface ChatView {

    fun scrollToPosition(position:Int)

    fun showDates()

    fun notifyRange(start:Int,count:Int)

    fun pushMessage(type:Int,message:String)

}
