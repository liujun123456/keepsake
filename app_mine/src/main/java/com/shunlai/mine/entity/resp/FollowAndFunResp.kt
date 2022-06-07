package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.BaseResp
import com.shunlai.mine.entity.bean.FollowAndFun

/**
 * @author Liu
 * @Date   2021/5/25
 * @mobile 18711832023
 */
class FollowAndFunResp: BaseResp() {
    var page:Int?=0
    var page_size:Int?=0
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<FollowAndFun>?= mutableListOf()

}
