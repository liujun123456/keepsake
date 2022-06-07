package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.bean.OrderBean

/**
 * @author Liu
 * @Date   2021/5/27
 * @mobile 18711832023
 */
class OrderResp {
    var page:Int?=0
    var page_size:Int?=0
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<OrderBean>? = mutableListOf()
}
