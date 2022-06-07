package com.shunlai.mine.shop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.*
import com.shunlai.mine.R
import com.shunlai.mine.dialog.LeaveMessageDialog
import com.shunlai.mine.dialog.TokenStarDialog
import com.shunlai.mine.entity.bean.*
import com.shunlai.mine.entity.resp.CheckBrandResp
import com.shunlai.mine.shop.adapter.ShopGoodsPagerAdapter
import com.shunlai.mine.shop.fragment.ShopGoodsFragment
import com.shunlai.mine.shop.manager.ShopStyleManager
import com.shunlai.mine.tokenStar.TokenStarViewModel
import com.shunlai.mine.utils.MyBounceInterpolator
import com.shunlai.mine.utils.ShopCacheUtil
import com.shunlai.mine.utils.ShopDetailBehavior
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.ui.DrawableManager
import kotlinx.android.synthetic.main.activity_personal_shop_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author Liu
 * @Date   2021/7/7
 * @mobile 18711832023
 */
class PersonalShopActivity:BaseActivity() {

    override fun getMainContentResId(): Int= R.layout.activity_personal_shop_layout

    override fun getToolBarResID(): Int=0

    private var mBehavior: ShopDetailBehavior?=null

    private var memberId:String?=null

    private var memberInfo:MemberInfo?=null

    private val mViewModel by lazy {
        ViewModelProvider(this).get(ShopViewModel::class.java)
    }

    private val tokenViewModel by lazy {
        ViewModelProvider(this).get(TokenStarViewModel::class.java)
    }

    private val shopManager by lazy {
        ShopStyleManager(this)
    }

    private val starDialog by lazy {
        TokenStarDialog(mContext,R.style.custom_dialog)
    }

    private var mSceneId:String?=null
    private var mDollId:String?=null
    private var signBoard:SignBoardBean?=null

    private val minHeight = 50
    private var oldBottom = 0
    private var newBottom = 0
    private val rect = Rect()

    override fun afterView() {
        EventBus.getDefault().register(this)
        mBehavior=(ll_fun_layout.layoutParams as CoordinatorLayout.LayoutParams).behavior as ShopDetailBehavior
        initIntent()
        initGoodsView()
        initTitle()
        initViewModel()
        shopManager.initView(ll_shop_style)
        shopManager.initSceneAndDoll(mSceneId!!,mDollId!!)
        updateTab(0)
    }

    @SuppressLint("SetTextI18n")
    private fun initIntent(){
        try {
            memberInfo= GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)
        }catch (e: java.lang.Exception){

        }
        memberId=memberInfo?.id?.toString()?:""
        mSceneId=memberInfo?.principalSceneId?:"1"
        mDollId=memberInfo?.principalModelId?:"1"

