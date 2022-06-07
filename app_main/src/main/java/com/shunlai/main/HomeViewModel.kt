package com.shunlai.main

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.igexin.sdk.PushManager
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.main.entities.HtActivityDetail
import com.shunlai.main.entities.HuaTiBean
import com.shunlai.main.entities.UgcBean
import com.shunlai.main.entities.UgcTjBean
import com.shunlai.main.entities.resp.AttentionActionResp
import com.shunlai.main.entities.resp.BaseResp
import com.shunlai.main.entities.resp.UgcListResp
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable
import com.shunlai.net.util.GsonUtil

/**
 * @author Liu
 * @Date   2021/5/10
 * @mobile 18711832023
 */
class HomeViewModel: ViewModel()  {
    private val lifecycleOwner: LifecycleOwner?=null
    private val memberId= PreferenceUtil.getString(Constant.USER_ID)?:""

    val recommendHtResp:MutableLiveData<MutableList<HuaTiBean>> = MutableLiveData()
    val allHtResp:MutableLiveData<MutableList<HuaTiBean>> = MutableLiveData()
    val keywordHtResp:MutableLiveData<MutableList<HuaTiBean>> = MutableLiveData()
    val htUgcNewResp:MutableLiveData<MutableList<UgcBean>> =MutableLiveData()
    val htUgcRecommendResp:MutableLiveData<MutableList<UgcBean>> =MutableLiveData()
    val homeUgcRecommendResp:MutableLiveData<MutableList<UgcBean>> = MutableLiveData()
    val homeUgcAttentionResp:MutableLiveData<MutableList<UgcBean>> = MutableLiveData()
    val tjUgcBean:MutableLiveData<MutableList<UgcTjBean>> =MutableLiveData()
    val attentionResp:MutableLiveData<AttentionActionResp> = MutableLiveData()
    val likeResp:MutableLiveData<Int> =MutableLiveData()
    val collectResp:MutableLiveData<Int> = MutableLiveData()
    val ugcDeleteResp:MutableLiveData<BaseResp> = MutableLiveData()
    val htActivityDetailResp:MutableLiveData<HtActivityDetail> = MutableLiveData()
    val brandState:MutableLiveData<Int> = MutableLiveData()
    val ugcChannels:MutableLiveData<MutableList<String>> =MutableLiveData()


