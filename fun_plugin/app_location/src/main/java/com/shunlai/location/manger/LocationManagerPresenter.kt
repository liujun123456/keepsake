package com.shunlai.location.manger

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.utils.toast
import com.shunlai.location.LocationViewModel

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
class LocationManagerPresenter(var mContext:Context,var mView:LocationManagerView) {

    private val mViewModel by lazy {
        ViewModelProvider(mContext as FragmentActivity).get(LocationViewModel::class.java)
    }

    init {
        initViewModel()
    }

    private fun initViewModel(){
        mViewModel.locResp.observe(mContext as FragmentActivity, Observer {
            mView.onLocationList(it?: mutableListOf())
        })
        mViewModel.deleteResp.observe(mContext as FragmentActivity, Observer {
            mView.hideLoading()
            if (!it.isSuccess){
                toast(it.errorMsg)
            }
            mView.onRemoveResult(it.isSuccess)
        })
        mViewModel.setDefaultResp.observe(mContext as FragmentActivity, Observer {
            mView.hideLoading()
            if (!it.isSuccess){
                toast(it.errorMsg)
            }
            mView.onDefaultResult(it.isSuccess)
        })
    }

    fun queryLocationList(){
        mViewModel.queryAddressList()
    }

    fun deleteLocation(addressId:String){
        mView.showLoading("删除中")
        mViewModel.deleteAddress(addressId)
    }

    fun setDefaultLocation(addressId:String){
        mView.showLoading("设置中")
        mViewModel.defaultAddress(addressId)
    }

}
