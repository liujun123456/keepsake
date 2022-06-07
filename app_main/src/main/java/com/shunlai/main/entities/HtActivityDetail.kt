package com.shunlai.main.entities

import com.shunlai.main.entities.resp.BaseResp


/**
 * @author Liu
 * @Date   2021/6/15
 * @mobile 18711832023
 */
class HtActivityDetail: BaseResp() {
    var id:String?=""
    var activityName:String?=""
    var activityStatus:String?=""
    var activityStartTime:String?=""
    var activityEndTime:String?=""
    var activityType:String?=""
    var activityTopicId:String?=""
    var billboardAttachment:String?=""
    var activityIntroduction:String?=""
    var avatarList:MutableList<String>?= mutableListOf()
}
