package com.shunlai.main.ht.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.getui.gs.sdk.GsManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.GoodsBean
import com.shunlai.common.bean.PublishSuccessEvent
import com.shunlai.common.utils.*
import com.shunlai.main.ActionInterface
import com.shunlai.main.R
import com.shunlai.main.entities.UgcBean
import com.shunlai.main.entities.UgcGoodsBean
import com.shunlai.main.fragment.discover.adapter.DiscoverAdapter
import com.shunlai.main.ht.adapter.HtSkeletonAdapter
import com.shunlai.main.ht.detail.HuaTiDetailActivity
import com.shunlai.publish.PublishViewModel
import com.shunlai.publish.entity.req.PublishReq
import com.shunlai.router.RouterManager
import com.shunlai.ui.QuickPubWindow
import com.shunlai.ui.UgcActionWindow
import com.shunlai.ui.srecyclerview.RefreshGridInset
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_ugc_ht_layout.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class HtUgcFragment(var topicId:String, var type:Int): Fragment() , ActionInterface,HtUgcView,UgcActionWindow.ActionListener,QuickPubWindow.OnQuickPubListener   {

    /**
     *  could not find Fragment constructor
     */
    constructor():this("",0)

    private val mAdapter by lazy {
        DiscoverAdapter(activity!!, mutableListOf(),this,3)
    }

    private val skeletonAdapter by lazy {
        HtSkeletonAdapter(activity!!)
    }

    private val mPresenter by lazy {
        HtUgcPresenter(activity!!,this)
    }

    private val ugcWindow by lazy {
        UgcActionWindow(activity!!,this)
    }

    private val quickPubWindow: QuickPubWindow by lazy {
        QuickPubWindow(activity!!,this)
    }

    private val pubModel by lazy {
        ViewModelProvider(this).get(PublishViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ugc_ht_layout,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initSkeleton()
        if (type==0){
            mPresenter.queryHtUgcNew(topicId)
        }else{
            mPresenter.queryHtUgcRecommend(topicId)
        }
    }

    private fun initSkeleton(){
        rv_skeleton.layoutManager=LinearLayoutManager(activity)
        rv_skeleton.adapter=skeletonAdapter
    }

    private fun initView(){
        rv_attention.setAdapter(mAdapter)
        rv_attention.setLayoutManager(LinearLayoutManager(activity))
        rv_attention.getRecyclerView().addItemDecoration(RefreshGridInset(1, ScreenUtils.dip2px(activity,4f),false,true))
        rv_attention.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                if (type==0){
                    mPresenter.loadMoreHtUgcNew(topicId)
                }else{
                    mPresenter.loadMoreHtUgcRecommend(topicId)
                }
            }

            override fun refresh() {
                if (type==0){
                    mPresenter.queryHtUgcNew(topicId)
                }else{
                    mPresenter.queryHtUgcRecommend(topicId)
                }
            }
        })

        pubModel.publishResp.observe(viewLifecycleOwner, Observer {
            (activity as BaseActivity).hideBaseLoading()
            if (it.isSuccess){
                toast("笔记发布成功!")
                PreferenceUtil.removeValueWithKey(RunIntentKey.PUBLISH_SAVE)
            }else{
                toast(it.errorMsg)
            }
        })
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    fun onEvent(event: PublishSuccessEvent){
        if (type==0&&mAdapter.mData.isNotEmpty()){
            rv_attention.getRecyclerView().scrollToPosition(0)
        }
    }

    var actionPosition=-1

    override fun doAttention(position: Int, memberId: String) {
        actionPosition=position
        mPresenter.doAttention(memberId)
    }

    override fun doLike(position: Int, ugcId: String,isLike:Boolean) {
        actionPosition=position
        mPresenter.doLike(ugcId,isLike)
    }

    override fun doCollect(position: Int, ugcId: String,isCollect:Boolean) {
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
        quickPubWindow.showAtLocation(activity?.window?.decorView, Gravity.NO_GRAVITY,0,0)
        quickPubWindow.setShowGoods(GoodsBean().apply {
            name = bean.productName
            cateId = bean.cateId
            thumb = bean.productImg
            subCateId = bean.subCateId
            price = bean.price
            type = bean.type
            productId = bean.productId
        })
    }

    override fun onNewUgcLoad(mData: MutableList<UgcBean>) {
        if (type==0){
            if (rv_skeleton.visibility==View.VISIBLE){
                rv_skeleton.visibility=View.GONE
                rv_attention.visibility=View.VISIBLE
            }
            mAdapter.mData=mData
            if (mAdapter.mData.isEmpty()){
                rv_attention.showEmpty()
            }else{
                rv_attention.notifyDataSetChanged()
            }

        }
    }

    override fun onNewMoreUgcLoad(mData: MutableList<UgcBean>) {
        if (type==0){
            if (mData.isEmpty()){
                rv_attention.showNoMore()
            }else{
                mAdapter.mData.addAll(mData)
                rv_attention.notifyDataSetChanged()
            }
        }
    }

    override fun onRecommendUgcLoad(mData: MutableList<UgcBean>) {
        if (type==1){
            if (rv_skeleton.visibility==View.VISIBLE){
                rv_skeleton.visibility=View.GONE
                rv_attention.visibility=View.VISIBLE
            }
            mAdapter.mData=mData
            if (mAdapter.mData.isEmpty()){
                rv_attention.showEmpty()
            }else{
                rv_attention.notifyDataSetChanged()
            }
        }
    }

    override fun onMoreRecommendUgcLoad(mData: MutableList<UgcBean>) {
        if (type==1){
            if (mData.isEmpty()){
                rv_attention.showNoMore()
            }else{
                mAdapter.mData.addAll(mData)
                rv_attention.notifyDataSetChanged()
            }
        }
    }

    override fun showLoading(value: String) {
        (activity as HuaTiDetailActivity).showBaseLoading()
    }

    override fun dismissLoading() {
        (activity as HuaTiDetailActivity).hideBaseLoading()
    }

    override fun onAttention(result: Int) {
        mAdapter.mData[actionPosition].isFollow=result.toString()
        rv_attention.notifyItemChanged(actionPosition)
    }

    override fun onLike(result: Int) {
        var likes=mAdapter.mData[actionPosition].likes?:0
        if (result==1){
            trackUgcLike(actionPosition)
            mAdapter.mData[actionPosition].whetherLikes=true
            likes += 1
        }else{
            mAdapter.mData[actionPosition].whetherLikes=false
            likes -= 1
        }
        mAdapter.mData[actionPosition].likes=likes
        rv_attention.notifyItemChanged(actionPosition)
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
        rv_attention.notifyItemChanged(actionPosition)
    }

    override fun onDeleteUgc(result: Int) {
        if (result==1){
            mAdapter.mData.removeAt(actionPosition)
            rv_attention.notifyItemRemoved(actionPosition)
        }
    }

    override fun onDeleteAction() {
        mPresenter.doUgcDelete(actionUgcId?:"")
    }

    override fun onComplaintAction() {
        val params= mutableMapOf<String,Any?>()
        params[RunIntentKey.UGC_ID]=actionUgcId
        RouterManager.startActivityWithParams(BundleUrl.COMPLAIN_ACTIVITY,activity as FragmentActivity,params)
    }


    private fun trackUgcLike(position: Int){
        val bean= mAdapter.mData[position]
        val params = JSONObject()
        bean.topic?.id?.let {
            params.put("topic_ids", it)
        }
        params.put("uid",bean.memberId)
        params.put("Page_name","Topic")
        params.put("note_type",bean.ugcType)
        params.put("note_id",bean.ugcId)
        GsManager.getInstance().onEvent("like_note", params)
    }

    override fun doPublish(goods: GoodsBean, content: String) {
        val req= PublishReq()
        req.ugcType=0
        req.ugcGoods.add(goods.buildUgcGoods())
        req.content=content
        pubModel.publish(req)
        (activity as BaseActivity).showBaseLoading()
    }

    override fun goPublish(goods: GoodsBean, content: String) {
        val params= mutableMapOf<String,Any?>()
        params[RunIntentKey.SIGN_GOODS_LIST] = mutableListOf(goods)
        params[RunIntentKey.UGC_CONTENT] =content
        RouterManager.startActivityWithParams(BundleUrl.PHOTO_NEW_PUBLISH_ACTIVITY,activity!!,
            params)
    }
}
