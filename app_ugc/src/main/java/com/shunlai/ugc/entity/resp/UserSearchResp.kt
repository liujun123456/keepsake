package com.shunlai.ugc.entity.resp

import com.shunlai.ugc.entity.SearchUserBean

/**
 * @author Liu
 * @Date   2021/8/24
 * @mobile 18711832023
 */
class UserSearchResp {
    var page:Int?=0
    var page_size:Int?=0
    var total_records:Long?=0
    var total_pages:Int?=0
    var data:MutableList<SearchUserBean> = mutableListOf()
}
