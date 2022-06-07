package com.shunlai.ugc.entity

import com.shunlai.ugc.entity.resp.UgcDetailImgResp

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class UgcBean:BaseResp() {
    var ugcId:String?=""
    var id:String?=""
    var memberId:String?=""
    var nickName:String?=""
    var avatar:String?=""
    var introduce:String?=""
    var isFollow:Int?=0
    var images:String?=""
    var content:String?=""
    var ugcType:String?=""
    var publishTime:String?=""
    var publishMid:String?=""
    var imageList:MutableList<String>?=mutableListOf()
    var video:String?=""
    var title:String?=""
    var likes:Int?=0
    var comments:Int?=0
    var favorites:Int?=0
    var topicId:String?=""
    var topicTag:String?=""
    var topic:HuaTiBean?=null
    var ugcGoods:MutableList<UgcGoodsBean>? = mutableListOf()
    var likeMembers:MutableList<UgcDetailImgResp.LikeMember>? = mutableListOf()
    var isLike:Int?=0
    var isFavorite:Int?=0
    var width:Int?=0
    var height:Int?=0
    var displayTime:String?=""

    class LikeMember{
        var id:String?=""
        var nickName:String?=""
        var avatar:String?=""
    }

}
