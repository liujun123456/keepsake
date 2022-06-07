package com.shunlai.main.entities

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class UgcTjBean {
    var nickName:String?=""
    var avatar:String?=""
    var memberId:String?=""
    var introduce:String?=""
    var isFollow:String?=""
    var ugcHeadPictures:MutableList<TjUgcImage>? = mutableListOf()

    class TjUgcImage{
        var ugcId:String?=""
        var ugcType:String?=""
        var headPicture:String?=""
    }
}
