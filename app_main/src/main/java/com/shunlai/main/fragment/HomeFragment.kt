package com.shunlai.main.fragment

import android.text.TextUtils
import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.getui.gs.sdk.GsManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.BaseFragment
import com.shunlai.common.bean.GoodsBean
import com.shunlai.common.bean.PublishSuccessEvent
import com.shunlai.common.bean.SavePublishBean
import com.shunlai.common.utils.*
import com.shunlai.main.HomePagerAdapter
import com.shunlai.main.HomeViewModel
import com.shunlai.main.R
import com.shunlai.main.entities.event.GoDiscoverEvent
import com.shunlai.net.util.GsonUtil
import com.shunlai.publish.PublishViewModel
import com.shunlai.publish.entity.req.PublishReq
import com.shunlai.router.RouterManager
import com.shunlai.ui.QuickPubWindow
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/4/2
 * @mobile 18711832023
 */
class HomeFragment:BaseFragment(), QuickPubWindow.OnQuickPubListener  {

    override fun createView(): Int= R.layout.fragment_home

    override fun createTitle(): Int =0

    private val mViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private val pubModel by lazy {
        ViewModelProvider(this).get(PublishViewModel::class.java)
    }

    private val quickPubWindow:QuickPubWindow by lazy {
        QuickPubWindow(activity!!,this)
    }

    private val channels = mutableListOf<String>()

    private val mAdapter by lazy {
        HomePagerAdapter(childFragmentManager,channels)
    }

    override fun afterView() {
        EventBus.getDefault().register(this)
        initListener()
        initViewModel()
        mViewModel.getUgcChannels()
    }


    private fun initListener(){
        vp_home.adapter= mAdapter
        vp_home.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }
        })
        tab_ugc.setupWithViewPager(vp_home)

        rl_search.setOnClickListener {
            RouterManager.startActivityWithParams(BundleUrl.UGC_SEARCH_ACTIVITY,mContext as FragmentActivity)
        }
        go_publish.setOnClickListener {
            goPublish()
        }
    }

    private fun initViewModel(){
        mViewModel.ugcChannels.observe(this, Observer {
            if (it.isNotEmpty()){
                channels.clear()
                channels.addAll(it)
                mAdapter.notifyDataSetChanged()
                vp_home.offscreenPageLimit= it.size+2
            }
        })
        pubModel.publishResp.observe(this, Observer {
            (activity as BaseActivity).hideBaseLoading()
            if (it.isSuccess){
                toast("笔记发布成功!")
                PreferenceUtil.removeValueWithKey(RunIntentKey.PUBLISH_SAVE)
                EventBus.getDefault().post(PublishSuccessEvent())
            }else{
                toast(it.errorMsg)
            }
        })
    }


    @Subscribe(threadMode= ThreadMode.MAIN)
    fun onEvent(event:GoDiscoverEvent){
        vp_home.currentItem=0
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    fun onEvent(event:PublishSuccessEvent){
        //发布成功后跳转到关注
        vp_home.currentItem=1
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    fun onEvent(event:GoodsBean){
        quickPubWindow.showAtLocation(activity?.window?.decorView, Gravity.NO_GRAVITY,0,0)
        quickPubWindow.setShowGoods(event)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }


    private fun goPublish(){
        RunCacheDataUtil.cleanHtCache()
        val data= PreferenceUtil.getString(RunIntentKey.PUBLISH_SAVE)
        val params= mutableMapOf<String,Any?>()
        if (!TextUtils.isEmpty(data)){
            val bean= GsonUtil.fromJson(data!!, SavePublishBean::class.java)
            params[RunIntentKey.CHOOSE_IMAGE_ITEM] = bean.selectImage
            params[RunIntentKey.SIGN_GOODS_LIST] =bean.signGoods
            params[RunIntentKey.UGC_CONTENT]=bean.content
            params[RunIntentKey.UGC_HT]=bean.ht
        }
        RouterManager.startActivityWithParams(BundleUrl.PHOTO_NEW_PUBLISH_ACTIVITY,activity!!,
            params)
        sensorsTrackPublish()
    }

    /**
     * 神策--发布按钮事件
     */
    private fun sensorsTrackPublish(){
        val params= JSONObject()
        params.put("page_name","Home")
        GsManager.getInstance().onEvent("CreateNoteClick",params)
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
