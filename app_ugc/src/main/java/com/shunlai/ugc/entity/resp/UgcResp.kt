package com.shunlai.ugc.entity.resp

import com.shunlai.ugc.entity.BaseResp
import com.shunlai.ugc.entity.UgcSearchBean

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class UgcResp {
    var page:Int?=0
    var page_size:Int?=0
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<UgcSearchBean> = mutableListOf()
}
