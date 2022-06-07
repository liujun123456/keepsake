package com.shunlai.ugc

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable
import com.shunlai.ugc.entity.*
import com.shunlai.ugc.entity.resp.*

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
class UgcViewModel : ViewModel() {
    private val lifecycleOwner: LifecycleOwner?=null
    val hotSearchResp:MutableLiveData<MutableList<HotSearchBean>> = MutableLiveData()
    val ugcSearchResp:MutableLiveData<MutableList<UgcSearchBean>> =MutableLiveData()
    val userSearchResp:MutableLiveData<MutableList<SearchUserBean>> = MutableLiveData()
    val ugcGoodsDetailResp:MutableLiveData<UgcGoodsDetailResp> = MutableLiveData()
    var ugcCommentResp:MutableLiveData<UgcCommentResp> = MutableLiveData()
    var shareResp:MutableLiveData<UgcShareResp> = MutableLiveData()
    var ugcDetailImgResp:MutableLiveData<UgcBean> = MutableLiveData()
    var ugcDetailCommentList:MutableLiveData<MutableList<UgcDetailCommentBean>> = MutableLiveData()
    var ugcDetailReplyList:MutableLiveData<MutableList<UgcDetailCommentBean>> = MutableLiveData()
    var commentResp:MutableLiveData<UgcDetailCommentBean> = MutableLiveData()
    val likeResp:MutableLiveData<Int> =MutableLiveData()
    val collectResp:MutableLiveData<Int> = MutableLiveData()
    val attentionResp:MutableLiveData<AttentionActionResp> = MutableLiveData()
    val commentLikeResp:MutableLiveData<Int> =MutableLiveData()
    val ugcDeleteResp:MutableLiveData<BaseResp> = MutableLiveData()
    val buildOrderResp:MutableLiveData<BuildGoodsOrderResp> = MutableLiveData()

    /**
     * 查询热门搜索
     */
    fun queryHotSearch(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.TYPE]=2
        UgcHttpManager.getByParams(null,UgcApiConfig.HOT_SEARCH, params)
            .subscribe(object :CoreHttpSubscriber<MutableList<HotSearchBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    hotSearchResp.postValue( mutableListOf())
                }

