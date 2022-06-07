package com.shunlai.main.fragment.discover

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.shunlai.common.utils.toast
import com.shunlai.main.HomeViewModel

/**
 * @author Liu
 * @Date   2021/5/10
 * @mobile 18711832023
 */
class DiscoverPresenter(private var lifecycleOwner: LifecycleOwner, private var viewModelStoreOwner: ViewModelStoreOwner, private var mView:DiscoverView) {
    val mViewModel by lazy {
        ViewModelProvider(viewModelStoreOwner).get(HomeViewModel::class.java)
    }

    init {
        initViewModel()
    }

    var currentPage=1

    private var isRequest=false

    private var isRefresh=true

    private fun initViewModel(){
        mViewModel.recommendHtResp.observe(lifecycleOwner, Observer {
            mView.onHtLoad(it)
        })
        mViewModel.homeUgcRecommendResp.observe(lifecycleOwner, Observer {
            if (isRefresh){
                currentPage=1
                mView.onHomeRecommendUgc(it)
            }else{
                if (!it.isNullOrEmpty()){
                    currentPage+=1
                }
                mView.onMoreHomeRecommendUgc(it)
            }
        })
        mViewModel.attentionResp.observe(lifecycleOwner, Observer {
            if (!isRequest)return@Observer
            mView.dismissLoading()
            if (TextUtils.isEmpty(it.msg)){
                mView.onAttention(it.code)
            }else{
                toast(it.msg)
            }
            isRequest=false
        })
        mViewModel.ugcDeleteResp.observe(lifecycleOwner, Observer {
            if (!isRequest)return@Observer
            mView.dismissLoading()
            if (it.isSuccess){
                mView.onDeleteUgc(1)
            }else{
                toast(it.errorMsg)
            }
            isRequest=false
        })
    }

    fun queryHotHt(){
        mViewModel.queryRecommendHt()
    }

    fun queryHomeUgc(ugcList:MutableList<Int>,channel:String?=null){
        isRefresh=true
        mViewModel.queryHomeUgc(1,ugcList,channel)
    }

    fun loadMoreHomeUgc(ugcList:MutableList<Int>,channel:String?=null){
        isRefresh=false
        mViewModel.queryHomeUgc(currentPage+1,ugcList,channel)
    }

    fun doLike(ugcId: String,isLike:Boolean){
        mViewModel.doLike(ugcId)
        if (isLike){
            mView.onLike(0)
        }else{
            mView.onLike(1)
        }
    }

    fun doCollect(ugcId:String,isCollect:Boolean){
        mViewModel.doCollect(ugcId)
        if (isCollect){
            mView.onCollect(0)
        }else{
            mView.onCollect(1)
        }
    }

    fun doAttention(memberId:String){
        mView.showLoading("操作中")
        isRequest=true
        mViewModel.doAttention(memberId)
    }

    fun doUgcDelete(ugcId: String){
        mView.showLoading("操作中")
        isRequest=true
        mViewModel.deleteUgc(ugcId)
    }
}
