package com.shunlai.ugc.entity.resp

import com.shunlai.ugc.entity.BaseResp
import com.shunlai.ugc.entity.UgcCommentBean

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class UgcCommentResp:BaseResp() {
    var page:Int?=0
    var page_size:Int?=0
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<UgcCommentBean>? = mutableListOf()
}
