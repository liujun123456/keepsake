package com.shunlai.message

import android.text.TextUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.message.entity.*
import com.shunlai.message.entity.req.ComplainReq
import com.shunlai.message.entity.resp.*
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable
import com.shunlai.net.bean.CoreBaseModel
import com.shunlai.net.bean.FileRequest
import java.io.File

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class MessageViewModel: ViewModel()  {
    private val lifecycleOwner: LifecycleOwner?=null
    private val memberId=PreferenceUtil.getString(Constant.USER_ID)?:""

    val homeMsgResp:MutableLiveData<HomeMsgResp> = MutableLiveData()
    val collectResp:MutableLiveData<MutableList<CollectBean>> =MutableLiveData()
    val attentionResp:MutableLiveData<MutableList<AttentionBean>> =MutableLiveData()
    val doAttentionResp:MutableLiveData<DoAttentionResp> = MutableLiveData()
    val commentResp:MutableLiveData<MutableList<CommentBean>> = MutableLiveData()
    val uploadResp:MutableLiveData<UploadImgResp> = MutableLiveData()
    val doComplainResp:MutableLiveData<BaseResp> = MutableLiveData()
    val sysPushMsgResp:MutableLiveData<MutableList<SysPushMsgBean>> = MutableLiveData()
    val doCommentResp:MutableLiveData<BaseResp> = MutableLiveData()

    /**
     * 查询系统消息
     */
    fun querySysMsg(pageNo: Int){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        params[RunIntentKey.TYPE]=1
        params["page"]=pageNo
        params["size"]=20
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.QUERY_SYSTEM_MESSAGE,params)
            .subscribe(object :CoreHttpSubscriber<SysPushMsgResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    sysPushMsgResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: SysPushMsgResp?) {
                    sysPushMsgResp.postValue(t?.data?: mutableListOf())
                }

            })
    }

    /**
     * 更新系统消息为已读
     */
    fun updateSysMsg(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        params[RunIntentKey.TYPE]="1"
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.UPDATE_MESSAGE_READ, params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {

                }

                override fun onSuccess(t: BaseResp?) {

                }

            })
    }


    /**
     * 查询推送消息
     */
    fun queryPushMsg(pageNo: Int){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        params[RunIntentKey.TYPE]=2
        params["page"]=pageNo
        params["size"]=20
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.QUERY_SYSTEM_MESSAGE, params)
            .subscribe(object :CoreHttpSubscriber<SysPushMsgResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    sysPushMsgResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: SysPushMsgResp?) {
                    sysPushMsgResp.postValue(t?.data?: mutableListOf())
                }

            })
    }

    /**
     * 更新推送消息为已读
     */
    fun updatePushMsg(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        params[RunIntentKey.TYPE]="2"
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.UPDATE_MESSAGE_READ, params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {

                }

                override fun onSuccess(t: BaseResp?) {

                }

            })
    }

    /**
     * 查询关注列表
     */
    fun queryAttention(pageNo:Int){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        params["page"]=pageNo
        params["size"]=20
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.QUERY_ATTENTION, params)
            .subscribe(object :CoreHttpSubscriber<AttentionResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    attentionResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: AttentionResp?) {
                    attentionResp.postValue(t?.data?: mutableListOf())
                }

            })
    }

    /**
     * 更新新增关注消息为已读
     */
    fun updateAttention(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        params[RunIntentKey.TYPE]="3"
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.UPDATE_ATTENTION_MESSAGE_READ, params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {

                }

                override fun onSuccess(t: BaseResp?) {

                }

            })
    }

    /**
     * 关注或者不关注
     */
    fun doAttention(publishMid:String){
        val params= mutableMapOf<String,Any>()
        params["publishMid"]=publishMid
        MessageHttpManager.postByParams(lifecycleOwner,MessageApiConfig.ATTENTION_USER,params)
            .subscribe(object :CoreHttpSubscriber<Int>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    doAttentionResp.postValue(DoAttentionResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: Int?) {
                    if (t==null){
                        doAttentionResp.postValue(DoAttentionResp().apply {
                            buildError("操作失败")
                        })
                    }else{
                        doAttentionResp.postValue(DoAttentionResp().apply {
                            isAttention=t
                        })
                    }
                }
            })
    }

    /**
     * 查询赞和收藏
     */
    fun queryCollect(pageNo:Int){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        params["page"]=pageNo
        params["size"]=20
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.QUERY_LIKE_OR_FOLLOW, params)
            .subscribe(object :CoreHttpSubscriber<CollectResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    collectResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: CollectResp?) {
                    collectResp.postValue(t?.data?: mutableListOf())
                }

            })

    }

    /**
     * 更新赞和收藏消息为已读
     */
    fun updateCollect(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        params[RunIntentKey.TYPE]="0"
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.UPDATE_ATTENTION_MESSAGE_READ, params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {

                }

                override fun onSuccess(t: BaseResp?) {

                }

            })
    }

    /**
     * 回复评论
     */
    fun doComment(commentId:String?,content:String,isReply:Int,pId:String?,publishMid:String?,ugcId:String?){
        val params= mutableMapOf<String,Any>()
        commentId?.let {
            params["commentId"]=commentId
        }
        params["content"]=content
        params["isReply"]=isReply
        pId?.let {
            params["pId"]=pId
        }
        publishMid?.let {
            params["publishMid"]=publishMid
        }
        ugcId?.let {
            params[RunIntentKey.UGC_ID]=ugcId
        }
        MessageHttpManager.postByParams(lifecycleOwner,MessageApiConfig.REPLY_COMMENT,params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    doCommentResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    doCommentResp.postValue(BaseResp())
                }

            })
    }

    /**
     * 点赞
     */
    fun doLike(commentId:String){
        val params= mutableMapOf<String,Any>()
        params["commentId"]=commentId
        MessageHttpManager.postByParams(lifecycleOwner,MessageApiConfig.COMMENT_LIKE, params)
            .subscribe(object :CoreHttpSubscriber<Int>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    doAttentionResp.postValue(DoAttentionResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: Int?) {
                    if (t==null){
                        doAttentionResp.postValue(DoAttentionResp().apply {
                            buildError("操作失败")
                        })
                    }else{
                        doAttentionResp.postValue(DoAttentionResp().apply {
                            isAttention=t
                        })
                    }
                }
            })
    }

    /**
     * 查询评论和回复消息
     */
    fun queryComment(pageNo:Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=pageNo
        params["size"]=20
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.QUERY_COMMENT_DATA, params)
            .subscribe(object :CoreHttpSubscriber<CommentResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    commentResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: CommentResp?) {
                    commentResp.postValue(t?.data?: mutableListOf())
                }

            })
    }

    /**
     * 更新评论和回复消息
     */
    fun updateComment(){
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.UPDATE_COMMENT_MESSAGE_READ, mutableMapOf())
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {

                }

                override fun onSuccess(t: BaseResp?) {

                }

            })
    }

    /**
     * 查询首页各消息数量
     */
    fun queryHomeMsg(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MessageHttpManager.getByParams(lifecycleOwner,MessageApiConfig.QUERY_MESSAGE_NUM, params)
            .subscribe(object :CoreHttpSubscriber<HomeMsgResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                homeMsgResp.postValue(HomeMsgResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t: HomeMsgResp?) {
                homeMsgResp.postValue(t?:HomeMsgResp().apply {
                    buildError("获取消息异常")
                })
            }

        })
    }


    /**
     * 投诉
     */
    fun doComplain(req: ComplainReq){
        MessageHttpManager.postByModel(lifecycleOwner,MessageApiConfig.UGC_COMPLAIN, req)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    doComplainResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    doComplainResp.postValue(BaseResp())
                }

            })
    }

    /**
     * 上传图片
     */
    fun uploadFile(file: File){
        val req = FileRequest().apply {
            files.add(FileRequest.FileModel().apply {
                this.file=file
                this.fileName=file.name
                this.key=file.name
            })
        }
        MessageHttpManager.uploadFile(lifecycleOwner,MessageApiConfig.UPLOAD_IMG,req).subscribe(object :CoreHttpSubscriber<BaseResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                uploadResp.postValue(
                    UploadImgResp().apply {
                        buildError("上传图片失败")
                    })
            }

            override fun onSuccess(t: BaseResp?) {

            }

            override fun onSuccessSource(model: CoreBaseModel) {
                super.onSuccessSource(model)
                if (TextUtils.isEmpty(model.url)){
                    uploadResp.postValue(
                        UploadImgResp().apply {
                            buildError("上传图片失败")
                        })
                }else{
                    uploadResp.postValue(
                        UploadImgResp().apply {
                            url=model.url?:""
                        })
                }
            }
        })
    }



}
