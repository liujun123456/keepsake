package com.shunlai.ugc.entity

/**
 * @author Liu
 * @Date   2021/6/3
 * @mobile 18711832023
 */
class GoodsCommentBean:BaseUgcGoodsBean() {
    var data: MutableList<UgcCommentBean> = mutableListOf()
    var totalCount:String?=""
    var productId:String?=""
}
