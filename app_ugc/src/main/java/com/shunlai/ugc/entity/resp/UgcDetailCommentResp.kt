package com.shunlai.ugc.entity.resp

import com.shunlai.ugc.entity.BaseResp
import com.shunlai.ugc.entity.UgcDetailCommentBean


/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class UgcDetailCommentResp: BaseResp() {
    var page:String?=""
    var page_size:String?=""
    var total_records:String?=""
    var total_pages:String?=""
    var data:MutableList<UgcDetailCommentBean> = mutableListOf()
}
