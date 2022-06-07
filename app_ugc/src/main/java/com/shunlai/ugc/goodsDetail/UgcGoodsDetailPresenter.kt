package com.shunlai.ugc.goodsDetail

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.utils.toast
import com.shunlai.ugc.UgcViewModel
import com.shunlai.ugc.entity.BuildGoodsOrderReq

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class UgcGoodsDetailPresenter(var mContext: Context, var mView:UgcGoodsDetailView) {
    private val mViewModel by lazy {
        ViewModelProvider(mContext as FragmentActivity).get(UgcViewModel::class.java)
    }

    init {
        initViewModel()
    }

    private fun initViewModel(){
        mViewModel.ugcGoodsDetailResp.observe(mContext as FragmentActivity, Observer {
            if (it.isSuccess){
                mView.imageDetail(it.contentImages?: mutableListOf())
                mView.buildProductInfo(it)
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.ugcCommentResp.observe(mContext as FragmentActivity, Observer {
            if (it.isSuccess){
                mView.buildProductComment(it.data?: mutableListOf(),it.total_records?:0)
            }
        })
        mViewModel.shareResp.observe(mContext as FragmentActivity, Observer {
            mView.dismissLoading()
            mView.doShareResult(it.isSuccess,it.imgUrl,it.errorMsg)

        })
    }

    fun queryUgcGoodsDetail(productId: String,ugcId:String){
        mViewModel.getGoodsDetail(productId,ugcId)
    }

    fun queryProductComment(productId: String){
        mViewModel.queryGoodsComment(productId,1,1,5)
    }

    fun buildPost(ugcId:String,shopMemberId:String,productId:String,goodType:String){
        mView.showLoading("分享中")
        mViewModel.buildPoster(ugcId,shopMemberId,productId,goodType)
    }
}
