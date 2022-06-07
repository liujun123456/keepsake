package com.shunlai.location

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.location.entity.BaseResp
import com.shunlai.location.entity.LocationBean
import com.shunlai.location.entity.ProvinceBean
import com.shunlai.location.entity.req.AddAddressReq
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class LocationViewModel: ViewModel()  {
    private val lifecycleOwner: LifecycleOwner?=null
    private val memberId= PreferenceUtil.getString(Constant.USER_ID)?:""
    val locResp:MutableLiveData<MutableList<LocationBean>> = MutableLiveData()
    val provinceResp:MutableLiveData<MutableList<ProvinceBean>> = MutableLiveData()
    val addAddressResp:MutableLiveData<BaseResp> = MutableLiveData()
    val deleteResp:MutableLiveData<BaseResp> = MutableLiveData()
    val setDefaultResp:MutableLiveData<BaseResp> = MutableLiveData()


    fun getProvince(){
        LocationHttpManager.getByParams(lifecycleOwner,LocationApiConfig.GET_PROVINCES, mutableMapOf())
            .subscribe(object :CoreHttpSubscriber<MutableList<ProvinceBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    provinceResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: MutableList<ProvinceBean>?) {
                    provinceResp.postValue(t?: mutableListOf())
                }

            })
    }

    fun addAddress(req:AddAddressReq){
        LocationHttpManager.postByModel(lifecycleOwner,LocationApiConfig.ADD_ADDRESS, req)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    addAddressResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    addAddressResp.postValue(BaseResp())
                }

            })
    }

    fun queryAddressList(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        LocationHttpManager.getByParams(lifecycleOwner,LocationApiConfig.ADDRESS_LIST, params)
            .subscribe(object :CoreHttpSubscriber<MutableList<LocationBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    locResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: MutableList<LocationBean>?) {
                    locResp.postValue(t?: mutableListOf())
                }

            })
    }

    fun deleteAddress(addressId:String){
        val params= mutableMapOf<String,Any>()
        params["addressId"]=addressId
        LocationHttpManager.getByParams(lifecycleOwner,LocationApiConfig.DELETE_ADDRESS, params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    deleteResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    deleteResp.postValue(BaseResp())
                }

            })
    }

    fun defaultAddress(addressId:String){
        val params= mutableMapOf<String,Any>()
        params["addressId"]=addressId
        LocationHttpManager.getByParams(lifecycleOwner,LocationApiConfig.ADDRESS_DEFAULT, params)
            .subscribe(object :CoreHttpSubscriber<BaseResp>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    setDefaultResp.postValue(BaseResp().apply {
                        buildError(throwable.msg)
                    })
                }

                override fun onSuccess(t: BaseResp?) {
                    setDefaultResp.postValue(BaseResp())
                }

            })
    }
}
