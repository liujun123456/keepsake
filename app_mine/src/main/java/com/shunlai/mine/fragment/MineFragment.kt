package com.shunlai.mine.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.getui.gs.sdk.GsManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.*
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.dialog.SettingWindow
import com.shunlai.mine.dialog.TokenStarDialog
import com.shunlai.mine.dialog.TokenWindow
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.mine.entity.bean.TokenTotalBean
import com.shunlai.mine.entity.resp.CheckBrandResp
import com.shunlai.mine.fragment.adapter.ImgWallAdapter
import com.shunlai.mine.fragment.adapter.MinePagerAdapter
import com.shunlai.mine.tokenStar.TokenStarViewModel
import com.shunlai.mine.utils.MineDetailBehavior
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.ui.UgcActionWindow
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_mine_layout.*
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/5/12
 * @mobile 18711832023
 */
@SuppressLint("SetTextI18n")
class MineFragment:Fragment(), SettingWindow.SettingInterface,UgcActionWindow.ActionListener  {

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    private val tokenViewModel by lazy {
        ViewModelProvider(this).get(TokenStarViewModel::class.java)
    }

    private val mAdapter by lazy {
        ImgWallAdapter(activity!!, mutableListOf())
    }

    private val tokenWindow by lazy {
        TokenWindow(activity!!)
    }

    private val settingWindow by lazy {
        SettingWindow(activity!!,this)
    }

    private val ugcAction by lazy {
        UgcActionWindow(activity!!,this)
    }

    private val starDialog by lazy {
        TokenStarDialog(activity!!,R.style.custom_dialog)
    }

    private var mBehavior: MineDetailBehavior?=null

    private var userType= 0  //0代表自己 1代表别人

    private val textViews= mutableListOf<TextView>()

    private val themeFragments= mutableListOf<ThemeFragment>()

    private var memberId:String?=""

    private var anim:ValueAnimator?=null

    private var mMemberInfo:MemberInfo?=null

    private var tokenScore:TokenTotalBean?=null

    private var currentTab=0

