package com.shunlai.mine.shop.edit.fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.shunlai.common.BaseFragment
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.common.utils.toast
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.DollListBean
import com.shunlai.mine.shop.ShopViewModel
import com.shunlai.mine.shop.edit.adapter.ShopImpressionAdapter
import com.shunlai.mine.utils.ShopCacheUtil
import com.shunlai.ui.MediaGridInset
import kotlinx.android.synthetic.main.fragment_edit_impression_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/7/14
 * @mobile 18711832023
 */
class EditImpressionFragment:BaseFragment(),ShopImpressionAdapter.DollListener {

    override fun createView(): Int= R.layout.fragment_edit_impression_layout

    override fun createTitle(): Int=0

    private val mAdapter by lazy {
        ShopImpressionAdapter(mContext, mutableListOf(),this)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(ShopViewModel::class.java)
    }

    override fun afterView() {
        rv_impression_bg.layoutManager=GridLayoutManager(mContext,3)
        rv_impression_bg.addItemDecoration(MediaGridInset(3, ScreenUtils.dip2px(mContext,32f), true,true))
        rv_impression_bg.adapter=mAdapter
        mViewModel.queryAllDoll()
        initViewModel()
    }
    private fun initViewModel(){
        mViewModel.dollListResp.observe(this, Observer {
            mAdapter.mDollList=it
            mAdapter.notifyDataSetChanged()
        })
    }

    override fun onDollClick(position: Int) {
        if (checkResource(mAdapter.mDollList[position])){
            mAdapter.mDollList.forEach {
                it.selectedFlag="0"
            }
            mAdapter.mDollList[position].selectedFlag="1"
            mAdapter.notifyDataSetChanged()
            EventBus.getDefault().post(mAdapter.mDollList[position])
        }else{
            toast("资源不存在")
        }

    }

    private fun checkResource(bean: DollListBean):Boolean{
        return ShopCacheUtil.getShopDollById(bean.modelId)!=null
    }
}
