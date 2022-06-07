package com.shunlai.message.entity.resp

import com.shunlai.message.entity.BaseResp
import com.shunlai.message.entity.CommentBean

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
class CommentResp:BaseResp() {
    var page:Int?=0
    var page_size:Int?=0
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<CommentBean>? = mutableListOf()

}
