package com.shunlai.location.add

import android.annotation.SuppressLint
import android.view.View
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.shunlai.common.BaseActivity
import com.shunlai.location.R
import com.shunlai.location.entity.req.AddAddressReq
import kotlinx.android.synthetic.main.activity_update_add_location_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*
import org.jetbrains.anko.toast

/**
 * @author Liu
 * @Date   2021/4/25
 * @mobile 18711832023
 */
class AddOrUpdateLocationActivity:BaseActivity(),AddLocationView, OnOptionsSelectListener {
    override fun getMainContentResId(): Int= R.layout.activity_update_add_location_layout

    override fun getToolBarResID(): Int = R.layout.public_title_layout

    private val mPresenter by lazy {
        AddLocationPresenter(mContext,this)
    }

    override fun afterView() {
        initTitle()
        mPresenter.getProvince()
    }

    private val pvOptions by lazy {
        OptionsPickerBuilder(mContext,this).build<String>()
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
    }


    fun showProvinceSelect(view:View){
        hideInput(view)
        if (mPresenter.mProvinces.isEmpty()){
            toast("无法获取省市区信息")
            return
        }
        val mProvinceString = mutableListOf<String>()
        val mCityString= mutableListOf<MutableList<String>>()
        val mAreaString:MutableList<MutableList<MutableList<String>>> = mutableListOf()
        mPresenter.mProvinces.forEach {
            mProvinceString.add(it.name?:"")  //初始化省数据
            val mCitiesStringChild = mutableListOf<String>()

            val mAreaStringChild = mutableListOf<MutableList<String>>()
            it.children?.forEach {data->
                mCitiesStringChild.add(data.name?:"")
                val mCitiesStringChildChild = mutableListOf<String>()
                data.children?.forEach {childData->
                    mCitiesStringChildChild.add(childData.name?:"")
                }
                mAreaStringChild.add(mCitiesStringChildChild)
            }
            mAreaString.add(mAreaStringChild)
            mCityString.add(mCitiesStringChild) //初始化市数据
        }
        pvOptions.setPicker(mProvinceString,mCityString,mAreaString)
        pvOptions.show()
    }

    fun commit(view:View){
        mPresenter.doCommit(et_name.text.toString(),
            et_phone.text.toString(),et_detail_address.text.toString(),if (sb_default.isChecked) 1 else 0)
    }

    override fun showLoading(value: String) {
        showBaseLoading()
    }

    override fun hideLoading() {
       hideBaseLoading()
    }

    @SuppressLint("SetTextI18n")
    override fun injectData(req: AddAddressReq) {
        et_name.setText(req.contact)
        et_phone.setText(req.phone)
        tv_address.text = req.provinceName+req.cityName+req.area
        et_detail_address.setText(req.street)
        sb_default.isChecked = req.isDefault != 0
    }

    @SuppressLint("SetTextI18n")
    override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View?) {
        mPresenter.onOptionsSelect(options1,options2,options3)
        tv_address.text=mPresenter.addAddressReq.provinceName+mPresenter.addAddressReq.cityName+mPresenter.addAddressReq.area
    }
}
