package com.shunlai.publish.search

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.utils.toast
import com.shunlai.publish.PublishViewModel
import com.shunlai.publish.R

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
class ProductSearchPresenter(var mCtx:Context,var mView:ProductSearchView) {

    private val mViewModel by lazy {
        ViewModelProvider(mCtx as FragmentActivity).get(PublishViewModel::class.java)
    }

    init {
        initViewModel()
    }

    var currentPage=1

    var currentSearch=""

    var isRefresh=true

    private fun initViewModel(){
        mViewModel.searchResp.observe(mCtx as FragmentActivity, Observer {
            mView.dismissLoading()
            if (isRefresh){
                if (!it.data.isNullOrEmpty()){
                    currentPage=1
                }
                mView.searchSuccess(it.data?: mutableListOf(),it.total?:"0")
            }else{
                if (!it.data.isNullOrEmpty()){
                    currentPage+=1
                }
                mView.loadMoreSuccess(it.data?: mutableListOf())
            }
        })
    }

    fun searchGoods(value:String){
        isRefresh=true
        mView.showLoading("查询商品中!")
        currentSearch=value
        mViewModel.searchGoods(value,1)
    }

    fun loadMoreGoods(){
        isRefresh=false
        mViewModel.searchGoods(currentSearch,currentPage+1)
    }
}
