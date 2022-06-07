package com.shunlai.mine

import android.text.TextUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.toast
import com.shunlai.mine.entity.BaseResp
import com.shunlai.mine.entity.UploadResp
import com.shunlai.mine.entity.req.LoginReq
import com.shunlai.mine.entity.resp.LoginResp
import com.shunlai.mine.entity.bean.*
import com.shunlai.mine.entity.req.EditWallReq
import com.shunlai.mine.entity.req.UnBindWeChatReq
import com.shunlai.mine.entity.req.UpdateLabelReq
import com.shunlai.mine.entity.resp.*
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
class MineViewModel: ViewModel()  {
    private val lifecycleOwner: LifecycleOwner?=null
    private val memberId= PreferenceUtil.getString(Constant.USER_ID)?:""
    val sendSmsResp:MutableLiveData<BaseResp> = MutableLiveData()
    val bindPhoneResp:MutableLiveData<BindPhoneResp> = MutableLiveData()
    val loginResp:MutableLiveData<LoginResp> = MutableLiveData()
    val loginoutResp:MutableLiveData<BaseResp> = MutableLiveData()
    val unBindResp:MutableLiveData<BaseResp> = MutableLiveData()
    val bindWeChatResp:MutableLiveData<BindWeChatResp> = MutableLiveData()
    val mineUgcResp:MutableLiveData<MutableList<UgcBean>> = MutableLiveData()
    val userLikeUgcResp:MutableLiveData<MutableList<UgcBean>> = MutableLiveData()
    val userCollectUgcResp:MutableLiveData<MutableList<UgcBean>> = MutableLiveData()
    val memberInfo:MutableLiveData<MemberInfo> = MutableLiveData()
    val userGoods:MutableLiveData<GoodsListResp> = MutableLiveData()
    val photoWallList:MutableLiveData<MutableList<WallPhotoBean>> = MutableLiveData()
    val allPhotoWallList:MutableLiveData<MutableList<WallPhotoBean>> = MutableLiveData()
    val editPhotoWallResp:MutableLiveData<BaseResp> = MutableLiveData()
    val mineFollowResp:MutableLiveData<MutableList<FollowAndFun>> =MutableLiveData()
    val mineFunResp:MutableLiveData<MutableList<FollowAndFun>> = MutableLiveData()
    val attentionResp:MutableLiveData<Int> = MutableLiveData()
    val updateUserResp:MutableLiveData<BaseResp> = MutableLiveData()
    val updateLabelResp:MutableLiveData<BaseResp> = MutableLiveData()
    val labelListResp:MutableLiveData<MutableList<UserLabel>> = MutableLiveData()
    val skinListResp:MutableLiveData<MutableList<SkinBean>> = MutableLiveData()
    val updateSkinResp:MutableLiveData<BaseResp> = MutableLiveData()
    val orderResp:MutableLiveData<MutableList<OrderBean>> = MutableLiveData()
    val tokenResp:MutableLiveData<TokenTotalBean> = MutableLiveData()
    val tokenList:MutableLiveData<MutableList<TokenScoreBean>> = MutableLiveData()
    val ugcDeleteResp:MutableLiveData<BaseResp> = MutableLiveData()
    val uploadResp:MutableLiveData<String> = MutableLiveData()
    var resourceResp:MutableLiveData<ResourceResp> = MutableLiveData()
    var blackListResp:MutableLiveData<MutableList<BlackUserBean>> = MutableLiveData()

