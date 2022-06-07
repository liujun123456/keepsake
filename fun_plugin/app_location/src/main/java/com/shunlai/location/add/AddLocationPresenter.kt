package com.shunlai.location.add

import android.content.Context
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.toast
import com.shunlai.location.LocationViewModel
import com.shunlai.location.entity.ProvinceBean
import com.shunlai.location.entity.req.AddAddressReq

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
class AddLocationPresenter(var mContext:Context,var mView:AddLocationView) {
    private val mViewModel by lazy {
        ViewModelProvider(mContext as FragmentActivity).get(LocationViewModel::class.java)
    }

    var addAddressReq= AddAddressReq()

    init {
        initViewModel()
        addAddressReq=(mContext as FragmentActivity).intent.getParcelableExtra<AddAddressReq>("addressReq")?:AddAddressReq()
        mView.injectData(addAddressReq)
    }

    var mProvinces= mutableListOf<ProvinceBean>()
    var chooseProvince:ProvinceBean?=null
    var chooseCity:ProvinceBean?=null
    var chooseArea:ProvinceBean?=null


    private fun initViewModel(){
        mViewModel.provinceResp.observe(mContext as FragmentActivity, Observer {
            mProvinces=it?: mutableListOf()
        })
        mViewModel.addAddressResp.observe(mContext as FragmentActivity, Observer {
            mView.hideLoading()
            if (it.isSuccess){
                toast("请求成功")
                (mContext as FragmentActivity).finish()
            }else{
                toast(it.errorMsg)
            }
        })
    }

    fun onOptionsSelect(options1: Int, options2: Int, options3: Int){
        chooseProvince=null
        chooseCity=null
        chooseArea=null
        if (mProvinces.size>options1){
            chooseProvince=mProvinces[options1]

            addAddressReq.provinceId=chooseProvince?.id
            addAddressReq.provinceName=chooseProvince?.name
        }
        if (chooseProvince!=null){
            if (chooseProvince?.children?.size?:0>options2){
                chooseCity= chooseProvince?.children?.get(options2)

                addAddressReq.cityId=chooseCity?.id
                addAddressReq.cityName=chooseCity?.name
            }
        }

        if (chooseCity!=null){
            if (chooseCity?.children?.size?:0>options3){
                chooseArea= chooseCity?.children?.get(options3)

                addAddressReq.areaId=chooseArea?.id
                addAddressReq.area=chooseArea?.name
            }
        }
    }

    fun getProvince(){
        mViewModel.getProvince()
    }

    fun doCommit(name:String,phone:String,address:String,isDefault:Int){
        if (TextUtils.isEmpty(name)){
            toast("姓名不能为空")
            return
        }
        if (TextUtils.isEmpty(phone)||phone.length!=11){
            toast("请输入正确的手机号")
            return
        }
        if (TextUtils.isEmpty(address)){
            toast("详细地址不能为空")
            return
        }
        addAddressReq.contact=name
        addAddressReq.phone=phone
        addAddressReq.street=address
        addAddressReq.isDefault=isDefault
        addAddressReq.memberId= PreferenceUtil.getString(Constant.USER_ID)?:""
        mView.showLoading("请求中")
        mViewModel.addAddress(addAddressReq)
    }
}
