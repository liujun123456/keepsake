package com.shunlai.mine.entity.req

import com.shunlai.mine.entity.bean.UserLabel

/**
 * @author Liu
 * @Date   2021/5/25
 * @mobile 18711832023
 */
class UpdateLabelReq {
    var relationId:String?=""
    var type:String?="1"
    var labels:MutableList<UserLabel> = mutableListOf()
}
