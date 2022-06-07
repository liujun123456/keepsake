package com.shunlai.mine.tokenStar.brand

import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.graphics.Color
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.RunCacheDataUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.toast
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.BrandBean
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.mine.tokenStar.TokenStarViewModel
import com.shunlai.mine.tokenStar.adapter.BrandChooseAdapter
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_brand_choose_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception

/**
 * @author Liu
 * @Date   2021/8/12
 * @mobile 18711832023
 */
class BrandChooseActivity:BaseActivity(), BrandChooseAdapter.OnBrandClickListener {
    override fun getMainContentResId(): Int= R.layout.activity_brand_choose_layout

    override fun getToolBarResID(): Int=0

    private val mAdapter by lazy {
        BrandChooseAdapter(mContext, mutableListOf(),this)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(TokenStarViewModel::class.java)
    }

    private val isFromHome by lazy {
        intent.getBooleanExtra(RunIntentKey.IS_FROM_HOME,false)
    }

    private var memberInfo: MemberInfo?=null

    private var anim: ValueAnimator?=null

    private val handler=Handler()

    private val ownerList= mutableListOf<BrandBean>()

    override fun afterView() {
        EventBus.getDefault().register(this)
        initListener()
        initRv()
        initViewModel()
        mViewModel.queryOwnerBrand()

        if (isFromHome){
            tv_jump.visibility= View.VISIBLE
            iv_close.visibility= View.GONE
        }else{
            iv_close.visibility= View.VISIBLE
            tv_jump.visibility= View.GONE
        }
    }

    private fun initRv(){
        try {
            memberInfo= GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)
        }catch (e: Exception){

        }
        rv_brand.layoutManager=StaggeredGridLayoutManager(5,StaggeredGridLayoutManager.HORIZONTAL)
        rv_brand.adapter=mAdapter
    }

    private fun initListener(){
        iv_close.setOnClickListener {
            finish()
        }
        tv_do_choose.setOnClickListener {
            showBaseLoading()
            mViewModel.saveBrandState(RunCacheDataUtil.brandChooseData)
        }
        rl_brand_search.setOnClickListener {
            val bundle= ActivityOptions.makeSceneTransitionAnimation(this,rl_brand_search,"search_transition").toBundle()
            RouterManager.startTransActivityWithParams(BundleUrl.BRAND_SEARCH_ACTIVITY,this,bundle,
                mutableMapOf())
        }
        tv_jump.setOnClickListener {
            mViewModel.skipBrandChoose()
            showBaseLoading()
        }
    }

    private fun startAnim(){
        anim=ValueAnimator.ofInt(0,1000)
        anim?.duration = 10*1000
        anim?.addUpdateListener {
            //取决于回调频率跟数据和duration没啥关系
            rv_brand.scrollBy(1,0)
        }
        anim?.repeatCount=ValueAnimator.INFINITE
        anim?.start()
    }

    private fun initViewModel(){
        mViewModel.brandList.observe(this, Observer {
            mAdapter.mData=it
            mAdapter.notifyDataSetChanged()
            handler.postDelayed({
                startAnim()
                ownerList.forEach {bean-> onEvent(bean) }
            },500)
        })
        mViewModel.jumpBrandResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                finish()
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.saveBrandResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                finish()
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.ownerBrandList.observe(this, Observer {
            it.forEach {bean->
                if (!RunCacheDataUtil.brandChooseData.contains(bean.brandCode)){
                    RunCacheDataUtil.brandChooseData.add(bean.brandCode?:"")
                    ownerList.add(bean)
                }
            }
            mViewModel.queryRecommendBrand()
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(bean: BrandBean){
        var isHave=false
        mAdapter.mData.forEachIndexed { index, brandBean ->
            if (bean.brandCode==brandBean.brandCode){
                mAdapter.notifyItemChanged(index)
                isHave=true
                return@forEachIndexed
            }
        }
        if (!isHave){
            anim?.cancel()
            mAdapter.mData.add(0,bean)
            mAdapter.notifyDataSetChanged()
            rv_brand.scrollToPosition(0)
            handler.postDelayed({
                startAnim()
            },500)
        }
        notifyConfirmButton()
    }

    private fun notifyConfirmButton(){
        if (RunCacheDataUtil.brandChooseData.isEmpty()){
            tv_do_choose.text="请至少选择3个品牌"
            tv_do_choose.setBackgroundResource(R.drawable.brand_confirm_bg)
            tv_do_choose.isEnabled=false
            tv_do_choose.setTextColor(Color.parseColor("#4D000000"))
        }else if (RunCacheDataUtil.brandChooseData.size>=3){
            tv_do_choose.text="完成"
            tv_do_choose.setBackgroundResource(R.drawable.black_radius_24)
            tv_do_choose.isEnabled=true
            tv_do_choose.setTextColor(Color.parseColor("#ffffff"))
        }else{
            tv_do_choose.text="再选择${3-RunCacheDataUtil.brandChooseData.size}个品牌"
            tv_do_choose.setBackgroundResource(R.drawable.brand_confirm_bg)
            tv_do_choose.isEnabled=false
            tv_do_choose.setTextColor(Color.parseColor("#4D000000"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        RunCacheDataUtil.brandChooseData.clear()
        anim?.cancel()
    }

    override fun onBrandClick(bean: BrandBean,position:Int) {
        bean.brandCode?.let {
            if (RunCacheDataUtil.brandChooseData.contains(it)){
                RunCacheDataUtil.brandChooseData.remove(it)
            }else{
                RunCacheDataUtil.brandChooseData.add(it)
            }
            mAdapter.notifyItemChanged(position)
        }
        notifyConfirmButton()
    }
}
