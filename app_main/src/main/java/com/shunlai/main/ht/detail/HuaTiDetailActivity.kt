package com.shunlai.main.ht.detail

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.PublishSuccessEvent
import com.shunlai.common.utils.*
import com.shunlai.main.HomeApiConfig
import com.shunlai.main.HomeViewModel
import com.shunlai.main.R
import com.shunlai.main.behavior.HtDetailHeadBehavior
import com.shunlai.main.entities.HtActivityDetail
import com.shunlai.main.entities.HuaTiBean
import com.shunlai.main.ht.adapter.HuaTiPagerAdapter
import com.shunlai.main.ht.fragment.HtUgcFragment
import com.shunlai.net.util.GsonUtil
import com.shunlai.common.bean.SavePublishBean
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_hua_ti_details_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author Liu
 * @Date   2021/5/8
 * @mobile 18711832023
 */
class HuaTiDetailActivity:BaseActivity() {
    override fun getMainContentResId(): Int =R.layout.activity_hua_ti_details_layout

    override fun getToolBarResID(): Int=0

    private val htBean by lazy {
        GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.HT_DETAIL)?:"",HuaTiBean::class.java)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private val mPagerAdapter by lazy {
        HuaTiPagerAdapter(supportFragmentManager, arrayListOf(HtUgcFragment(htBean.id?:"",0), HtUgcFragment(htBean.id?:"",1)))
    }

    private var mBehavior: HtDetailHeadBehavior?=null

    override fun afterView() {
        StatusBarUtil.showLightStatusBarIcon(this)

        if (htBean==null){
            AlertDialog.Builder(mContext).setTitle("提示").setMessage("话题详情异常")
                .setPositiveButton("确认") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }.show()
            return
        }

        EventBus.getDefault().register(this)
        mBehavior=(ll_behavior.layoutParams as CoordinatorLayout.LayoutParams).behavior as HtDetailHeadBehavior
        initData()
        initListener()
        hua_ti_pager.adapter=mPagerAdapter
        hua_ti_tab.setupWithViewPager(hua_ti_pager)
    }

    @SuppressLint("SetTextI18n")
    private fun initData(){
        ImageUtil.showCropImgWithString(iv_ht_bg,mContext,htBean.homeImgUrl?:"")
        tv_hua_ti_style.text=htBean.tag
        tv_hua_ti_hot.text="${htBean.pnum?:"0"} 人参与"
        if (htBean.activity==true){
            mViewModel.queryDetailActivity(htBean.id?:"")
            ht_activity_skeleton.visibility=View.VISIBLE
            tv_division.visibility=View.VISIBLE
        }else{
            ll_behavior.postDelayed({  mBehavior?.doInit() },500)
        }

    }

    private fun initHtActivity(detail: HtActivityDetail?){
        detail?.let {
            ht_activity_skeleton.visibility=View.GONE
            tv_html.visibility=View.VISIBLE
            tv_html.text= detail.activityIntroduction
            ll_hot_billboard.visibility=View.VISIBLE
            detail.avatarList?.forEachIndexed { index, avatar ->
                if (index>=5)return@forEachIndexed
                val img= ImageView(mContext)
                val params= RelativeLayout.LayoutParams(ScreenUtils.dip2px(mContext,20f),ScreenUtils.dip2px(mContext,20f))
                params.leftMargin=ScreenUtils.dip2px(mContext,16f.times(index))
                img.layoutParams=params
                ImageUtil.showCircleImgWithString(img,mContext,avatar,R.mipmap.user_default_icon)
                ll_hot_person.addView(img)
            }
        }
        ll_behavior.postDelayed({
            mBehavior?.onLayoutChild(cl_layout,ll_behavior,ViewCompat.getLayoutDirection(cl_layout))
            mBehavior?.doInit()
        },500)

    }

    private fun initListener(){
        ll_back.setOnClickListener {
            finish()
        }
        ll_hot_billboard.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.WEB_TITLE]="榜单"
            params[RunIntentKey.WEB_URL]="${HomeApiConfig.ROOT_URL}/static/app/xwsapp/#/billboard?topicId=${htBean.id}"
            RouterManager.startActivityWithParams(BundleUrl.LIGHT_APP_ACTIVITY,mContext as FragmentActivity,params)
        }
        mViewModel.htActivityDetailResp.observe(this, Observer {
            if (it.isSuccess&&it.activityStatus=="1"){
                initHtActivity(it)
            }else{
                initHtActivity(null)
            }
        })
        mBehavior?.setTopListener {
            if (it==true){
                tv_title_name.text=htBean.tag
            }else{
                tv_title_name.text=""
            }
        }
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    fun onEvent(event: PublishSuccessEvent){
        //发布成功后跳转到最新
        hua_ti_pager.currentItem=0
    }

    fun goPublish(view: View){
        RunCacheDataUtil.cleanHtCache()
        val data= PreferenceUtil.getString(RunIntentKey.PUBLISH_SAVE)
        if (TextUtils.isEmpty(data)){
            RunCacheDataUtil.setHtCache(GsonUtil.toJson(htBean))
            RouterManager.startActivityWithParams(BundleUrl.PHOTO_NEW_PUBLISH_ACTIVITY,this)
        }else{
            val bean= GsonUtil.fromJson(data!!, SavePublishBean::class.java)
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.CHOOSE_IMAGE_ITEM] = bean.selectImage
            params[RunIntentKey.SIGN_GOODS_LIST] =bean.signGoods
            params[RunIntentKey.UGC_CONTENT]=bean.content
            params[RunIntentKey.UGC_HT]=bean.ht
            RouterManager.startActivityWithParams(BundleUrl.PHOTO_NEW_PUBLISH_ACTIVITY,this,params)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
