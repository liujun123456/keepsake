package com.shunlai.ugc.details

import com.shunlai.ugc.entity.UgcBean
import com.shunlai.ugc.entity.UgcDetailCommentBean

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
interface UgcDetailView {
    fun initData(ugc: UgcBean?)

    fun showLoading(value:String)

    fun hideLoading()

    fun onDetailCallBack(ugc: UgcBean?)

    fun onCommentListCallBack(data: MutableList<UgcDetailCommentBean>)

    fun onMoreChildCommentCallBack(data: MutableList<UgcDetailCommentBean>)

    fun onDoCommentBack(bean:UgcDetailCommentBean)

    fun onLikeBack(value:Int)

    fun onCollectBack(value:Int)

    fun onAttentionBack(value: Int)

    fun onCommentLikeBack(value:Int)

    fun onDeleteUgc(result: Int)
}
