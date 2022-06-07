package com.shunlai.message.comment

import com.shunlai.message.entity.CommentBean

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
interface CommentView {

    fun loadMoreComment(mDates:MutableList<CommentBean>)

    fun refreshComment(mDates:MutableList<CommentBean>)

    fun updatePraiseState(praiseState:Int)

    fun showLoading(value:String)

    fun dismissLoading()

}
