package com.shunlai.message.entity.resp

import com.shunlai.message.entity.AttentionBean
import com.shunlai.message.entity.BaseResp

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
class AttentionResp: BaseResp() {
    var page:Int?=null
    var page_size:Int?=null
    var total_records:Long?=null
    var total_pages:Int?=null
    var data:MutableList<AttentionBean>? = mutableListOf()
}
