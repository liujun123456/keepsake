package com.shunlai.mine.entity.bean

import com.shunlai.mine.entity.BaseResp


/**
 * @author Liu
 * @Date   2021/5/19
 * @mobile 18711832023
 */
class MemberInfo : BaseResp(){
    var avatar:String?=""
    var feesNum:Long?=0
    var token:Long?=0
    var followNum:Long?=0
    var id:Long?=0
    var introduce:String?=""
    var isEachOther:Int?=0
    var isFollow:Int?=0
    var likesNum:Long?=0
    var nickName:String?=""
    var labelList:MutableList<UserLabel>?= mutableListOf()
    var themeSkin:SkinBean?=null
    var productNum:String?="0"
    var productImgList:MutableList<String>? = mutableListOf()
    var logoUrl:String?=""
    var principalModelId:String?=""
    var principalSceneId:String?=""
    var status:Int?=-1
}