    fun sendSms(phone:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.PHONE]=phone
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.SEND_VERIFY_CODE,params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                sendSmsResp.postValue(BaseResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t: BaseResp?) {
                sendSmsResp.postValue(BaseResp())
            }

        })
    }

    fun loginWithVerify(req: LoginReq){
        MineHttpManager.postByModel(lifecycleOwner,MineApiConfig.LOGIN_WITH_VERIFY,req)
            .subscribe(object :CoreHttpSubscriber<LoginResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    loginResp.postValue(LoginResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: LoginResp?) {
                    loginResp.postValue(t?: LoginResp().apply {
                        buildError("登录失败!")
                    })
                }

            })
    }

    fun loginWithWeChat(code:String){
        val params= mutableMapOf<String,Any>()
        params["code"]=code
        params[RunIntentKey.LOGIN_TYPE]=3
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.LOGIN_WITH_WE_CHAT,params)
            .subscribe(object :CoreHttpSubscriber<LoginResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    loginResp.postValue(LoginResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: LoginResp?) {
                    loginResp.postValue(t?: LoginResp().apply {
                        buildError("登录失败!")
                    })
                }

            })

    }

    fun loginout(){
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.LOGIN_OUT, mutableMapOf())
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    loginoutResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    loginoutResp.postValue(BaseResp())
                }

            })
    }

    fun bindPhoneNum(code:String,phone:String){
        val params= mutableMapOf<String,Any>()
        params["code"]=code
        params[RunIntentKey.PHONE]=phone
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.BIND_MOBILE,params)
            .subscribe(object :CoreHttpSubscriber<BindPhoneResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    bindPhoneResp.postValue(BindPhoneResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BindPhoneResp?) {
                    bindPhoneResp.postValue(t?:BindPhoneResp().apply {
                        buildError("绑定失败")
                    })
                }

            })
    }

    fun bindWeChat(code:String){
        val params= mutableMapOf<String,Any>()
        params["code"]=code
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.BIND_WE_CHAT,params)
            .subscribe(object :CoreHttpSubscriber<BindWeChatResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    bindWeChatResp.postValue(BindWeChatResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BindWeChatResp?) {
                    bindWeChatResp.postValue(t?:BindWeChatResp().apply {
                        buildError("绑定失败")
                    })
                }
            })
    }

    fun unBindWeChat(appOpenId:String){
        val req=UnBindWeChatReq().buildData(memberId,appOpenId)
        MineHttpManager.postByModel(lifecycleOwner,MineApiConfig.UN_BIND_WE_CHAT,req )
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    unBindResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    unBindResp.postValue(BaseResp())
                }

            })
    }


    /**
     * 查询用户信息
     */
    fun queryMemberInfo(memberId:String?){
        val params= mutableMapOf<String,Any>()
        memberId?.let {
            params[RunIntentKey.MEMBER_ID]=memberId
        }
        MineHttpManager.getByParams(lifecycleOwner, MineApiConfig.QUERY_MEMBER_INFO,params)
            .subscribe(object :CoreHttpSubscriber<MemberInfo>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    memberInfo.postValue(MemberInfo().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: MemberInfo?) {
                    memberInfo.postValue(t?:MemberInfo().apply {
                        buildError("查询用户信息失败!")
                    })
                }

            })
    }


    /**
     * 查询用户发布的笔记列表
     * 不传memberId查自己
     */
    fun queryUserUgc(page:Int,memberId:String?=null){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        params["size"]=10
        memberId?.let {
            params[RunIntentKey.MEMBER_ID]=it
        }
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.QUERY_MEMBER_UGC,params)
            .subscribe(object :CoreHttpSubscriber<UgcListResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                        mineUgcResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: UgcListResp?) {
                    mineUgcResp.postValue(t?.data?: mutableListOf())
                }

            })

    }

    /**
     * type 1推荐 0避雷
     */
    fun queryUserGoods(page:Int,type:Int,memberId:String?=null){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        params[RunIntentKey.TYPE]=type
        params["size"]=10
        memberId?.let {
            params[RunIntentKey.MEMBER_ID]=it
        }
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.QUERY_MEMBER_GOODS,params)
            .subscribe(object :CoreHttpSubscriber<GoodsListResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    userGoods.postValue(GoodsListResp())
                }

                override fun onSuccess(t: GoodsListResp?) {
                    userGoods.postValue(t?:GoodsListResp())
                }

            })
    }

    /**
     * 查询喜欢的笔记
     */
    fun queryUserLikeUgc(page: Int,memberId:String?=null){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        params["size"]=10
        params[RunIntentKey.TYPE]=1
        memberId?.let {
            params[RunIntentKey.MEMBER_ID]=it
        }
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.QUERY_LIKE_UGC,params)
            .subscribe(object :CoreHttpSubscriber<UgcListResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    userLikeUgcResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: UgcListResp?) {
                    userLikeUgcResp.postValue(t?.data?: mutableListOf())
                }

            })
    }


    /**
     * 查询收藏的笔记
     */
    fun queryUserCollectUgc(page: Int,memberId:String?=null){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        params["size"]=10
        params[RunIntentKey.TYPE]=0
        memberId?.let {
            params[RunIntentKey.MEMBER_ID]=it
        }
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.QUERY_LIKE_UGC,params)
            .subscribe(object :CoreHttpSubscriber<UgcListResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    userCollectUgcResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: UgcListResp?) {
                    userCollectUgcResp.postValue(t?.data?: mutableListOf())
                }

            })
    }


    /**
     * 查询图片墙
     */
    fun queryWallPage(memberId:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.QUERY_USER_WALL_PHOTO,params)
            .subscribe(object :CoreHttpSubscriber<MutableList<WallPhotoBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    photoWallList.postValue(mutableListOf())
                }

                override fun onSuccess(t: MutableList<WallPhotoBean>?) {
                    photoWallList.postValue(t?: mutableListOf())
                }

            })
    }

    /**
     * 查询所有的图片
     */
    fun queryAllWallPage(memberId:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.QUERY_USER_ALL_WALL_PHOTO,params)
            .subscribe(object :CoreHttpSubscriber<MutableList<WallPhotoBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    allPhotoWallList.postValue(mutableListOf())
                }

                override fun onSuccess(t: MutableList<WallPhotoBean>?) {
                    allPhotoWallList.postValue(t?: mutableListOf())
                }

            })
    }

    /**
     * 编辑的图片墙
     */
    fun editWallPage(req:MutableList<WallPhotoBean>){

        MineHttpManager.postByModel(lifecycleOwner,MineApiConfig.UPDATE_USER_WALL_PHOTO,EditWallReq(req))
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    editPhotoWallResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    editPhotoWallResp.postValue(BaseResp())
                }

            })
    }

    /**
     * 查询我的关注
     */
    fun queryMineFollow(page:Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        params["size"]=10
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.MINE_FOLLOW,params)
            .subscribe(object :CoreHttpSubscriber<FollowAndFunResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    mineFollowResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: FollowAndFunResp?) {
                    mineFollowResp.postValue(t?.data?:mutableListOf())
                }

            })
    }


    /**
     * 查询我的粉丝
     */
    fun queryMineFun(page:Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        params["size"]=10
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.MINE_FUN_S,params)
            .subscribe(object :CoreHttpSubscriber<FollowAndFunResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    mineFunResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: FollowAndFunResp?) {
                    mineFunResp.postValue(t?.data?:mutableListOf())
                }

            })
    }

    /**
     * 关注
     */
    fun doAttention(publishMid:String){
        val params= mutableMapOf<String,Any>()
        params["publishMid"]=publishMid
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.ATTENTION_USER,params)
            .subscribe(object :CoreHttpSubscriber<Int>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    attentionResp.postValue(null)
                }

                override fun onSuccess(t: Int?) {
                    attentionResp.postValue(t)
                }
            })
    }

    /**
     * 更新用户信息
     */
    fun updateUserInfo(avatar:String?,introduce:String?,nickName:String?){
        val params= mutableMapOf<String,Any>()
        avatar?.let {
            params["avatar"]=avatar
        }
        introduce?.let {
            params["introduce"]=introduce
        }
        nickName?.let {
            params["nickName"]=nickName
        }

        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.UPDATE_USER_INFO,params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    updateUserResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    updateUserResp.postValue(BaseResp())
                }
            })
    }

    /**
     * 查询当前用户的所有标签
     */
    fun queryUserLabel(type:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.TYPE]=type
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.QUERY_USER_LABEL,params)
            .subscribe(object :CoreHttpSubscriber<MutableList<UserLabel>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    labelListResp.postValue(mutableListOf())
                    toast(throwable.msg)
                }

                override fun onSuccess(t: MutableList<UserLabel>?) {
                    labelListResp.postValue(t?: mutableListOf())
                }
            })
    }

    /**
     * 更新显示的标签
     */
    fun updateUserLabel(memberId:String,labels:MutableList<UserLabel>){
        val req=UpdateLabelReq()
        req.relationId=memberId
        req.labels=labels
        MineHttpManager.postByModel(lifecycleOwner,MineApiConfig.UPDATE_USER_LABEL,req)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    updateLabelResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    updateLabelResp.postValue(BaseResp())
                }
            })
    }

    /**
     * 查询所有皮肤风格
     */
    fun querySkinTheme(){
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.SKIN_THEME, mutableMapOf())
            .subscribe(object :CoreHttpSubscriber<MutableList<SkinBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    skinListResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: MutableList<SkinBean>?) {
                    skinListResp.postValue(t?: mutableListOf())
                }
            })
    }

    /**
     * 更新皮肤
     */
    fun updateSkinTheme(id:Int){
        val params= mutableMapOf<String,Any>()
        params["skinId"]=id
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.UPDATE_SKIN_THEME,params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    updateSkinResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    updateSkinResp.postValue(BaseResp())
                }
            })
    }

    /**
     * 查询订单
     */
    fun queryOrder(page:Int){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=PreferenceUtil.getString(Constant.USER_ID)?:""
        params["page"]=page
        params["size"]=20
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.QUERY_ORDER,params)
            .subscribe(object :CoreHttpSubscriber<OrderResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    orderResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: OrderResp?) {
                    orderResp.postValue(t?.data?: mutableListOf())
                }
            })

    }

    /**
     * 查询token分统计
     */
    fun queryToken(memberId:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.QUERY_TOKEN_SCORE,params)
            .subscribe(object :CoreHttpSubscriber<TokenTotalBean>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    tokenResp.postValue(TokenTotalBean().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: TokenTotalBean?) {
                    tokenResp.postValue(t?:TokenTotalBean().apply {
                        buildError("获取token分异常")
                    })
                }

            })
    }

    /**
     * 查看token分明细
     */
    fun queryTokenList(page:Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        params["size"]=20
        params[RunIntentKey.MEMBER_ID]=PreferenceUtil.getString(Constant.USER_ID)?:""
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.QUERY_TOKEN_SCORE_LIST,params)
            .subscribe(object :CoreHttpSubscriber<TokenListResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    tokenList.postValue(mutableListOf())
                }

                override fun onSuccess(t: TokenListResp?) {
                    tokenList.postValue(t?.data?: mutableListOf())
                }

            })
    }

    fun deleteUgc(ugcId:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.UGC_ID]=ugcId
        MineHttpManager.postByParams(lifecycleOwner, MineApiConfig.DELETE_UGC, params)
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


    fun doCollect(id:String){
        val params= mutableMapOf<String,Any>()
        params["id"]=id
        params[RunIntentKey.TYPE]=0
        MineHttpManager.postByParams(lifecycleOwner, MineApiConfig.DO_LIKE_OR_COLLECT, params)
            .subscribe(object :CoreHttpSubscriber<Int>{
                override fun onFailed(throwable: CoreHttpThrowable) {

                }

                override fun onSuccess(t: Int?) {

                }

            })
    }

    fun doLike(id:String){
        val params= mutableMapOf<String,Any>()
        params["id"]=id
        params[RunIntentKey.TYPE]=1
        MineHttpManager.postByParams(lifecycleOwner, MineApiConfig.DO_LIKE_OR_COLLECT, params)
            .subscribe(object :CoreHttpSubscriber<Int>{
                override fun onFailed(throwable: CoreHttpThrowable) {

                }

                override fun onSuccess(t: Int?) {

                }

            })
    }

    /**
     * 上传图片
     */
    fun uploadFile(file: File) {
        val req = FileRequest().apply {
            files.add(FileRequest.FileModel().apply {
                this.file = file
                this.fileName = file.name
                this.key = file.name
            })
        }
        MineHttpManager.uploadFile(lifecycleOwner,MineApiConfig.UPLOAD_IMG,req).subscribe(object :CoreHttpSubscriber<String>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                uploadResp.postValue("")
            }
            override fun onSuccess(t: String?) {

            }

            override fun onSuccessSource(model: CoreBaseModel) {
                super.onSuccessSource(model)
                if (TextUtils.isEmpty(model.url)){
                    uploadResp.postValue("")
                }else{
                    uploadResp.postValue(model.url)
                }
            }
        })
    }


    /**
     * 查询主理人和场景资源
     */
    fun querySourceList(){
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.SOURCE_LIST, mutableMapOf()).subscribe(object :CoreHttpSubscriber<ResourceResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                resourceResp.postValue(ResourceResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t: ResourceResp?) {
                resourceResp.postValue(t?:ResourceResp().apply {
                    buildError("查询资源失败")
                })
            }
        })
    }

    fun blockUser(userId:String){
        val params= mutableMapOf<String,Any>()
        params["beBlackMember"]=userId
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.BLOCK_USER,params).subscribe(object :CoreHttpSubscriber<String> {
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t: String?) {

            }
        })
    }


    fun queryBlackList(page:Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.BLACK_LIST,params).subscribe(object :CoreHttpSubscriber<BlackListResp> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                blackListResp.postValue(mutableListOf())
            }

            override fun onSuccess(t: BlackListResp?) {
                blackListResp.postValue(t?.data?: mutableListOf())
            }
        })
    }

    fun cancelBlack(memberId:String){
        val params= mutableMapOf<String,Any>()
        params["beBlackMember"]=memberId
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.CANCEL_BLACK,params).subscribe(object :CoreHttpSubscriber<String> {
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t: String?) {

            }
        })
    }

}
