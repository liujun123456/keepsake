package com.shunlai.mine.entity.bean

import com.shunlai.mine.entity.BaseResp

/**
 * @author Liu
 * @Date   2021/7/22
 * @mobile 18711832023
 */
class SignBoardBean:BaseResp() {
    var memberId:String?=""
    var ugcId:String?=""
    var ugcTitle:String?=""
    var enabled:String?=""
    var imageUrlList:MutableList<String>? = mutableListOf()
}
