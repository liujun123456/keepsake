package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.BaseResp
import com.shunlai.mine.entity.bean.TokenStarUser

/**
 * @author Liu
 * @Date   2021/9/1
 * @mobile 18711832023
 */
class CheckBrandResp:BaseResp() {
    var myStatus:String?=""
    var otherStatus:String?=""
    var list:MutableList<TokenStarUser> = mutableListOf()
}
