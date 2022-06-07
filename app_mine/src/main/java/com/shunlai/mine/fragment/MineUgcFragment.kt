package com.shunlai.mine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.getui.gs.sdk.GsManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.GoodsBean
import com.shunlai.common.utils.*
import com.shunlai.mine.MineActionInterface
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.UgcBean
import com.shunlai.mine.entity.bean.UgcGoodsBean
import com.shunlai.mine.fragment.adapter.MineUgcAdapter
import com.shunlai.router.RouterManager
import com.shunlai.ui.UgcActionWindow
import com.shunlai.ui.srecyclerview.RefreshGridInset
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_mine_ugc_layout.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/5/17
 * @mobile 18711832023
 */
class MineUgcFragment:ThemeFragment(), UgcActionWindow.ActionListener,MineActionInterface {

    private val mAdapter by lazy {
        MineUgcAdapter(activity!!, mutableListOf(),type!!,skinBean,this,memberId==PreferenceUtil.getString(Constant.USER_ID))
    }
    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }
    private val ugcAction by lazy {
        UgcActionWindow(activity!!,this)
    }

    private var memberId:String?=null

    var currentPage:Int=1

    var isRefresh=true

    var type:Int?=1   //1用户发布 2用户喜欢 3用户收藏

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return View.inflate(activity!!, R.layout.fragment_mine_ugc_layout,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type=arguments?.getInt("fragmentType",1)
        memberId=arguments?.getString(RunIntentKey.MEMBER_ID)
        initView()
        initViewModel()
        doRequest(1)
    }

    private fun initView(){
        rv_mine_ugc.setAdapter(mAdapter)
        rv_mine_ugc.setLayoutManager(LinearLayoutManager(activity))
        rv_mine_ugc.getRecyclerView().addItemDecoration(
            RefreshGridInset(1,
                ScreenUtils.dip2px(activity,12f),false,true)
        )
        rv_mine_ugc.isCanRefresh=false
        rv_mine_ugc.setSRecyclerListener(object : SRecyclerListener{
            override fun loadMore() {
                doRequest(currentPage+1)
            }

            override fun refresh() {

            }
        })
    }

    private fun doRequest(page:Int){
        isRefresh=page==1
        if (type==1){
            mViewModel.queryUserUgc(page,memberId)
        }else if (type==2){
            mViewModel.queryUserLikeUgc(page,memberId)
        }else if (type==3){
            mViewModel.queryUserCollectUgc(page,memberId)
        }

    }

    private fun initViewModel(){
        mViewModel.mineUgcResp.observe(viewLifecycleOwner, Observer {
            dealData(it)
        })
        mViewModel.userLikeUgcResp.observe(viewLifecycleOwner, Observer {
            dealData(it)
        })
        mViewModel.userCollectUgcResp.observe(viewLifecycleOwner, Observer {
            dealData(it)
        })
        mViewModel.ugcDeleteResp.observe(viewLifecycleOwner, Observer {
            (activity as BaseActivity).hideBaseLoading()
            if (it.isSuccess){
                mAdapter.mData.removeAt(actionPosition)
                if ( mAdapter.mData.isNullOrEmpty()){
                    rv_mine_ugc.showEmpty()
                }else{
                    rv_mine_ugc.notifyDataSetChanged()
                }
            }else{
                toast(it.errorMsg)
            }
        })
    }

    private fun dealData(it:MutableList<UgcBean>){
        if (isRefresh){
            mAdapter.mData=it
            if (it.isEmpty()){
                rv_mine_ugc.showEmpty()
            }else{
                rv_mine_ugc.notifyDataSetChanged()
                currentPage=1
            }
        }else{
            mAdapter.mData.addAll(it)
            if (it.isEmpty()){
                rv_mine_ugc.showNoMore()
            }else{
                rv_mine_ugc.notifyDataSetChanged()
                currentPage+=1
            }

        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun notifyTheme() {
        activity?.let {
            mAdapter.skin=skinBean
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onDeleteAction() {
        (activity as BaseActivity).showBaseLoading()
        mViewModel.deleteUgc(actionUgcId?:"")
    }

    override fun onComplaintAction() {
        val params= mutableMapOf<String,Any?>()
        params[RunIntentKey.UGC_ID]=actionUgcId
        RouterManager.startActivityWithParams(BundleUrl.COMPLAIN_ACTIVITY,activity as FragmentActivity,params)
    }

    override fun doAttention(position: Int, memberId: String) {

    }

    override fun doLike(position: Int, ugcId: String, isLike: Boolean) {
        var likes= mAdapter.mData[position].likes?:0
        mAdapter.mData[position].whetherLikes = !isLike
        if (isLike){
            likes-=1
        }else{
            likes+=1
            trackUgcLike(position)
        }
        mAdapter.mData[position].likes=likes
        rv_mine_ugc.notifyItemChanged(position)
        mViewModel.doLike(ugcId)
    }

    override fun doCollect(position: Int, ugcId: String, isCollect: Boolean) {
        var collects= mAdapter.mData[position].favorites?:0
        mAdapter.mData[position].whetherFavorites = !isCollect
        if (isCollect){
            collects-=1
        }else{
            collects+=1
        }
        mAdapter.mData[position].favorites=collects
        rv_mine_ugc.notifyItemChanged(position)
        mViewModel.doCollect(ugcId)
    }

    var actionUgcId:String?=null
    var actionPosition=-1
    override fun doMore(position: Int, ugcId: String, memberId: String) {
        actionPosition=position
        actionUgcId=ugcId
        if (memberId== PreferenceUtil.getString(Constant.USER_ID)){
            ugcAction.showWindow(1)
        }else{
            ugcAction.showWindow(0)
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

    private fun trackUgcLike(position: Int){
        val bean= mAdapter.mData[position]
        val params = JSONObject()
        bean.topic?.id?.let {
            params.put("topic_ids", it)
        }
        params.put("uid",bean.memberId)
        if (memberId==PreferenceUtil.getString(Constant.USER_ID)){
            params.put("Page_name","My_Profile")
        }else{
            params.put("Page_name","Others_profile")
        }
        params.put("note_type",bean.ugcType)
        params.put("note_id",bean.ugcId)
        GsManager.getInstance().onEvent("like_note", params)
    }

}
