package com.shunlai.publish.entity.req

import com.shunlai.common.bean.UgcGoods

/**
 * @author Liu
 * @Date   2021/4/13
 * @mobile 18711832023
 */
class PublishReq {
    var content:String=""
    var title:String=""
    var hasGoods:Int?=1
    var topicId:String?=null
    var images:MutableList<String> = mutableListOf()
    var mjId:Int?=null
    var sources:Int?=1
    var status:Int?=1
    var ugcCateId:Int?=null
    var ugcGoods:MutableList<UgcGoods> = mutableListOf()
    var ugcType:Int?=1
    var video:String?=null
    var videoTime:Int?=null
}