    private val isFromActivity by lazy {
        arguments?.getBoolean("isFromActivity",false)?:false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(activity, R.layout.fragment_mine_layout,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        initViewModel()
        initListener()
    }


    private fun initView(){
        if (isFromActivity){
            if (userType!=0){
                iv_right_icon.setImageResource(R.mipmap.mine_more_icon_white)
            }
            iv_left_icon.setImageResource(R.mipmap.white_arrow_left)
        }
        mBehavior=(ll_fun_layout.layoutParams as CoordinatorLayout.LayoutParams).behavior as MineDetailBehavior
        initTabAndFragment()
        initRv()
    }

    private fun initData(){
        userType=arguments?.getInt(RunIntentKey.USER_TYPE,0)?:0
        memberId=arguments?.getString(RunIntentKey.MEMBER_ID)
        if (TextUtils.isEmpty(memberId)){
            memberId=PreferenceUtil.getString(Constant.USER_ID)
        }
    }

    private fun initViewModel(){
        mViewModel.memberInfo.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess){
                initTitleView(it)
            }
        })
        mViewModel.photoWallList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()){
                mAdapter.mData=it
                mAdapter.notifyDataSetChanged()
            }
        })
        mViewModel.tokenResp.observe(viewLifecycleOwner, Observer {
            tokenScore=it
        })
        tokenViewModel.brandState.observe(viewLifecycleOwner, Observer {
            activity?.let {
                (activity as BaseActivity).hideBaseLoading()
            }
            dealBrandResult(it)
        })
        tokenViewModel.brandCheckResp.observe(viewLifecycleOwner, Observer {
            activity?.let {
                (activity as BaseActivity).hideBaseLoading()
            }
            dealBrandCheck(it)
        })
    }

    private fun initTitleView(info:MemberInfo){
        mMemberInfo=info
        ImageUtil.showCircleImgWithString(head_avatar,activity!!,info.avatar?:"")
        ImageUtil.showCircleImgWithString(tv_title_avatar,activity!!,info.avatar?:"")
        tv_title_user_name.text=info.nickName
        tv_head_user_name.text=info.nickName
        if (TextUtils.isEmpty(info.introduce)){
            if (userType==0){
                tv_head_user_desc.text="您还没有个人简介哦"
            }else{
                tv_head_user_desc.text="他没有个人简介哦"
            }
        }else{
            tv_head_user_desc.text=info.introduce
        }

        if (userType==0){
            tv_head_user_name.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.white_arrow_right,0)
        }else{
            tv_head_user_name.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        }

        if (info.labelList.isNullOrEmpty()){
            tv_label_one.visibility=View.INVISIBLE
            tv_label_two.visibility=View.INVISIBLE
        }else{
            label_layout.visibility=View.VISIBLE
            if (info.labelList?.size?:0>=2){
                tv_label_one.visibility=View.VISIBLE
                tv_label_one.text=info.labelList!![0].content
                tv_label_two.visibility=View.VISIBLE
                tv_label_two.text=info.labelList!![1].content
            }else{
                tv_label_one.visibility=View.VISIBLE
                tv_label_one.text=info.labelList!![0].content
                tv_label_two.visibility=View.INVISIBLE
            }
        }

        tv_token_sum.text=info.token.toString()
        tv_attention_sum.text=info.followNum.toString()
        tv_funs_num.text=info.feesNum.toString()
        tv_likes_num.text=info.likesNum.toString()

        if (userType==1){
            if (info.isFollow==1){
                tv_go_chat_2.visibility=View.VISIBLE
                iv_check_right.visibility=View.VISIBLE

                iv_go_chat.visibility=View.GONE
                tv_do_attention.visibility=View.GONE
            }else{
                tv_go_chat_2.visibility=View.GONE
                iv_check_right.visibility=View.GONE

                iv_go_chat.visibility=View.VISIBLE
                tv_do_attention.visibility=View.VISIBLE
            }
        }
        buildProductImage(info.productImgList?: mutableListOf(),info.logoUrl?:"")
        updateTab(currentTab)

        if (mMemberInfo?.status==1) {
            AlertDialog.Builder(activity!!).setTitle("提示").setMessage("账号违反社区规则，已被封禁")
                .setPositiveButton("确认") { dialog, _ ->
                    dialog.dismiss()
                    activity?.finish()
                }.show()
        }

    }

    @SuppressLint("CheckResult")
    private fun buildProductImage(images:MutableList<String>, logoUrl:String){
        if (images.size>0){
            tv_product_count.text="${images.size}件好物"
            rl_product_img.removeAllViews()
            if (!TextUtils.isEmpty(logoUrl)){
                Observable.just(logoUrl).map {
                    return@map ImageUtil.getBitmapFromUrl(activity!!,it)
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe {
                        if (it!=null){
                            val realHeight=ScreenUtils.dip2px(activity,80f)
                            val realWidth=realHeight.times(it.width).div(it.height)
                            buildChildView(images,realWidth-ScreenUtils.dip2px(activity,16f))
                            val img= ImageView(activity)
                            val params= RelativeLayout.LayoutParams(realWidth,realHeight)
                            params.topMargin=ScreenUtils.dip2px(activity,2f)
                            img.layoutParams=params
                            img.setImageBitmap(it)
                            val anim=AnimationUtils.loadAnimation(activity,R.anim.doll_anim)
                            img.animation=anim
                            anim.start()
                            rl_product_img.addView(img,0)
                        }else{
                            buildChildView(images,0)
                        }
                    }
            }else{
                buildChildView(images,0)
            }
            val params=(rl_product_img.layoutParams as RelativeLayout.LayoutParams)
            params.topMargin=ScreenUtils.dip2px(activity,14f)
            rl_product_img.layoutParams=params

        }else{
            rl_product_img.removeAllViews()
            if (memberId==PreferenceUtil.getString(Constant.USER_ID)){
                val params=(rl_product_img.layoutParams as RelativeLayout.LayoutParams)
                params.topMargin=ScreenUtils.dip2px(activity,16f)
                rl_product_img.layoutParams=params

                tv_product_count.text="分享好物，开启店铺"
                val view=View.inflate(activity,R.layout.item_mine_add_ugc_layout,null)
                view.layoutParams=RelativeLayout.LayoutParams(ScreenUtils.dip2px(activity,80f),ScreenUtils.dip2px(activity,80f))
                view.setOnClickListener {
                    doPublish()
                }
                rl_product_img.addView(view)
            }else{
                val parentParams=(rl_product_img.layoutParams as RelativeLayout.LayoutParams)
                parentParams.topMargin=ScreenUtils.dip2px(activity,14f)
                rl_product_img.layoutParams=parentParams

                tv_product_count.text="${images.size}件好物"
                val img= ImageView(activity)
                val params= RelativeLayout.LayoutParams(ScreenUtils.dip2px(activity,92f),ScreenUtils.dip2px(activity,92f))
                img.layoutParams=params
                ImageUtil.showRoundImgWithResAndRadius(img,activity!!,R.mipmap.image_shop_default,12f)
                rl_product_img.addView(img)
            }
        }
    }

    private fun buildChildView(images:MutableList<String>,initMargin:Int){
        images.forEachIndexed { index, string ->
            if (index>2)return@forEachIndexed
            val view=View.inflate(activity,R.layout.item_mine_shop_goods_view,null)
            val params= RelativeLayout.LayoutParams(ScreenUtils.dip2px(activity,(92-index.times(8)).toFloat()),ScreenUtils.dip2px(activity,(92-index.times(8)).toFloat()))
            params.leftMargin=initMargin+ScreenUtils.dip2px(activity,16f.times(index))
            params.addRule(RelativeLayout.CENTER_VERTICAL)
            view.layoutParams=params
            ImageUtil.showRoundImgWithStringAndRadius(view.findViewById(R.id.iv_card_img),activity!!,string,12f)
            rl_product_img.addView(view,0)
        }
    }

    private fun doPublish(){
        RunCacheDataUtil.cleanHtCache()
        RouterManager.startActivityWithParams(BundleUrl.PHOTO_PICKER_ACTIVITY,activity!!)
    }

    private fun initTabAndFragment(){
        textViews.add(tv_go_mine_ugc)
        textViews.add(tv_go_like)
        textViews.add(tv_go_collect)

        val mine=MineUgcFragment()
        val bundleMine=Bundle()
        bundleMine.putString(RunIntentKey.MEMBER_ID,memberId)
        bundleMine.putInt("fragmentType",1)
        mine.arguments=bundleMine
        themeFragments.add(mine)

        val like=MineUgcFragment()
        val bundleLike=Bundle()
        bundleLike.putString(RunIntentKey.MEMBER_ID,memberId)
        bundleLike.putInt("fragmentType",2)
        like.arguments=bundleLike
        themeFragments.add(like)

        if (userType==0){
            val collect=MineUgcFragment()
            val bundleCollect=Bundle()
            bundleCollect.putString(RunIntentKey.MEMBER_ID,memberId)
            bundleCollect.putInt("fragmentType",3)
            collect.arguments=bundleCollect
            themeFragments.add(collect)
        }else{
            tv_go_collect.visibility=View.GONE
        }


        ll_mine_page.adapter= MinePagerAdapter(childFragmentManager,
            themeFragments)
        ll_mine_page.offscreenPageLimit=themeFragments.size
    }


    private fun initListener(){

        tv_go_mine_ugc.setOnClickListener {
            ll_mine_page.currentItem=0
        }
        tv_go_like.setOnClickListener {
            ll_mine_page.currentItem=1
        }
        tv_go_collect.setOnClickListener {
            ll_mine_page.currentItem=2
        }
        head_avatar.setOnClickListener {
            if (mMemberInfo==null){
                toast("用户信息异常!")
                return@setOnClickListener
            }
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.IMAGE_URL]=mMemberInfo?.avatar?:""
            val bundle= ActivityOptions.makeSceneTransitionAnimation(activity,head_avatar,"preview_img").toBundle()
            RouterManager.startTransActivityWithParams(BundleUrl.PHOTO_SIMPLE_PREVIEW_PATH_ACTIVITY2,activity!!,bundle,
                params)
        }

        tv_head_user_name.setOnClickListener {
            if (userType==1)return@setOnClickListener
            if (mMemberInfo==null){
                toast("用户信息异常!")
                return@setOnClickListener
            }
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.MEMBER_INFO]=GsonUtil.toJson(mMemberInfo!!)
            RouterManager.startActivityWithParams(BundleUrl.MINE_USER_INFO_ACTIVITY,activity!!,params)
        }

        tv_mine_token.setOnClickListener {
            if (tokenScore?.isSuccess==true){
                tokenWindow.showAtLocation(activity?.window?.decorView, Gravity.NO_GRAVITY,0,0)
                tokenWindow.setToken(tokenScore!!,userType)
            }else{
                toast(tokenScore?.errorMsg?:"获取token分异常!")
            }
        }

        tv_user_attention.setOnClickListener {
            if (userType==1)return@setOnClickListener
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.TYPE]=0
            RouterManager.startActivityWithParams(BundleUrl.MINE_FOLLOW_FUN_ACTIVITY,activity!!,params)
        }

        tv_user_fun_s.setOnClickListener {
            if (userType==1)return@setOnClickListener
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.TYPE]=1
            RouterManager.startActivityWithParams(BundleUrl.MINE_FOLLOW_FUN_ACTIVITY,activity!!,params)
        }

        ll_mine_page.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled( position: Int,positionOffset: Float,positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                updateTab(position)
            }

        })
        ll_setting.setOnClickListener {
            if (userType==0){
                settingWindow.showAtLocation(activity?.window?.decorView, Gravity.NO_GRAVITY,0,0)
            }else{
                ugcAction.showWindow(2)
            }
        }
        rl_go_wall.setOnClickListener {
            if (isFromActivity){
                activity?.finish()
            }else{
                goWallPage()
            }
        }

        iv_go_chat.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.TO_USER_ID]=mMemberInfo?.id.toString()
            params[RunIntentKey.TO_USER_NAME]=mMemberInfo?.nickName
            RouterManager.startActivityWithParams(BundleUrl.CHAT_ACTIVITY,activity!!,params)
        }

        tv_go_chat_2.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.TO_USER_ID]=mMemberInfo?.id.toString()
            params[RunIntentKey.TO_USER_NAME]=mMemberInfo?.nickName
            RouterManager.startActivityWithParams(BundleUrl.CHAT_ACTIVITY,activity!!,params)
        }

        tv_do_attention.setOnClickListener {
            mViewModel.doAttention(mMemberInfo?.id.toString())
            mMemberInfo?.let {info->
                if (info.isFollow==1){
                    info.isFollow=0
                }else{
                    info.isFollow=1
                }
                initTitleView(info)
            }
        }
        rl_go_shop.setOnClickListener {
            mMemberInfo?.let {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_INFO]=GsonUtil.toJson(it)
                RouterManager.startActivityWithParams(BundleUrl.PERSONAL_SHOP_RESOURCE_LOAD,activity!!,
                    params)
            }
        }
        mBehavior?.setTopListener(object :MineDetailBehavior.TopListener{
            override fun onPullRelease(distance: Int) {
                if (distance>ScreenUtils.dip2px(activity,100f)){
                    goWallPage()
                    updatePullState(0)
                }
            }

            override fun onTop(bol: Boolean?) {
                if (bol==true){
                    mine_title_layout.visibility=View.VISIBLE
                }else{
                    mine_title_layout.visibility=View.GONE
                }
            }

            override fun onPull(distance: Int) {
                updatePullState(distance)
            }

        })

        ll_go_token_star.setOnClickListener {
            activity?.let {
                (activity as BaseActivity).showBaseLoading()
            }
            //任何情况下都是先检查自己是否填写品牌信息
            if (userType==0){
                tokenViewModel.queryBrandState(tokenViewModel.memberId)
            }else{
                tokenViewModel.checkBrandState(memberId?:"")
            }

        }
    }

    private fun dealBrandCheck(bean:CheckBrandResp){
        starDialog.show()
        if (bean.myStatus=="1"){ //如果自己开通
            if (bean.otherStatus=="1"){
                starDialog.setData(mMemberInfo,0,bean.list)
            }else{
                starDialog.setData(mMemberInfo,1,bean.list)
            }
        }else{//如果自己没开通
            starDialog.setData(mMemberInfo,2,bean.list)
        }
    }


    private fun dealBrandResult(code:Int){
        if (code==1){ //直接进入自己的token星球
            if (mMemberInfo!=null){
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_INFO]=GsonUtil.toJson(mMemberInfo!!)
                RouterManager.startActivityWithParams(BundleUrl.TOKEN_STAR_CENTER,activity!!,params)
            }else{
                toast("用户信息不存在")
            }
        }else{//如果没有选择品牌  去选择品牌页
            RouterManager.startActivityWithParams(BundleUrl.TOKEN_STAR_CHOOSE_BRAND,activity!!)
        }
    }

    private fun updatePullState(distance:Int){
        if (distance>ScreenUtils.dip2px(activity,100f)){
            if (tv_pull_notice.visibility==View.GONE){
                tv_pull_notice.visibility=View.VISIBLE
                if (!isFromActivity){
                    iv_left_icon.animate().rotation(45f)
                }
            }
        }else{
            if (tv_pull_notice.visibility==View.VISIBLE){
                tv_pull_notice.visibility=View.GONE
                if (!isFromActivity){
                    iv_left_icon.animate().rotation(0f)
                }
            }
        }

    }

    private fun initRv(){
        anim=ValueAnimator.ofInt(0,1000)
        anim?.duration = 10*1000
        anim?.addUpdateListener {
            //取决于回调频率跟数据和duration没啥关系
            rv_wall_img.scrollBy(0,1)
        }
        anim?.repeatCount=ValueAnimator.INFINITE

        val layoutManager= GridLayoutManager(activity!!,6)
        layoutManager.spanSizeLookup=object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position.rem(5)<2){
                    return 3
                }else{
                    return 2
                }
            }
        }
        rv_wall_img.layoutManager=layoutManager
        rv_wall_img.adapter=mAdapter
        anim?.start()
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
            anim?.pause()
            currentTime?.let {
                if (System.currentTimeMillis()-it>1000){
                    sensorTrackStay(System.currentTimeMillis()-it)
                }
            }
        }else{
            anim?.start()
            sensorTrackMineIn()
        }
    }


    private fun updateTab(position:Int){
        currentTab=position
        textViews.forEachIndexed { index, textView ->
            if (position==index){
                textView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.tab_index_bg)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            }else{
                textView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            }
        }
    }

    private fun goWallPage(){
        if (mMemberInfo==null){
            toast("用户信息不存在!")
        }else{
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.USER_TYPE]=userType
            params[RunIntentKey.MEMBER_INFO]=GsonUtil.toJson(mMemberInfo!!)
            params[RunIntentKey.WALL_PHOTO_LIST]=GsonUtil.toJson(mAdapter.mData)
            RouterManager.startActivityWithParams(BundleUrl.PHOTO_WALL_ACTIVITY,activity!!,params)
            activity?.overridePendingTransition(0,R.anim.bottom_close_exit)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        anim?.cancel()
    }


    override fun onResume() {
        super.onResume()
        mViewModel.queryWallPage(memberId?:"")
        mViewModel.queryMemberInfo(memberId)
        mViewModel.queryToken(memberId?:"")
        if (!isHidden){
            sensorTrackMineIn()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isHidden){
            currentTime?.let {
                if (System.currentTimeMillis()-it>1000){
                    sensorTrackStay(System.currentTimeMillis()-it)
                }
            }
        }
    }


    override fun goSetting() {
        mMemberInfo?.let {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.MEMBER_INFO]=GsonUtil.toJson(it)
            RouterManager.startActivityWithParams(BundleUrl.SETTING_ACTIVITY,activity!!,params)
        }
    }

    override fun goOrder() {
        RouterManager.startActivityWithParams(BundleUrl.ORDER_ACTIVITY,activity!!)
    }

    override fun onDeleteAction() {

    }

    override fun onComplaintAction() {

    }

    override fun onBlockAction() {
        super.onBlockAction()
        AlertDialog.Builder(activity!!).setTitle("提示").setMessage("确定要拉黑对方吗？").setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }.setPositiveButton("确认") { dialog, _ ->
            dialog.dismiss()
            mViewModel.blockUser(memberId?:"")
            activity?.finish()
        }.show()
    }

    private fun sensorTrackMineIn(){
        val pageName=if (userType==0) "My_Profile"  else "Others_profile"
        val params = JSONObject()
        params.put("from_page", RunCacheDataUtil.lastPage)
        params.put( "is_login",!TextUtils.isEmpty(PreferenceUtil.getString(Constant.USER_ID)))
        GsManager.getInstance().onEvent("${pageName}_page", params)
        RunCacheDataUtil.lastPage =pageName
    }

    var currentTime:Long?=null

    private fun sensorTrackStay(duration:Long){
        val pageName=if (userType==0) "My_Profile"  else "Others_profile"
        val params = JSONObject()
        params.put("event_duration", duration)
        params.put("page_name",pageName)
        params.put("from_page", RunCacheDataUtil.lastPage)
        params.put( "is_login",!TextUtils.isEmpty(PreferenceUtil.getString(Constant.USER_ID)))
        GsManager.getInstance().onEvent("${pageName}_stay", params)
    }

}
