package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.bean.TokenScoreBean

/**
 * @author Liu
 * @Date   2021/5/28
 * @mobile 18711832023
 */
class TokenListResp {
    var page:String?=""
    var page_size:String?=""
    var total_records:String?=""
    var total_pages:String?=""
    var data:MutableList<TokenScoreBean>?= mutableListOf()
}