        tv_shop_name.text="${memberInfo?.nickName}的店铺"
        ImageUtil.showCircleImgWithString(iv_avatar,mContext,memberInfo?.avatar?:"")
    }

    private fun initTitle(){
        if (memberId==PreferenceUtil.getString(Constant.USER_ID)){
            iv_go_edit_shop.visibility=View.VISIBLE
        }else{
            iv_go_edit_shop.visibility=View.GONE
        }
        ll_back.setOnClickListener {
            finish()
        }
        iv_go_edit_shop.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.SCENE_KEY]=SceneListBean().apply {
                sceneId=mSceneId
            }
            params[RunIntentKey.MODEL_ID]=DollListBean().apply {
                modelId=mDollId
            }
            if (signBoard==null){
                params[RunIntentKey.SIGN_BOARD]= OwnerUgcBean()
            }else{
                params[RunIntentKey.SIGN_BOARD]= OwnerUgcBean().apply {
                    ugcId=signBoard?.ugcId
                    content=signBoard?.ugcTitle
                    if (!signBoard?.imageUrlList.isNullOrEmpty()){
                        firstImage=signBoard!!.imageUrlList!![0]
                    }
                }
            }
            RouterManager.startActivityForResultWithParams(BundleUrl.PERSONAL_SHOP_EDIT_ACTIVITY,this,
                params,10086)
        }
        ll_avatar.setOnClickListener {
            showBaseLoading()
            //任何情况下都是先检查自己是否填写品牌信息
            if (memberId==PreferenceUtil.getString(Constant.USER_ID)){
                tokenViewModel.queryBrandState(tokenViewModel.memberId)
            }else{
                tokenViewModel.checkBrandState(memberId?:"")
            }
        }
    }

    private fun initViewModel(){
        mViewModel.leaveMessageResp.observe(this, Observer {
            if (it.isSuccess){
                LeaveMessageDialog(mContext,R.style.custom_dialog).show()
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.feedResp.observe(this, Observer {
            if (it.isSuccess){
                doFeed()
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.unReadResp.observe(this, Observer {
            if (it>0){
                leave_msg_count.text=it.toString()
                leave_msg_count.visibility=View.VISIBLE
            }else{
                leave_msg_count.text=""
                leave_msg_count.visibility=View.INVISIBLE
            }
        })
        mViewModel.unReadFeedResp.observe(this, Observer {
            if (it>0){
                feed_msg_count.text=it.toString()
                feed_msg_count.visibility=View.VISIBLE
            }else{
                feed_msg_count.text=""
                feed_msg_count.visibility=View.INVISIBLE
            }
        })
        mViewModel.danMuList.observe(this, Observer {
            shopManager.buildDanMu(it)
        })
        mViewModel.signBoardResp.observe(this, Observer {
            if (it.isSuccess&&!TextUtils.isEmpty(it.ugcId)){
                ll_signboard.visibility=View.VISIBLE
                initSignBoard(it)
            }else{
                ll_signboard.visibility=View.GONE
            }
        })

        tokenViewModel.brandState.observe(this, Observer {
            hideBaseLoading()
            dealBrandResult(it)
        })
        tokenViewModel.brandCheckResp.observe(this, Observer {
            hideBaseLoading()
            dealBrandCheck(it)
        })
    }

    private fun dealBrandCheck(bean: CheckBrandResp){
        starDialog.show()
        if (bean.myStatus=="1"){ //如果自己开通
            if (bean.otherStatus=="1"){
                starDialog.setData(memberInfo,0,bean.list)
            }else{
                starDialog.setData(memberInfo,1,bean.list)
            }
        }else{//如果自己没开通
            starDialog.setData(memberInfo,2,bean.list)
        }
    }


    private fun dealBrandResult(code:Int){
        if (code==1){ //直接进入自己的token星球
            if (memberInfo!=null){
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_INFO]= GsonUtil.toJson(memberInfo!!)
                RouterManager.startActivityWithParams(BundleUrl.TOKEN_STAR_CENTER,this,params)
            }else{
                toast("用户信息不存在")
            }
        }else{//如果没有选择品牌  去选择品牌页
            RouterManager.startActivityWithParams(BundleUrl.TOKEN_STAR_CHOOSE_BRAND,this)
        }
    }

    private fun initSignBoard(bean:SignBoardBean){
        signBoard=bean
        tv_ugc_title.text=bean.ugcTitle
        val bannerList= mutableListOf<BannerInfo>()
        bean.imageUrlList?.forEach {
            bannerList.add(BannerInfo(it))
        }
        x_banner.setBannerData(R.layout.banner_item_layout,bannerList)
        x_banner.loadImage { _, _, view, position ->
            ImageUtil.showRoundImgWithStringAndRadius(view as ImageView,mContext,bannerList[position].imageUrl,8f)
        }
        tv_ugc_title?.post {
            tv_ugc_title.isSelected=true
        }
    }

    private fun initGoodsView(){
        vp_mine_goods.adapter= ShopGoodsPagerAdapter(supportFragmentManager, mutableListOf(
            ShopGoodsFragment(memberId?:"",1),ShopGoodsFragment(memberId?:"",0)
        ))
        vp_mine_goods.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                updateTab(position)
            }

        })

        tv_tuijian.setOnClickListener {
            vp_mine_goods.currentItem=0
        }
        tv_bilei.setOnClickListener {
            vp_mine_goods.currentItem=1
        }
        ll_left_msg.setOnClickListener {
            if (memberId==PreferenceUtil.getString(Constant.USER_ID)){
                mViewModel.clearUnReadLeaveMessage()
                RouterManager.startActivityWithParams(BundleUrl.SHOP_IMPRESSION_ACTIVITY,this,
                    mutableMapOf())
            }else{
                showBottomInput()
            }
        }

        ll_feed.setOnClickListener {
            if (memberId==PreferenceUtil.getString(Constant.USER_ID)){
                mViewModel.clearFeedMsg()
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_ID]=memberId
                RouterManager.startActivityWithParams(BundleUrl.SHOP_FEED_RECORD_ACTIVITY,this,
                    params)
            }else{
                mViewModel.doFeeding(memberId?:"")
            }
        }

        tv_send.setOnClickListener {
            if (TextUtils.isEmpty(et_msg_input.text.toString())){
                toast("请输入您的留言")
                return@setOnClickListener
            }
            mViewModel.leaveMessage(et_msg_input.text.toString(),memberId?:"")
            hideInput(et_msg_input)
        }

        et_msg_input.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (TextUtils.isEmpty(s?.toString())){
                    tv_send.visibility=View.GONE
                }else{
                    tv_send.visibility=View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        v_transition.setOnClickListener {
            hideInput(et_msg_input)
        }

        main_layout.viewTreeObserver.addOnGlobalLayoutListener {
            updateWindowHeight()
        }

        mBehavior?.setShopBehaviorListener { x, y ->
            shopManager.onTouchXy(x,y)
            signBoard?.ugcId?.let {
                val rect=Rect()
                ll_signboard.getHitRect(rect)
                if (rect.contains(x,y)){
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.UGC_ID]= it
                    RouterManager.startActivityWithParams(BundleUrl.UGC_DETAIL_ACTIVITY,this,params)
                }
            }
        }
    }

    private fun doFeed(){
        val location= IntArray(2)
        iv_feed.getLocationOnScreen(location)
        val view=ImageView(mContext)
        val params=RelativeLayout.LayoutParams(iv_feed.width,iv_feed.height)
        params.topMargin=location[1]
        params.leftMargin=location[0]
        view.layoutParams=params
        view.setImageResource(R.mipmap.icon_feed)
        main_layout.addView(view)
        val anim= AnimationUtils.loadAnimation(mContext,R.anim.feed_anim)
        anim.interpolator= MyBounceInterpolator()
        view.animation=anim
        anim.start()
        anim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                main_layout.removeView(view)
            }

            override fun onAnimationStart(animation: Animation?) {

            }

        })
    }

    private var defaultMargin: Int = 0
    private fun updateWindowHeight(){
        oldBottom = rect.bottom
        main_layout.getWindowVisibleDisplayFrame(rect)
        newBottom = rect.bottom
        if (oldBottom - newBottom > minHeight){
            val params = bottom_menu.layoutParams as RelativeLayout.LayoutParams
            defaultMargin += oldBottom - newBottom
            params.setMargins(0, 0, 0, defaultMargin)
            bottom_menu.layoutParams = params
        }else if (newBottom - oldBottom > minHeight){
            val params = bottom_menu.layoutParams as RelativeLayout.LayoutParams
            if (defaultMargin == newBottom - oldBottom){
                params.setMargins(0, 0, 0, 0)
                bottom_menu.layoutParams = params
                defaultMargin=0
                bottom_menu.visibility=View.GONE
                v_transition.visibility=View.GONE
            }else{
                if (defaultMargin > 0){
                    defaultMargin -= newBottom - oldBottom
                    params.setMargins(0, 0, 0, defaultMargin)
                    bottom_menu.layoutParams = params
                }
            }
        }
    }

    private fun showBottomInput(){
        v_transition.visibility= View.VISIBLE
        bottom_menu.visibility= View.VISIBLE
        et_msg_input.setText("")
        et_msg_input.requestFocus()
        showInput(et_msg_input)
    }


    private fun updateTab(position:Int){
        val sceneBean= ShopCacheUtil.getShopBgById(mSceneId)
        if (position==0){
            tv_tuijian.setTextColor(Color.parseColor("#FFFFFF"))
            try {
                val drawable=DrawableManager.buildLinerDrawableWithCorner(
                    intArrayOf(Color.parseColor("#${sceneBean.themeColor}"),Color.parseColor("#${sceneBean.themeColor}")),
                    ScreenUtils.dip2px(mContext,16f).toFloat())
                RunCacheDataUtil.THEME_COLOR=sceneBean.themeColor
                tv_tuijian.background=drawable
            }catch (e:Exception){
                RunCacheDataUtil.THEME_COLOR="F279A2"
                tv_tuijian.setBackgroundResource(R.drawable.pink_radius_16)
            }
            tv_bilei.setTextColor(Color.parseColor("#191919"))
            tv_bilei.setBackgroundResource(R.drawable.alpha_0d_radius16_bg)
        }else{
            tv_tuijian.setTextColor(Color.parseColor("#191919"))
            tv_tuijian.setBackgroundResource(R.drawable.alpha_0d_radius16_bg)
            tv_bilei.setTextColor(Color.parseColor("#FFFFFF"))

            try {
                val drawable=DrawableManager.buildLinerDrawableWithCorner(
                    intArrayOf(Color.parseColor("#${sceneBean.themeColor}"),Color.parseColor("#${sceneBean.themeColor}")),
                    ScreenUtils.dip2px(mContext,16f).toFloat())
                tv_bilei.background=drawable
                RunCacheDataUtil.THEME_COLOR=sceneBean.themeColor
            }catch (e:Exception){
                RunCacheDataUtil.THEME_COLOR="F279A2"
                tv_bilei.setBackgroundResource(R.drawable.pink_radius_16)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event:MineGoodsEvent){
        if (event.type==1){
            tv_tuijian.text="推荐 ${event.size}"
        }else{
            tv_bilei.text="避雷 ${event.size}"
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        shopManager.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onPause() {
        super.onPause()
        shopManager.onPause()
    }


    override fun onResume() {
        super.onResume()
        shopManager.onResume()
        if (memberId==PreferenceUtil.getString(Constant.USER_ID)){
            mViewModel.unReadLeaveMessage(memberId?:"")
            mViewModel.queryFeedMsg()
        }
        mViewModel.queryAllDanMu(memberId?:"")
        mViewModel.querySignBoardInfo(memberId?:"")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==10086&&resultCode==Activity.RESULT_OK){
            data?.let {
                mSceneId=it.getStringExtra(RunIntentKey.SCENE_KEY)
                mDollId=it.getStringExtra(RunIntentKey.MODEL_ID)
                shopManager.initSceneAndDoll(mSceneId!!,mDollId!!)
            }
        }
    }

}
