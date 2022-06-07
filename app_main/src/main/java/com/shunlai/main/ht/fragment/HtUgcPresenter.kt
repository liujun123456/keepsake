package com.shunlai.main.ht.fragment

import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.utils.toast
import com.shunlai.main.HomeViewModel

/**
 * @author Liu
 * @Date   2021/5/12
 * @mobile 18711832023
 */
class HtUgcPresenter(var mContext: FragmentActivity, var mView:HtUgcView) {
    private val mViewModel by lazy {
        ViewModelProvider(mContext).get(HomeViewModel::class.java)
    }

    init {
        initViewModel()
    }

    private var isRequest=false

    private var isRefresh=true

    private fun initViewModel(){
        mViewModel.htUgcRecommendResp.observe(mContext, Observer {
            if (isRefresh){
                currentPage=1
                mView.onRecommendUgcLoad(it)
            }else{
                if (!it.isNullOrEmpty()){
                    currentPage+=1
                }
                mView.onMoreRecommendUgcLoad(it)
            }

        })

        mViewModel.htUgcNewResp.observe(mContext, Observer {
            if (isRefresh){
                currentPage=1
                mView.onNewUgcLoad(it)
            }else{
                if (!it.isNullOrEmpty()){
                    currentPage+=1
                }
                mView.onNewMoreUgcLoad(it)
            }

        })

        mViewModel.attentionResp.observe(mContext, Observer {
            if (!isRequest)return@Observer
            mView.dismissLoading()
            if (TextUtils.isEmpty(it.msg)){
                mView.onAttention(it.code)
            }else{
                toast(it.msg)
            }
            isRequest=false
        })

        mViewModel.ugcDeleteResp.observe(mContext, Observer {
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

    var currentPage=1

    fun queryHtUgcRecommend(topicId:String){
        isRefresh=true
        mViewModel.queryHtUgcRecommend(topicId,1)
    }

    fun queryHtUgcNew(topicId:String){
        isRefresh=true
        mViewModel.queryHtUgcNew(topicId,1)
    }

    fun loadMoreHtUgcRecommend(topicId:String){
        isRefresh=false
        mViewModel.queryHtUgcRecommend(topicId,currentPage+1)
    }


    fun loadMoreHtUgcNew(topicId:String){
        isRefresh=false
        mViewModel.queryHtUgcNew(topicId,currentPage+1)
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
