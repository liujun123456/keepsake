package com.shunlai.main.entities.resp

import com.shunlai.main.entities.UgcBean

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class UgcListResp {
    var page:Int?=1
    var page_size:Int?=20
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<UgcBean> = mutableListOf()
}
