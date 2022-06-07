package com.shunlai.ugc.entity.resp

import com.shunlai.ugc.entity.BaseResp
import com.shunlai.ugc.entity.BuildGoodsOrderReq

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class UgcGoodsDetailResp:BaseResp() {
    var goodsId:String?=""
    var title:String?=""
    var stock:Int?=0
    var oldPrice:String?=""
    var finalPrice:String?=""
    var sellCount:String?=""
    var listImage:String?=""
    var images:MutableList<String>? = mutableListOf()
    var contentImages:MutableList<String>? = mutableListOf()
    var materialUrl:String?=""
    var shopName:String?=""
    var brandId:Int?=0
    var type:Int?=0  //1天猫  2淘宝 3京东  4心愿
    var shareMemberId:String?=""
    var shopMemberId:String?=""
    var couponUrl:String?=""
    var nickName:String?=""


    fun buildOrderReq(): BuildGoodsOrderReq {
        val req=BuildGoodsOrderReq()
        req.couponUrl=couponUrl
        req.goodsId=goodsId
        req.materialUrl=materialUrl
        req.type=type
        req.shareMemberId=shareMemberId
        return req
    }

}
