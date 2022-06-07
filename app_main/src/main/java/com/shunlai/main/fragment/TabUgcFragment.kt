package com.shunlai.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.bean.GoodsBean
import com.shunlai.common.utils.*
import com.shunlai.main.ActionInterface
import com.shunlai.main.HomeActivity
import com.shunlai.main.R
import com.shunlai.main.entities.HuaTiBean
import com.shunlai.main.entities.UgcBean
import com.shunlai.main.entities.UgcGoodsBean
import com.shunlai.main.fragment.discover.DiscoverPresenter
import com.shunlai.main.fragment.discover.DiscoverView
import com.shunlai.main.fragment.discover.adapter.DiscoverAdapter
import com.shunlai.router.RouterManager
import com.shunlai.ui.UgcActionWindow
import com.shunlai.ui.srecyclerview.RefreshGridInset
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_tab_ugc_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/9/22
 * @mobile 18711832023
 */
class TabUgcFragment(var keyWord:String):Fragment(), ActionInterface, UgcActionWindow.ActionListener,
    DiscoverView {


    private val mAdapter by lazy {
        DiscoverAdapter(activity!!, mutableListOf(),this,1)
    }

    private val mPresenter by lazy {
        DiscoverPresenter(viewLifecycleOwner,this,this)
    }

    private val ugcWindow by lazy {
        UgcActionWindow(activity!!,this)
    }


    constructor():this("")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab_ugc_layout,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        mPresenter.queryHomeUgc( buildUgcList(),keyWord)
    }

    private fun initView(){
        initRv()
    }

    private fun initRv(){
        tv_tab_recycler.setAdapter(mAdapter)
        tv_tab_recycler.setLayoutManager(LinearLayoutManager(activity))
        tv_tab_recycler.getRecyclerView().addItemDecoration(
            RefreshGridInset(1,
                ScreenUtils.dip2px(activity,8f),false,true)
        )

        tv_tab_recycler.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                mPresenter.loadMoreHomeUgc( buildUgcList(),keyWord)
            }

            override fun refresh() {
                mPresenter.queryHomeUgc( buildUgcList(),keyWord)
            }
        })
    }


    private fun buildUgcList():MutableList<Int>{
        val params= mutableListOf<Int>()
        mAdapter.mData.forEach {
            try {
                params.add((it.ugcId?:"0").toInt())
            }catch (e:Exception){

            }
        }
        return params
    }

    override fun onHomeRecommendUgc(data: MutableList<UgcBean>) {
        mAdapter.mData.addAll(0,data)
        if (mAdapter.mData.isEmpty()){
            tv_tab_recycler.showEmpty()
        }else{
            tv_tab_recycler.notifyItemRangeInserted(0,data.size)
        }
    }

    override fun onMoreHomeRecommendUgc(data: MutableList<UgcBean>) {
        if (data.isEmpty()){
            tv_tab_recycler.showNoMore()
        }else{
            mAdapter.mData.addAll(data)
            tv_tab_recycler.notifyDataSetChanged()
        }
    }

    var actionPosition=-1

    override fun doAttention(position: Int, memberId: String) {
        actionPosition=position
        mPresenter.doAttention(memberId)
    }

    override fun doLike(position: Int, ugcId: String, isLike: Boolean) {
        actionPosition=position
        mPresenter.doLike(ugcId,isLike)
    }

    override fun doCollect(position: Int, ugcId: String, isCollect: Boolean) {
        actionPosition=position
        mPresenter.doCollect(ugcId,isCollect)
    }

    var actionUgcId:String?=null
    override fun doMore(position: Int, ugcId: String, memberId: String) {
        actionPosition=position
        actionUgcId=ugcId
        if (memberId== PreferenceUtil.getString(Constant.USER_ID)){
            ugcWindow.showWindow(1)
        }else{
            ugcWindow.showWindow(0)
        }
    }

    override fun doEva(bean: UgcGoodsBean) {
        EventBus.getDefault().post(GoodsBean().apply {
            name = bean.productName
            cateId = bean.cateId
            thumb = bean.productImg
            subCateId = bean.subCateId
            price = bean.price
            type = bean.type
            productId = bean.productId
        })
    }

    override fun onDeleteAction() {
        mPresenter.doUgcDelete(actionUgcId?:"")
    }

    override fun onComplaintAction() {
        val params= mutableMapOf<String,Any?>()
        params[RunIntentKey.UGC_ID]=actionUgcId
        RouterManager.startActivityWithParams(BundleUrl.COMPLAIN_ACTIVITY,activity as FragmentActivity,params)
    }

    override fun onHtLoad(data: MutableList<HuaTiBean>) {

    }


    override fun showLoading(value: String) {
        (activity as HomeActivity).showBaseLoading()
    }

    override fun dismissLoading() {
        (activity as HomeActivity).hideBaseLoading()
    }

    override fun onAttention(result: Int) {
        mAdapter.mData[actionPosition].isFollow=result.toString()
        tv_tab_recycler.notifyItemChanged(actionPosition)
    }

    override fun onLike(result: Int) {
        var likes=mAdapter.mData[actionPosition].likes?:0
        if (result==1){
            mAdapter.mData[actionPosition].whetherLikes=true
            likes += 1
        }else{
            mAdapter.mData[actionPosition].whetherLikes=false
            likes -= 1
        }
        mAdapter.mData[actionPosition].likes=likes
        tv_tab_recycler.notifyItemChanged(actionPosition)
    }

    override fun onCollect(result: Int) {
        var collects= mAdapter.mData[actionPosition].favorites?:0
        if (result==1){
            mAdapter.mData[actionPosition].whetherFavorites=true
            collects+=1
        }else{
            mAdapter.mData[actionPosition].whetherFavorites=false
            collects-=1
        }
        mAdapter.mData[actionPosition].favorites=collects
        tv_tab_recycler.notifyItemChanged(actionPosition)
    }

    override fun onDeleteUgc(result: Int) {
        if (result==1){
            mAdapter.mData.removeAt(actionPosition)
            tv_tab_recycler.notifyItemRemoved(actionPosition)
        }
    }
}
