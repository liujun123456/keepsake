package com.shunlai.main.fragment.attention

import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.shunlai.common.utils.toast
import com.shunlai.main.HomeViewModel

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class AttentionPresenter(var life: LifecycleOwner, var owner: ViewModelStoreOwner, var mView:AttentionView) {

    private val mViewModel by lazy {
        ViewModelProvider(owner).get(HomeViewModel::class.java)
    }

    init {
        initViewModel()
    }

    private var isRequest=false

    private var isRefresh=true

    private fun initViewModel(){
        mViewModel.homeUgcAttentionResp.observe(life, Observer {
            if (isRefresh){
                currentPage=1
                if (it.isNullOrEmpty()){
                    queryTj()
                }else{
                    mView.onUgcLoad(it)
                }
            }else{
                if (!it.isNullOrEmpty()){
                    currentPage+=1
                }
                mView.onMoreUgcLoad(it)
            }
        })
        mViewModel.tjUgcBean.observe(life, Observer {
            mView.onTjLoad(it)
        })

        mViewModel.attentionResp.observe(life, Observer {
            if (!isRequest)return@Observer
            mView.dismissLoading()
            if (TextUtils.isEmpty(it.msg)){
                mView.onAttention(it.code)
            }else{
                toast(it.msg)
            }
            isRequest=false
        })

        mViewModel.ugcDeleteResp.observe(life, Observer {
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

    fun queryAttentionUgc(){
        isRefresh=true
        mViewModel.queryHomeAttentionUgc(currentPage)
    }

    fun loadMoreAttentionUgc(){
        isRefresh=false
        mViewModel.queryHomeAttentionUgc(currentPage+1)
    }

    private fun queryTj(){
        mViewModel.queryHomeTj()
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
