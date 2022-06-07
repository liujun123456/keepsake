package com.shunlai.message.comment

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.utils.toast
import com.shunlai.message.MessageViewModel
import com.shunlai.message.entity.CommentBean

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
class CommentPresenter(var mContext:Context,var mView:CommentView) {
    private val mViewModel by lazy {
        ViewModelProvider(mContext as FragmentActivity).get(MessageViewModel::class.java)
    }

    init {
        initViewModel()
    }

    private var currentPage=1

    private var isRefresh=true

    private fun initViewModel(){
        mViewModel.commentResp.observe(mContext as FragmentActivity, Observer {
            if (isRefresh){
                currentPage=1
                mView.refreshComment(it)
            }else{
                if (!it.isNullOrEmpty()){
                    currentPage+=1
                }
                mView.loadMoreComment(it)
            }
        })
        mViewModel.doAttentionResp.observe(mContext as FragmentActivity, Observer {
            mView.dismissLoading()
            if (it.isSuccess){
                mView.updatePraiseState(it.isAttention)
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.doCommentResp.observe(mContext as FragmentActivity, Observer {
            mView.dismissLoading()
            if (it.isSuccess){
               toast("回复成功!")
            }else{
                toast(it.errorMsg)
            }
        })
    }

    fun queryComment(){
        mViewModel.queryComment(currentPage)
    }

    fun loadMoreComment(){
        isRefresh=false
        mViewModel.queryComment(currentPage+1)
    }

    fun doLikeComment(commentId:String){
        mView.showLoading("操作中!")
        mViewModel.doLike(commentId)
    }

    fun doComment(bean:CommentBean,content:String){
        mView.showLoading("回复中")
        mViewModel.doComment(bean.commentId,content,1,bean.pid,bean.publishMid,bean.ugcId)
    }
}
