package com.shunlai.ugc.goodsDetail

import com.shunlai.ugc.entity.UgcCommentBean
import com.shunlai.ugc.entity.resp.UgcGoodsDetailResp


/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
interface UgcGoodsDetailView {
    fun buildProductInfo(bean: UgcGoodsDetailResp)
    fun imageDetail(data:MutableList<String>)
    fun buildProductComment(data:MutableList<UgcCommentBean>,totalCount:Long)
    fun doShareResult(result:Boolean,imgUrl:String?,error:String?)
    fun showLoading(value:String)
    fun dismissLoading()
}
