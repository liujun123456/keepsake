package com.shunlai.mine.tokenStar

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.toast
import com.shunlai.mine.MineApiConfig
import com.shunlai.mine.MineHttpManager
import com.shunlai.mine.entity.BaseResp
import com.shunlai.mine.entity.bean.BrandBean
import com.shunlai.mine.entity.bean.MatchResultBean
import com.shunlai.mine.entity.bean.TestRecordBean
import com.shunlai.mine.entity.bean.TokenStarUser
import com.shunlai.mine.entity.req.DoMatchReq
import com.shunlai.mine.entity.req.SaveBrandReq
import com.shunlai.mine.entity.resp.BrandListResp
import com.shunlai.mine.entity.resp.CheckBrandResp
import com.shunlai.mine.entity.resp.TestRecordResp
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable

/**
 * @author Liu
 * @Date   2021/8/27
 * @mobile 18711832023
 */
class TokenStarViewModel: ViewModel()  {
    val lifecycleOwner: LifecycleOwner?=null
    val memberId= PreferenceUtil.getString(Constant.USER_ID)?:""
    val testRecordList: MutableLiveData<MutableList<TestRecordBean>> = MutableLiveData()
    val brandList:MutableLiveData<MutableList<BrandBean>> = MutableLiveData()
    val saveBrandResp:MutableLiveData<BaseResp> = MutableLiveData()
    val jumpBrandResp:MutableLiveData<BaseResp> = MutableLiveData()
    val brandState:MutableLiveData<Int> = MutableLiveData()
    var brandCheckResp:MutableLiveData<CheckBrandResp> = MutableLiveData()
    var matchResp:MutableLiveData<MatchResultBean> = MutableLiveData()
    var starMap:MutableLiveData<MutableList<TokenStarUser>> = MutableLiveData()
    var shareImgResp:MutableLiveData<String> = MutableLiveData()
    val ownerBrandList:MutableLiveData<MutableList<BrandBean>> = MutableLiveData()



    /**
     * 发起的测试记录
     */
    fun querySendTestRecord(page: Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        MineHttpManager.getByParams(lifecycleOwner, MineApiConfig.SEND_TEST_RECORD, params).subscribe(object :
            CoreHttpSubscriber<TestRecordResp> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                testRecordList.postValue( mutableListOf())
            }

            override fun onSuccess(t:TestRecordResp?) {
                testRecordList.postValue(t?.data?: mutableListOf())
            }

        })
    }

    /**
     * 收到的测试记录
     */
    fun queryReceiverTestRecord(page: Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        MineHttpManager.getByParams(lifecycleOwner, MineApiConfig.RECEIVER_TEST_RECORD, params).subscribe(object :
            CoreHttpSubscriber<TestRecordResp> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                testRecordList.postValue( mutableListOf())
            }

            override fun onSuccess(t: TestRecordResp?) {
                testRecordList.postValue(t?.data?: mutableListOf())
            }

        })
    }

    /**
     * 查询推荐品牌
     */
    fun queryRecommendBrand(){
        MineHttpManager.getByParams(lifecycleOwner, MineApiConfig.RECOMMEND_BRAND_LIST, mutableMapOf()).subscribe(object :
            CoreHttpSubscriber<MutableList<BrandBean>> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                brandList.postValue( mutableListOf())
            }

            override fun onSuccess(t:MutableList<BrandBean>?) {
                brandList.postValue(t?: mutableListOf())
            }

        })
    }

    /**
     * 保存品牌信息
     */
    fun saveBrandState(data:MutableList<String>){
        val req=SaveBrandReq()
        req.brandCodes=data
        MineHttpManager.postByModel(lifecycleOwner, MineApiConfig.SAVE_BRAND_STATE, req).subscribe(object :
            CoreHttpSubscriber<String> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                saveBrandResp.postValue(BaseResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t:String?) {
                saveBrandResp.postValue(BaseResp())
            }

        })
    }

    /**
     * 根据关键字查询品牌
     */
    fun queryBrandByKey(keyWord:String,page: Int){
        val params= mutableMapOf<String,Any>()
        params["keyword"]=keyWord
        params["page"]=page
        MineHttpManager.getByParams(lifecycleOwner, MineApiConfig.QUERY_ALL_BRAND_LIST, params).subscribe(object :
            CoreHttpSubscriber<BrandListResp> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                brandList.postValue(mutableListOf())
            }

            override fun onSuccess(t:BrandListResp?) {
                brandList.postValue(t?.data?: mutableListOf())
            }

        })
    }

    /**
     * 跳过选择品牌
     */
    fun skipBrandChoose(){
        MineHttpManager.postByParams(lifecycleOwner, MineApiConfig.SKIP_BRAND, mutableMapOf()).subscribe(object :
            CoreHttpSubscriber<String> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                jumpBrandResp.postValue(BaseResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t:String?) {
                jumpBrandResp.postValue(BaseResp())
            }

        })
    }

    /**
     * 查询用户品牌状态
     */
    fun queryBrandState(memberId:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MineHttpManager.getByParams(lifecycleOwner, MineApiConfig.BRAND_STATE, params).subscribe(object :
            CoreHttpSubscriber<Int> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                brandState.postValue(-1)
            }

            override fun onSuccess(t:Int?) {
                brandState.postValue(t?:0)
            }

        })
    }


    /**
     * 会员匹配校验结果
     */
    fun checkBrandState(memberId:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MineHttpManager.getByParams(lifecycleOwner, MineApiConfig.CHECK_BRAND_STATE, params).subscribe(object :
            CoreHttpSubscriber<CheckBrandResp> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                brandCheckResp.postValue(CheckBrandResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t:CheckBrandResp?) {
                brandCheckResp.postValue(t?:CheckBrandResp().apply {
                    buildError("")
                })
            }

        })
    }

    /**
     * 发起测试
     */
    fun doStarMatch(memberId:String){
        MineHttpManager.postByModel(lifecycleOwner,MineApiConfig.MATCH_DISTANCE,DoMatchReq(memberId)).subscribe(object :
            CoreHttpSubscriber<MatchResultBean> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                matchResp.postValue(MatchResultBean().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t:MatchResultBean?) {
                matchResp.postValue(t?:MatchResultBean().apply {
                    buildError("匹配失败")
                })
            }

        })
    }

    /**
     * 查询用户距离
     */
    fun queryTokenMap(memberId:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.TOKEN_STAR_MAP,params).subscribe(object :
            CoreHttpSubscriber<MutableList<TokenStarUser>> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                starMap.postValue(mutableListOf())
            }

            override fun onSuccess(t:MutableList<TokenStarUser>?) {
                starMap.postValue(t?: mutableListOf())
            }

        })
    }

    /**
     * 生成token星球分享图
     */
    fun shareTokenStar(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.USER_ID]=memberId
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.SHARE_TOKEN_STAR,params).subscribe(object :
            CoreHttpSubscriber<String> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                toast(throwable.msg)

            }

            override fun onSuccess(t:String?) {
                shareImgResp.postValue(t?:"")
            }

        })
    }

    /**
     * 查询自己的品牌列表
     */
    fun queryOwnerBrand(){
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.OWNER_BRAND_LIST, mutableMapOf()).subscribe(object :
            CoreHttpSubscriber<MutableList<BrandBean>> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                ownerBrandList.postValue(mutableListOf())

            }

            override fun onSuccess(t:MutableList<BrandBean>?) {
                ownerBrandList.postValue(t?: mutableListOf())
            }

        })
    }
}