    fun queryAllHt(){
        HomeHttpManager.getByParams(lifecycleOwner,HomeApiConfig.QUERY_ALL_HT, mutableMapOf())
            .subscribe(object :CoreHttpSubscriber<MutableList<HuaTiBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    allHtResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: MutableList<HuaTiBean>?) {
                    allHtResp.postValue(t?: mutableListOf())
                }

            })
    }

    fun queryRecommendHt(){
        HomeHttpManager.getByParams(lifecycleOwner,HomeApiConfig.QUERY_HOT_HT, mutableMapOf())
            .subscribe(object :CoreHttpSubscriber<MutableList<HuaTiBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    recommendHtResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: MutableList<HuaTiBean>?) {
                    recommendHtResp.postValue(t?: mutableListOf())
                }

            })
    }

    fun queryHtByKeyword(keyword:String){
        val params = mutableMapOf<String,Any>()
        params["tag"]=keyword
        HomeHttpManager.getByParams(lifecycleOwner,HomeApiConfig.QUERY_HT_BY_KEYWORD, params)
            .subscribe(object :CoreHttpSubscriber<MutableList<HuaTiBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    keywordHtResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: MutableList<HuaTiBean>?) {
                    keywordHtResp.postValue(t?: mutableListOf())
                }

            })
    }


    fun queryHtUgcNew(id:String,page:Int){
        val params = mutableMapOf<String,Any>()
        params["topicId"]=id
        params["page"]=page
        params["size"]=10
        HomeHttpManager.getByParams(lifecycleOwner,HomeApiConfig.QUERY_HT_NEW_UGC, params)
            .subscribe(object :CoreHttpSubscriber<UgcListResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    htUgcNewResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: UgcListResp?) {
                    htUgcNewResp.postValue(t?.data?: mutableListOf())
                }

            })
    }

    fun queryHtUgcRecommend(id:String,page:Int){
        val params = mutableMapOf<String,Any>()
        params["topicId"]=id
        params["page"]=page
        params["size"]=10
        HomeHttpManager.getByParams(lifecycleOwner,HomeApiConfig.QUERY_HT_RECOMMEND_UGC, params)
            .subscribe(object :CoreHttpSubscriber<UgcListResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    htUgcRecommendResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: UgcListResp?) {
                    htUgcRecommendResp.postValue(t?.data?: mutableListOf())
                }

            })
    }

    fun queryHomeUgc(page:Int,ugcList:MutableList<Int>,channel:String?=null){
        val params = mutableMapOf<String,Any>()
        params["page"]=page
        params["size"]=10
        channel?.let {
            params["channel"]=channel
        }
        params["ugcList"]=GsonUtil.toJson(ugcList)
        HomeHttpManager.getByParams(lifecycleOwner,HomeApiConfig.QUERY_HOME_RECOMMEND_UGC,params)
            .subscribe(object :CoreHttpSubscriber<UgcListResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    homeUgcRecommendResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: UgcListResp?) {
                    homeUgcRecommendResp.postValue(t?.data?: mutableListOf())
                }

            })
    }

    fun queryHomeAttentionUgc(page:Int){
        val params = mutableMapOf<String,Any>()
        params["page"]=page
        params["size"]=10
        HomeHttpManager.getByParams(lifecycleOwner,HomeApiConfig.QUERY_HOME_ATTENTION_UGC,params)
            .subscribe(object :CoreHttpSubscriber<UgcListResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    homeUgcAttentionResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: UgcListResp?) {
                    homeUgcAttentionResp.postValue(t?.data?: mutableListOf())
                }

            })
    }

    fun queryHomeTj(){
        HomeHttpManager.getByParams(lifecycleOwner,HomeApiConfig.QUERY_HOME_ATTENTION_TUI_J,
            mutableMapOf())
            .subscribe(object :CoreHttpSubscriber<MutableList<UgcTjBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    tjUgcBean.postValue(mutableListOf())
                }

                override fun onSuccess(t: MutableList<UgcTjBean>?) {
                    tjUgcBean.postValue(t?: mutableListOf())
                }
            })
    }

    fun doAttention(id:String){
        val params= mutableMapOf<String,Any>()
        params["publishMid"]=id
        HomeHttpManager.postByParams(lifecycleOwner, HomeApiConfig.DO_ATTENTION, params)
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
        HomeHttpManager.postByParams(lifecycleOwner, HomeApiConfig.DO_LIKE_OR_COLLECT, params)
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
        HomeHttpManager.postByParams(lifecycleOwner, HomeApiConfig.DO_LIKE_OR_COLLECT, params)
            .subscribe(object :CoreHttpSubscriber<Int>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    collectResp.postValue(null)
                }

                override fun onSuccess(t: Int?) {
                    collectResp.postValue(t)
                }

            })
    }

    fun deleteUgc(ugcId:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.UGC_ID]=ugcId
        HomeHttpManager.postByParams(lifecycleOwner, HomeApiConfig.DELETE_UGC, params)
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

    fun queryDetailActivity(topicId:String){
        val params= mutableMapOf<String,Any>()
        params["topicId"]=topicId
        HomeHttpManager.getByParams(lifecycleOwner,HomeApiConfig.QUERY_HT_ACTIVITY,params)
            .subscribe(object :CoreHttpSubscriber<HtActivityDetail>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    htActivityDetailResp.postValue(HtActivityDetail().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: HtActivityDetail?) {
                    htActivityDetailResp.postValue(t?:HtActivityDetail())
                }

            })

    }

    fun queryBrandState(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        HomeHttpManager.getByParams(lifecycleOwner, HomeApiConfig.BRAND_STATE, params).subscribe(object :
            CoreHttpSubscriber<Int> {
            override fun onFailed(throwable: CoreHttpThrowable) {
                brandState.postValue(-1)
            }

            override fun onSuccess(t:Int?) {
                brandState.postValue(t?:0)
            }

        })
    }

    fun bindCidWithUser(cid:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        params[RunIntentKey.CID]=cid
        params[RunIntentKey.DEVICE_CHANNEL]="Android"
        HomeHttpManager.postByParams(lifecycleOwner,HomeApiConfig.BIND_CID,params).subscribe(object :CoreHttpSubscriber<String>{
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t: String?) {

            }
        })


    }

    fun getUgcChannels(){
        HomeHttpManager.getByParams(lifecycleOwner,HomeApiConfig.QUERY_UGC_CHANNELS, mutableMapOf()).subscribe(object :CoreHttpSubscriber<MutableList<String>>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                ugcChannels.postValue(mutableListOf())
            }

            override fun onSuccess(t: MutableList<String>?) {
                ugcChannels.postValue(t?: mutableListOf())
            }
        })
    }
}
