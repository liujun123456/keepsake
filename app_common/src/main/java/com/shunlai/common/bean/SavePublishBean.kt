package com.shunlai.common.bean



/**
 * @author Liu
 * @Date   2021/4/15
 * @mobile 18711832023
 */
class SavePublishBean {
    var title:String?=""
    var content:String?=""
    var ht:String?=""
    var selectImage:MutableList<PathItem> = mutableListOf()
    var signGoods:MutableList<GoodsBean> = mutableListOf()
    var type:Int?=1
}
