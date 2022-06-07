package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.BaseResp
import com.shunlai.mine.entity.bean.ImpressionBean

/**
 * @author Liu
 * @Date   2021/7/20
 * @mobile 18711832023
 */
class LeaveMsgResp:BaseResp() {
    var data:MutableList<ImpressionBean> = mutableListOf()
}
