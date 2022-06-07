package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.bean.OwnerUgcBean

/**
 * @author Liu
 * @Date   2021/7/22
 * @mobile 18711832023
 */
class OwnerUgcResp {
    var page:Int?=0
    var page_size:Int?=0
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<OwnerUgcBean>? = mutableListOf()
}
