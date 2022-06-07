package com.shunlai.location.manger

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.location.R
import com.shunlai.location.entity.LocationBean
import com.shunlai.location.manger.adapter.LocationAdapter
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_location_list_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*

/**
 * @author Liu
 * @Date   2021/4/25
 * @mobile 18711832023
 */
class LocationManagerActivity:BaseActivity(),LocationManagerView,
    LocationAdapter.LocalActionListener {
    override fun getMainContentResId(): Int=R.layout.activity_location_list_layout

    override fun getToolBarResID(): Int= R.layout.public_title_layout

    private val mPresenter by lazy {
        LocationManagerPresenter(this,this)
    }

    private val mAdapter by lazy {
        LocationAdapter(mContext, mutableListOf(),this)
    }
    override fun afterView() {
        initTitle()
        initRv()

    }

    override fun onResume() {
        super.onResume()
        mPresenter.queryLocationList()
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
    }

    private fun initRv(){
        rv_loc.layoutManager=LinearLayoutManager(mContext)
        rv_loc.adapter=mAdapter
    }

    fun addAddress(view:View){
        RouterManager.startActivityWithParams(BundleUrl.LOCATION_ADD_OR_UPDATE,this)
    }

    override fun onLocationList(locs: MutableList<LocationBean>) {
        if (locs.isEmpty()){
            tv_page_name.visibility= View.GONE
            tv_add_new_loc.visibility=View.GONE
            ll_add_address.visibility=View.VISIBLE
        }else{
            tv_page_name.visibility= View.VISIBLE
            tv_add_new_loc.visibility=View.VISIBLE
            ll_add_address.visibility=View.GONE
        }
        mAdapter.mData=locs
        mAdapter.notifyDataSetChanged()
    }

    override fun showLoading(value: String) {
       showBaseLoading()
    }

    override fun hideLoading() {
       hideBaseLoading()
    }

    override fun onRemoveResult(boolean: Boolean) {
        if (boolean){
            mAdapter.mData.removeAt(currentPosition)
            mAdapter.notifyItemRemoved(currentPosition)
            if (mAdapter.mData.size==0){
                tv_page_name.visibility= View.GONE
                tv_add_new_loc.visibility=View.GONE
                ll_add_address.visibility=View.VISIBLE
            }
        }
    }

    override fun onDefaultResult(boolean: Boolean) {
        if (boolean){
            mPresenter.queryLocationList()
        }
    }

    var currentPosition:Int=0

    override fun onRemoveLoc(bean: LocationBean, position: Int) {
        currentPosition=position
        mPresenter.deleteLocation(bean.id.toString())
    }

    override fun setDefault(bean: LocationBean) {
        mPresenter.setDefaultLocation(bean.id.toString())
    }

    override fun onItemEdit(bean: LocationBean) {
        val params= mutableMapOf<String,Any?>()
        params["addressReq"]=bean.buildLocReq()
        RouterManager.startActivityWithParams(BundleUrl.LOCATION_ADD_OR_UPDATE,this,params)
    }

    override fun onItemClick(bean: LocationBean) {
        finish()
    }
}
