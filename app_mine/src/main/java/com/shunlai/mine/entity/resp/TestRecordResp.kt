package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.bean.TestRecordBean

/**
 * @author Liu
 * @Date   2021/8/26
 * @mobile 18711832023
 */
class TestRecordResp {
    var page:Int?=1
    var page_size:Int?=10
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<TestRecordBean> = mutableListOf()
}
