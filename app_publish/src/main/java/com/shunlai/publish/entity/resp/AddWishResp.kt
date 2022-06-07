package com.shunlai.publish.entity.resp

import com.shunlai.common.bean.GoodsBean

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
class AddWishResp:BaseResp() {
    var id:Int?=null
    var name:String?=null
    var brandName:String?=null
    var price:String?=null
    var imageList:MutableList<String>?= mutableListOf()

    fun buildGoods(): GoodsBean {
        val bean= GoodsBean()
        bean.name=name
        bean.productId=id.toString()
        bean.brandName=brandName
        bean.price=price
        bean.type="4"
        if (!imageList.isNullOrEmpty()){
            bean.thumb= imageList!![0]
        }
        return bean
    }
}
