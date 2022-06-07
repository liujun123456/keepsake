package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.bean.FeedRecord

/**
 * @author Liu
 * @Date   2021/7/23
 * @mobile 18711832023
 */
class FeedRecordResp {
    var page:Int?=1
    var page_size:Int?=20
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<FeedRecord>?= mutableListOf()
}
