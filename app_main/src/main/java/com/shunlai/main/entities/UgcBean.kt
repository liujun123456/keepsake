package com.shunlai.main.entities

import com.shunlai.common.bean.PictureInfo

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class UgcBean {
    var ugcId:String?=""
    var memberId:String?=""
    var nickName:String?=""
    var avatar:String?=""
    var introduce:String?=""
    var isFollow:String?=""
    var images:String?=""
    var content:String?=""
    var ugcType:String?=""
    var publishTime:String?=""
    var imageList:MutableList<String>?=mutableListOf()
    var video:String?=""
    var title:String?=""
    var likes:Int?=0
    var comments:Int?=0
    var favorites:Int?=0
    var topicId:String?=""
    var topicTag:String?=""
    var ugcGoods:MutableList<UgcGoodsBean>? = mutableListOf()
    var whetherLikes:Boolean=false
    var whetherFavorites:Boolean=false
    var displayTime:String?=""
    var topic:HuaTiBean?=null
    var topping:Int=0
    var selectedFlag:Int=0
    var pictureList:MutableList<PictureInfo>? = mutableListOf()
    var distanceText:String?=""
    var score:Int?=0


}
