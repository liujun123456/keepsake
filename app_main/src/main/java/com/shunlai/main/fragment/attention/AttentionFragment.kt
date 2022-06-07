package com.shunlai.main.fragment.attention

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.getui.gs.sdk.GsManager
import com.shunlai.common.bean.GoodsBean
import com.shunlai.common.bean.PublishSuccessEvent
import com.shunlai.common.utils.*
import com.shunlai.common.utils.PreferenceUtil.getString
import com.shunlai.main.ActionInterface
import com.shunlai.main.HomeActivity
import com.shunlai.main.R
import com.shunlai.main.entities.UgcBean
import com.shunlai.main.entities.UgcGoodsBean
import com.shunlai.main.entities.UgcTjBean
import com.shunlai.main.entities.event.GoDiscoverEvent
import com.shunlai.main.fragment.attention.adapter.TuiJAdapter
import com.shunlai.main.fragment.discover.adapter.DiscoverAdapter
import com.shunlai.router.RouterManager
import com.shunlai.ui.MediaGridInset
import com.shunlai.ui.UgcActionWindow
import com.shunlai.ui.srecyclerview.RefreshGridInset
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_attention_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/5/6
 * @mobile 18711832023
 */
class AttentionFragment:Fragment(),AttentionView  , ActionInterface,UgcActionWindow.ActionListener {

    private val mAdapter by lazy {
        DiscoverAdapter(activity!!, mutableListOf(),this,2)
    }

    private val tjAdapter by lazy {
        TuiJAdapter(activity!!, mutableListOf(),this)
    }

    private val mPresenter by lazy {
        AttentionPresenter(viewLifecycleOwner,this,this)
    }

    private val ugcWindow by lazy {
        UgcActionWindow(activity!!,this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        return inflater.inflate(R.layout.fragment_attention_layout,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        mPresenter.queryAttentionUgc()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    private fun initView(){
        rv_attention.setAdapter(mAdapter)
        rv_attention.setLayoutManager( LinearLayoutManager(activity))
        rv_attention.getRecyclerView().addItemDecoration(RefreshGridInset(1, ScreenUtils.dip2px(activity,4f),false,true))
        rv_attention.setSRecyclerListener(object :SRecyclerListener{
            override fun loadMore() {
                mPresenter.loadMoreAttentionUgc()
            }

            override fun refresh() {
                mPresenter.queryAttentionUgc()
            }
        })
        tv_go_discover.setOnClickListener {
            EventBus.getDefault().post(GoDiscoverEvent())
        }

        rv_tui_jian.setAdapter(tjAdapter)
        rv_tui_jian.setLayoutManager(LinearLayoutManager(activity))
        rv_tui_jian.getRecyclerView().addItemDecoration(MediaGridInset(1, ScreenUtils.dip2px(activity,4f),false,true))
        rv_tui_jian.setSRecyclerListener(object :SRecyclerListener{
            override fun loadMore() {

            }

            override fun refresh() {
                mPresenter.queryAttentionUgc()
            }

        })

    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    fun onEvent(event: PublishSuccessEvent){
        //发布成功后 滑动到顶部
        mPresenter.queryAttentionUgc()
        if (mAdapter.mData.isNotEmpty()){
            rv_attention.getRecyclerView().scrollToPosition(0)
        }
    }

    override fun onUgcLoad(mData: MutableList<UgcBean>) {
        ll_have_no_value.visibility=View.GONE
        rv_tui_jian.notifyDataSetChanged()
        rv_tui_jian.visibility=View.GONE
        rv_attention.visibility=View.VISIBLE
        mAdapter.mData=mData
        if (mAdapter.mData.isEmpty()){
            rv_attention.showEmpty()
        }else{
            rv_attention.notifyDataSetChanged()
        }
    }

    override fun onMoreUgcLoad(mData: MutableList<UgcBean>) {
        if (mData.isEmpty()){
            rv_attention.showNoMore()
        }else{
            mAdapter.mData.addAll(mData)
            rv_attention.notifyDataSetChanged()
        }

    }

    override fun onTjLoad(mData: MutableList<UgcTjBean>) {
        if (mData.isNullOrEmpty()){
            ll_have_no_value.visibility=View.VISIBLE
            rv_tui_jian.visibility=View.GONE
            rv_attention.visibility=View.GONE
        }else{
            ll_have_no_value.visibility=View.GONE
            rv_attention.visibility=View.GONE
            rv_tui_jian.visibility=View.VISIBLE
            tjAdapter.mData=mData
            rv_tui_jian.notifyDataSetChanged()
        }
    }

    var actionPosition=-1

    override fun showLoading(value: String) {
        (activity as HomeActivity).showBaseLoading()
    }

    override fun dismissLoading() {
        (activity as HomeActivity).hideBaseLoading()
    }

    override fun onAttention(result: Int) {
        tjAdapter.mData[actionPosition].isFollow=result.toString()
        tjAdapter.notifyDataSetChanged()
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
        if (memberId== getString(Constant.USER_ID)){
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

    var currentTime:Long?=null

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            if (isVisibleToUser){
                trackFollowIn()
            }else{
                currentTime?.let {
                    if (System.currentTimeMillis()-it>1000){
                        trackFollowStay(System.currentTimeMillis()-it)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint){
            currentTime=System.currentTimeMillis()
        }

    }

    override fun onPause() {
        super.onPause()
        if (userVisibleHint){
            currentTime?.let {
                if (System.currentTimeMillis()-it>1000){
                    trackFollowStay(System.currentTimeMillis()-it)
                }
            }
        }
    }

    private fun trackFollowIn(){
        currentTime=System.currentTimeMillis()
        val params = JSONObject()
        params.put("from_page", RunCacheDataUtil.lastPage)
        params.put( "is_login",!TextUtils.isEmpty(getString(Constant.USER_ID)))
        GsManager.getInstance().onEvent("Follow_feed_page", params)
        RunCacheDataUtil.lastPage="Follow"
    }

    private fun trackFollowStay(duration:Long){
        val params = JSONObject()
        params.put("event_duration", duration)
        params.put("page_name","Follow")
        params.put("from_page", RunCacheDataUtil.lastPage)
        params.put( "is_login",!TextUtils.isEmpty(getString(Constant.USER_ID)))
        GsManager.getInstance().onEvent("Follow_feed_stay", params)
    }

    private fun trackUgcLike(position: Int){
        val bean= mAdapter.mData[position]
        val params = JSONObject()
        bean.topic?.id?.let {
            params.put("topic_ids", it)
        }
        params.put("uid",bean.memberId)
        params.put("Page_name","Follow")
        params.put("note_type",bean.ugcType)
        params.put("note_id",bean.ugcId)
        GsManager.getInstance().onEvent("like_note", params)
    }

}