                override fun onSuccess(t: MutableList<HotSearchBean>?) {
                    hotSearchResp.postValue(t?: mutableListOf())
                }

            })
    }

    /**
     * 关键字查询笔记
     */
    fun queryUgcList(keyWords:String,pageIndex:Int){
        val params= mutableMapOf<String,Any>()
        params["keyWords"]=keyWords
        params["page"]=pageIndex
        params["size"]=20
        UgcHttpManager.getByParams(null,UgcApiConfig.UGC_SEARCH, params)
            .subscribe(object :CoreHttpSubscriber<UgcSearchResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    ugcSearchResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: UgcSearchResp?) {
                    ugcSearchResp.postValue(t?.data?.data?: mutableListOf())
                }

            })
    }

    fun queryUserList(keyWords:String,pageIndex:Int){
        val params= mutableMapOf<String,Any>()
        params["keyWords"]=keyWords
        params["page"]=pageIndex
        params["size"]=20
        UgcHttpManager.getByParams(null,UgcApiConfig.USER_SEARCH, params)
            .subscribe(object :CoreHttpSubscriber<UserSearchResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    userSearchResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: UserSearchResp?) {
                    userSearchResp.postValue(t?.data?: mutableListOf())
                }

            })
    }

    /**
     * 查询商品详情
     */
    fun getGoodsDetail(productId:String,ugcId:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.PRODUCT_ID]=productId
        params[RunIntentKey.UGC_ID]=ugcId
        UgcHttpManager.getByParams(null,UgcApiConfig.UGC_GOODS_DETAIL, params)
            .subscribe(object :CoreHttpSubscriber<UgcGoodsDetailResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    ugcGoodsDetailResp.postValue(UgcGoodsDetailResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: UgcGoodsDetailResp?) {
                    ugcGoodsDetailResp.postValue(t?:UgcGoodsDetailResp().apply {
                        buildError("系统异常")
                    })
                }

            })
    }

    /**
     * 查询商品评价
     */
    fun queryGoodsComment(productId:String?,type:Int,pageIndex:Int,pageSize:Int=50){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.PRODUCT_ID]=productId?:""
        params[RunIntentKey.TYPE]=type
        params["page"]=pageIndex
        params["size"]=pageSize
        UgcHttpManager.getByParams(null,UgcApiConfig.GOODS_COMMENT_LIST, params)
            .subscribe(object :CoreHttpSubscriber<UgcCommentResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    ugcCommentResp.postValue(UgcCommentResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: UgcCommentResp?) {
                    ugcCommentResp.postValue(t?:UgcCommentResp().apply {
                        buildError("异常")
                    })
                }

            })
    }

    /**
     * 生成分享的图片
     */
    fun buildPoster(ugcId:String,shopMemberId:String,productId:String,goodType:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.UGC_ID]=ugcId
        params[RunIntentKey.SHOP_MEMBER_ID]=shopMemberId
        params[RunIntentKey.PRODUCT_ID]=productId
        params[RunIntentKey.GOODS_TYPE]=goodType
        params[RunIntentKey.LOGIN_TYPE]=1
        params[RunIntentKey.TYPE]=2
        UgcHttpManager.getByParams(null,UgcApiConfig.BUILD_POST,params)
            .subscribe(object :CoreHttpSubscriber<String>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    shareResp.postValue(UgcShareResp("").apply {
                        buildError("")
                    })
                }

                override fun onSuccess(t: String?) {
                    shareResp.postValue(UgcShareResp(t?:""))
                }

            })

    }


    /**
     * 查询笔记评论
     */
    fun queryUgcComment(page:Int,ugcId:String){
        val params= mutableMapOf<String,Any>()
        params["id"]=ugcId
        params["page"]=page
        params["size"]=10
        UgcHttpManager.getByParams(lifecycleOwner,UgcApiConfig.UGC_COMMENT_LIST,params)
            .subscribe(object :CoreHttpSubscriber<UgcDetailCommentResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    ugcDetailCommentList.postValue(mutableListOf())
                }

                override fun onSuccess(t: UgcDetailCommentResp?) {
                    ugcDetailCommentList.postValue(t?.data?: mutableListOf())
                }
            })
    }

    /**
     * 查询更多回复接口
     */
    fun queryUgcCommentReply(page:Int,commentId:String){
        val params= mutableMapOf<String,Any>()
        params["commentId"]=commentId
        params["page"]=page
        params["size"]=10
        UgcHttpManager.getByParams(lifecycleOwner,UgcApiConfig.UGC_COMMENT_REPLY_List,params)
            .subscribe(object :CoreHttpSubscriber<UgcDetailCommentResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    ugcDetailReplyList.postValue(mutableListOf())
                }

                override fun onSuccess(t: UgcDetailCommentResp?) {
                    ugcDetailReplyList.postValue(t?.data?: mutableListOf())
                }
            })
    }

    /**
     * 查询图文笔记详情
     */
    fun queryUgcImgDetail(ugcId: String){
        val params= mutableMapOf<String,Any>()
        params["id"]=ugcId
        UgcHttpManager.getByParams(lifecycleOwner,UgcApiConfig.UGC_DETAIL_IMG,params)
            .subscribe(object :CoreHttpSubscriber<UgcBean>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    ugcDetailImgResp.postValue(UgcBean().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: UgcBean?) {
                    ugcDetailImgResp.postValue(t?:UgcBean().apply {
                        buildError("获取详情失败")
                    })
                }
            })
    }

    /**
     * 查询视频笔记详情
     */
    fun queryUgcVideoDetail(ugcId: String){
        val params= mutableMapOf<String,Any>()
        params["id"]=ugcId
        UgcHttpManager.getByParams(lifecycleOwner,UgcApiConfig.UGC_DETAIL_VIDEO,params)
            .subscribe(object :CoreHttpSubscriber<UgcBean>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    ugcDetailImgResp.postValue(UgcBean().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: UgcBean?) {
                    ugcDetailImgResp.postValue(t?:UgcBean().apply {
                        buildError("获取详情失败")
                    })
                }
            })
    }

    fun buildGoodsOrder(req:BuildGoodsOrderReq){
        UgcHttpManager.postByModel(lifecycleOwner,UgcApiConfig.BUILD_GOODS_ORDER,req)
            .subscribe(object :CoreHttpSubscriber<BuildGoodsOrderResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    buildOrderResp.postValue(BuildGoodsOrderResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BuildGoodsOrderResp?) {
                    buildOrderResp.postValue(t?:BuildGoodsOrderResp().apply {
                        buildError("生成订单失败")
                    })
                }
            })

    }


    fun doComment(ugcId:String,content:String,commentId:String?,pId:String?,publishMid:String?,isReply:Int=0) {
        val params = mutableMapOf<String, Any>()
        params[RunIntentKey.UGC_ID] = ugcId
        params["content"] = content
        params["isReply"] = isReply
        commentId?.let {
            params["commentId"] = commentId
        }
        pId?.let {
            params["pId"] = pId
        }
        publishMid?.let {
            params["publishMid"] = publishMid
        }
        UgcHttpManager.postByParams(lifecycleOwner, UgcApiConfig.UGC_DO_COMMENT, params)
            .subscribe(object : CoreHttpSubscriber<UgcDetailCommentBean> {
                override fun onFailed(throwable: CoreHttpThrowable) {
                    commentResp.postValue(UgcDetailCommentBean().apply {
                        buildError(throwable.msg)
                        errorCode=throwable.code
                    })
                }

                override fun onSuccess(t: UgcDetailCommentBean?) {
                    commentResp.postValue(t?:UgcDetailCommentBean().apply {
                        buildError("评论失败!")
                    })
                }

            })
    }

    fun doAttention(id:String){
        val params= mutableMapOf<String,Any>()
        params["publishMid"]=id
        UgcHttpManager.postByParams(lifecycleOwner, UgcApiConfig.DO_ATTENTION, params)
            .subscribe(object :CoreHttpSubscriber<Int>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    attentionResp.postValue(AttentionActionResp(0,throwable.msg))
                }

                override fun onSuccess(t: Int?) {
                    attentionResp.postValue(AttentionActionResp(t?:0,""))
                }

            })
    }

    fun doLike(id:String){
        val params= mutableMapOf<String,Any>()
        params["id"]=id
        params[RunIntentKey.TYPE]=1
        UgcHttpManager.postByParams(lifecycleOwner, UgcApiConfig.DO_LIKE_OR_COLLECT, params)
            .subscribe(object :CoreHttpSubscriber<Int>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    likeResp.postValue(null)
                }

                override fun onSuccess(t: Int?) {
                    likeResp.postValue(t)
                }

            })
    }

    fun doCollect(id:String){
        val params= mutableMapOf<String,Any>()
        params["id"]=id
        params[RunIntentKey.TYPE]=0
        UgcHttpManager.postByParams(lifecycleOwner, UgcApiConfig.DO_LIKE_OR_COLLECT, params)
            .subscribe(object :CoreHttpSubscriber<Int>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    collectResp.postValue(null)
                }

                override fun onSuccess(t: Int?) {
                    collectResp.postValue(t)
                }

            })
    }

    fun doCommentLike(id:String){
        val params= mutableMapOf<String,Any>()
        params["commentId"]=id
        UgcHttpManager.postByParams(lifecycleOwner, UgcApiConfig.DO_COMMENT_LIKE, params)
            .subscribe(object :CoreHttpSubscriber<Int>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    commentLikeResp.postValue(null)
                }

                override fun onSuccess(t: Int?) {
                    commentLikeResp.postValue(t)
                }

            })
    }

    fun deleteUgc(ugcId:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.UGC_ID]=ugcId
        UgcHttpManager.postByParams(lifecycleOwner, UgcApiConfig.DELETE_UGC, params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    ugcDeleteResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    ugcDeleteResp.postValue(BaseResp())
                }

            })
    }

    fun blockUser(userId:String){
        val params= mutableMapOf<String,Any>()
        params["beBlackMember"]=userId
        UgcHttpManager.postByParams(lifecycleOwner, UgcApiConfig.BLOCK_USER,params).subscribe(object :CoreHttpSubscriber<String> {
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t: String?) {

            }
        })
    }

}
