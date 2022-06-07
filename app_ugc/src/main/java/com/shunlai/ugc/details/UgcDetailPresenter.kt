package com.shunlai.ugc.details

import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.getui.gs.sdk.GsManager
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.toast
import com.shunlai.ugc.UgcViewModel
import com.shunlai.ugc.entity.UgcBean
import com.shunlai.ugc.entity.event.MoreCommentEvent
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class UgcDetailPresenter(var mContext:FragmentActivity,var mView:UgcDetailView) {
    private val ugcDetailId by lazy {
        mContext.intent.getStringExtra(RunIntentKey.UGC_ID)?:""
    }

    private val ugcType by lazy {
        mContext.intent.getStringExtra(RunIntentKey.UGC_TYPE)?:"1"
    }

    private val mViewModel by lazy {
        ViewModelProvider(mContext).get(UgcViewModel::class.java)
    }

    private var currentPage=1

    var bean:UgcBean?=null

    init {
        initViewModel()
    }

    private fun initViewModel(){
        mViewModel.ugcDetailImgResp.observe(mContext, Observer {
            bean=it
            if (it.isSuccess){
                mView.onDetailCallBack(it)
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.ugcDetailCommentList.observe(mContext, Observer {
            if (it.isNotEmpty()){
                currentPage++
            }
            mView.onCommentListCallBack(it)
        })
        mViewModel.commentResp.observe(mContext, Observer {
            mView.hideLoading()
            if (it.isSuccess){
                mView.onDoCommentBack(it)
            }else{
                trackUgcComment(false,it.errorCode)
                toast(it.errorMsg)
            }
        })
        mViewModel.attentionResp.observe(mContext, Observer {
            mView.hideLoading()
            if (TextUtils.isEmpty(it.msg)){
                mView.onAttentionBack(it.code)
            }else{
                toast(it.msg)
            }
        })


        mViewModel.ugcDeleteResp.observe(mContext, Observer {
            mView.hideLoading()
            if (it.isSuccess){
                mView.onDeleteUgc(1)
            }else{
                toast(it.errorMsg)
            }
        })

        mViewModel.ugcDetailReplyList.observe(mContext, Observer {
            mView.hideLoading()
            mView.onMoreChildCommentCallBack(it)
        })
    }

    fun getDetail(){
        if (ugcType=="1"){
            mViewModel.queryUgcImgDetail(ugcDetailId)
        }else{
            mViewModel.queryUgcVideoDetail(ugcDetailId)
        }

    }

    fun queryComment(){
        mViewModel.queryUgcComment(currentPage,ugcDetailId)
    }

    fun queryMoreChildComment(event: MoreCommentEvent){
        mView.showLoading("查看更多评论!")
        mViewModel.queryUgcCommentReply(event.page,event.commentId?:"")
    }

    fun doComment(content:String,commentId:String?,pid:String?,publishMid:String?,isReply:Int?){
        mView.showLoading("发布评论中!")
        mViewModel.doComment(ugcDetailId,content,commentId,pid,publishMid,isReply?:0)
    }

    fun doLike(){
        mViewModel.doLike(ugcDetailId)
        if (bean?.isLike==0){   //不关心结果直接返回
            mView.onLikeBack(1)
        }else{
            mView.onLikeBack(0)
        }
    }

    fun doCommentLike(commentId:String,isLike:Int){
        mViewModel.doCommentLike(commentId)
        if (isLike==0){
            mView.onCommentLikeBack(1)
        }else{
            mView.onCommentLikeBack(0)
        }
    }

    fun doCollect(){
        mViewModel.doCollect(ugcDetailId)
        if (bean?.isFavorite==0){
            mView.onCollectBack(1)
        }else{
            mView.onCollectBack(0)
        }
    }

    fun doAttention(){
        mView.showLoading("操作中!")
        mViewModel.doAttention(bean?.publishMid?:"")
    }


    fun doUgcDelete(){
        mView.showLoading("操作中")
        mViewModel.deleteUgc(bean?.id?:"")
    }

    fun doBlock(){
        mViewModel.blockUser(bean?.publishMid?:"")
    }

    fun trackUgcComment(isSuccess:Boolean,code:Int?){
        val params = JSONObject()
        bean?.topic?.id?.let {
            params.put("topic_ids", it)
        }
        params.put("uid",bean?.memberId)
        params.put("note_type",bean?.ugcType)
        params.put("note_id",bean?.ugcId)
        code?.let {
            val msg:String
            if (it==1021||it==1022||it==1023){
                msg="block"
            }else if (it==3001){
                msg="refuse"
            }else{
                msg="overtime"
            }
            params.put("fail",msg)
        }
        params.put("success",if (isSuccess) "yes" else "no")
        GsManager.getInstance().onEvent("post_note", params)
    }

}
