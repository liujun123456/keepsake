package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.bean.BlackUserBean

/**
 * @author Liu
 * @Date   2021/8/31
 * @mobile 18711832023
 */
class BlackListResp {
    var page:Int?=1
    var page_size:Int?=20
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<BlackUserBean>?= mutableListOf()
}
