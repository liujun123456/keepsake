package com.shunlai.message.entity.resp

import com.shunlai.message.entity.BaseResp
import com.shunlai.message.entity.SysPushMsgBean

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
class SysPushMsgResp:BaseResp() {
    var page:Int?=null
    var page_size:Int?=null
    var total_records:Long?=null
    var total_pages:Int?=null
    var data:MutableList<SysPushMsgBean>?= mutableListOf()
}
