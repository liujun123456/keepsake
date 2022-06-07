package com.shunlai.mine.shop.edit.fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.shunlai.common.BaseFragment
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.common.utils.toast
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.SceneListBean
import com.shunlai.mine.shop.ShopViewModel
import com.shunlai.mine.shop.edit.adapter.ShopBgAdapter
import com.shunlai.mine.utils.ShopCacheUtil
import com.shunlai.ui.MediaGridInset
import kotlinx.android.synthetic.main.fragment_shop_bg_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/7/14
 * @mobile 18711832023
 */
class EditBgFragment:BaseFragment(),ShopBgAdapter.SceneListener {

    override fun createView(): Int = R.layout.fragment_shop_bg_layout

    override fun createTitle(): Int=0

    private val mAdapter by lazy {
        ShopBgAdapter(mContext, mutableListOf(),this)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(ShopViewModel::class.java)
    }

    override fun afterView() {
        rv_shop_bg.layoutManager= GridLayoutManager(mContext,3)
        rv_shop_bg.addItemDecoration(MediaGridInset(3, ScreenUtils.dip2px(mContext,32f), true,true))
        rv_shop_bg.adapter=mAdapter
        mViewModel.queryAllScene()
        initViewModel()
    }

    private fun initViewModel(){
        mViewModel.sceneListResp.observe(this, Observer {
            mAdapter.sceneList=it
            mAdapter.notifyDataSetChanged()
        })
    }

    override fun onSceneClick(position: Int) {
        if (checkResource(mAdapter.sceneList[position])){
            mAdapter.sceneList.forEach {
                it.selectedFlag="0"
            }
            mAdapter.sceneList[position].selectedFlag="1"
            mAdapter.notifyDataSetChanged()
            EventBus.getDefault().post(mAdapter.sceneList[position])
        }else{
            toast("资源不存在")
        }

    }

    fun checkResource(bean: SceneListBean):Boolean{
        return ShopCacheUtil.getShopBgById(bean.sceneId)!=null
    }
}
