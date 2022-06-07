package com.shunlai.main

import com.shunlai.main.entities.UgcGoodsBean

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
interface ActionInterface {
    fun doAttention(position:Int,memberId:String)
    fun doLike(position: Int,ugcId:String,isLike:Boolean)
    fun doCollect(position:Int,ugcId: String,isCollect:Boolean)
    fun doMore(position: Int,ugcId: String,memberId: String)
    fun doEva(bean: UgcGoodsBean)
}
