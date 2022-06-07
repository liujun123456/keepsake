package com.shunlai.message.entity.resp

import com.shunlai.message.entity.BaseResp
import com.shunlai.message.entity.HomeMsgBean

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
class SystemMsgResp:BaseResp() {
    var id:String?=""
    var title:String?=""
    var content:String?=""
    var image:String?=""
    var createTime:String?=""

    fun buildHomeMsgBean(count:Int):HomeMsgBean{
        val bean=HomeMsgBean()
        bean.content=content
        bean.title=title
        bean.count=count
        bean.img=image
        bean.time=createTime
        bean.type=0
        return bean
    }
}
