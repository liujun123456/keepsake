package com.shunlai.publish.entity.resp

import com.shunlai.common.bean.GoodsBean

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
class SearchResp: BaseResp() {
    val total:String?=null
    val size:String?=null
    val pages:Int?=null
    val current:Int?=null
    var data:MutableList<GoodsBean>? = mutableListOf()
}
