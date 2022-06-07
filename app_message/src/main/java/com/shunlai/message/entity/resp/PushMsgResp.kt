package com.shunlai.message.entity.resp

import com.shunlai.message.entity.BaseResp
import com.shunlai.message.entity.HomeMsgBean

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
class PushMsgResp:BaseResp() {
    var id:String?=""
    var title:String?=""
    var content:String?=""
    var createTime:String?=""

    fun buildHomeMsgBean(count:Int): HomeMsgBean {
        val bean= HomeMsgBean()
        bean.content=content
        bean.title=title
        bean.count=count
        bean.time=createTime
        bean.type=1
        return bean
    }
}
