package com.shunlai.mine.shop.manager

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.DanMuBean
import com.shunlai.mine.shop.ui.OwnerPrincipalView
import com.shunlai.mine.utils.ShopCacheUtil
import com.shunlai.ui.danmuku.BitmapUtils
import com.shunlai.ui.danmuku.DanMuKuCacheStuffer
import com.shunlai.ui.danmuku.DanMuKuContext
import com.shunlai.ui.danmuku.DanMuKuView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser

/**
 * @author Liu
 * @Date   2021/7/21
 * @mobile 18711832023
 */
@SuppressLint("ClickableViewAccessibility","CheckResult")
class ShopStyleManager(var mContext:FragmentActivity) {

    private var mDanMuKuContext: DanMuKuContext?=null
    private lateinit var mDanmakuView:DanMuKuView
    private lateinit var sl_shop_bg: HorizontalScrollView
    private lateinit var ll_shop_bg: LinearLayout
    private lateinit var iv_shop_bg: ImageView
    private lateinit var iv_logo: OwnerPrincipalView
    private lateinit var sl_shop_fg: HorizontalScrollView
    private lateinit var ll_shop_fg: LinearLayout
    private lateinit var iv_shop_fg: ImageView
    private var touchRect=Rect(0,0,0,0)

    fun initView(root:ViewGroup){
        val view:View=View.inflate(mContext,R.layout.shop_style_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        root.addView(view)
        findAllView(view)
    }

    private fun findAllView(view:View){
        mDanmakuView=view.findViewById(R.id.mDanmakuView)
        sl_shop_bg=view.findViewById(R.id.sl_shop_bg)
        ll_shop_bg=view.findViewById(R.id.ll_shop_bg)
        iv_shop_bg=view.findViewById(R.id.iv_shop_bg)
        iv_logo=view.findViewById(R.id.iv_logo)
        sl_shop_fg=view.findViewById(R.id.sl_shop_fg)
        ll_shop_fg=view.findViewById(R.id.ll_shop_fg)
        iv_shop_fg=view.findViewById(R.id.iv_shop_fg)

        sl_shop_bg.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }
        sl_shop_fg.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }
    }

    private fun initDanMuKu(){
        //设置最大显示行数
        val maxLInesPair = mutableMapOf<Int,Int>()
        maxLInesPair[BaseDanmaku.TYPE_SCROLL_RL] = 3

        //设置是否禁止重叠
        val overlappingEnablePair = mutableMapOf<Int, Boolean>()
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = true

        //创建弹幕上下文
        mDanMuKuContext = DanMuKuContext.create()
        val display = mContext.windowManager.defaultDisplay
        val refreshRate = display.refreshRate
        val rate = (1000 / refreshRate).toInt()
        mDanMuKuContext?.setFrameUpateRate(rate)

        //设置一些相关的配置
        mDanMuKuContext!!.setDuplicateMergingEnabled(false) //是否重复合并
            .setScrollSpeedFactor(1.2f) //滑动速度
            .setScaleTextSize(1.2f) //图文混排的时候使用！
            .setCacheStuffer(DanMuKuCacheStuffer(mContext), mBackgroundCacheStuffer)
            .setMaximumLines(maxLInesPair) //设置显示最大行数
            .preventOverlapping(overlappingEnablePair) //设置防重叠，null代表可以重叠
        mDanmakuView.enableDanmakuDrawingCache(true)
        mDanmakuView.showFPS(true)
        mDanmakuView.setCallback(object : DrawHandler.Callback{
            override fun drawingFinished() {

            }

            override fun danmakuShown(danmaku: BaseDanmaku?) {

            }

            override fun prepared() {
                mDanmakuView.start()
            }

            override fun updateTimer(timer: DanmakuTimer?) {

            }
        })
        mDanmakuView.prepare(getDefaultDanMaKuParser(), mDanMuKuContext)
    }

    private val mBackgroundCacheStuffer: BaseCacheStuffer.Proxy =
        object : BaseCacheStuffer.Proxy() {
            override fun prepareDrawing(
                danmaku: BaseDanmaku,
                fromWorkerThread: Boolean
            ) {

            }

            override fun releaseResource(danmaku: BaseDanmaku) {

            }
        }

    private fun getDefaultDanMaKuParser(): BaseDanmakuParser? {
        return object : BaseDanmakuParser() {
            override fun parse(): IDanmakus {
                return Danmakus()
            }
        }
    }

    fun buildDanMu(danMuList:MutableList<DanMuBean>){
        var index=0
        if (danMuList.isNotEmpty()){
            Observable.fromIterable(danMuList).map {
                it.bitmap=ImageUtil.getBitmapFromUrl(mContext,it.avatar?:"")
                return@map it
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    index+=1
                    addToDanMu(it,index)
                }
        }
    }

    private fun addToDanMu(bean:DanMuBean?,index:Int){
        bean?.let {
            val danmaku=mDanMuKuContext?.mDanmakuFactory?.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL)
            //设置相应的数据
            val showBitmap = BitmapUtils.getShowPicture(it.bitmap)
            val map: MutableMap<String, Any> = mutableMapOf()
            map["content"] = bean.content?:""
            map["bitmap"] = showBitmap
            map["color"] = "#4d000000"
            danmaku?.tag = map
            danmaku?.padding = 10
            // 一定会显示, 一般用于本机发送的弹幕
            danmaku?.priority = 0
            danmaku?.isLive = false
            danmaku?.time = mDanmakuView.currentTime+index.times(1000)
            danmaku?.textColor = Color.WHITE
            // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
            danmaku?.textShadowColor = 0
            danmaku?.let {
                mDanmakuView.addDanmaku(danmaku)
            }
        }
    }

    fun initSceneAndDoll(sceneId:String,DollId:String){
        val scene=ShopCacheUtil.getShopBgById(sceneId)
        val doll=ShopCacheUtil.getShopDollById(DollId)

        scene?.let {
            var height:Int
            ll_shop_bg.post {
                height=ll_shop_bg.height
                val realShowWidth=height.times(scene.bgWidth).div(scene.bgHeight)
                val translationX=(realShowWidth- ScreenUtils.getScreenWidth(mContext)).div(2)

                //定位背景图
                iv_shop_bg.layoutParams=LinearLayout.LayoutParams(realShowWidth,LinearLayout.LayoutParams.MATCH_PARENT)
                iv_shop_bg.translationX=-translationX.toFloat()
                iv_shop_bg.setImageBitmap(BitmapFactory.decodeFile(it.localPath+"background.jpg"))

                //定位前景图
                iv_shop_fg.layoutParams=LinearLayout.LayoutParams(realShowWidth,LinearLayout.LayoutParams.MATCH_PARENT)
                iv_shop_fg.translationX=-translationX.toFloat()
                iv_shop_fg.setImageBitmap(BitmapFactory.decodeFile(it.localPath+"foreground.png"))

                val labelBottom=height.times(scene.location?.top?:0f).div(100).toInt()
                val labelLeft=realShowWidth.times(scene.location?.left?:0f).div(100).toInt()-translationX

                val labelWidth=realShowWidth.times(scene.location?.width?:0f).div(100).toInt()
                val labelHeight=labelWidth.times(doll?.height?:0).div(doll?.width?:1)

                val params= RelativeLayout.LayoutParams(labelWidth,labelHeight)
                params.setMargins(labelLeft,labelBottom-labelHeight,0,0)
                iv_logo.layoutParams=params
                iv_logo.setResourcePath(doll.actionLocalPath?:"")

                val bollImgRect=Rect(labelLeft,labelBottom-labelHeight,labelLeft+labelWidth,labelBottom)
                touchRect.left=labelLeft+labelWidth.times(doll?.hotRegion?.left?:0F).div(100).toInt()
                touchRect.top=bollImgRect.top+bollImgRect.top.times(doll?.hotRegion?.top?:0F).div(100).toInt()
                touchRect.right= touchRect.left+labelWidth.times(doll?.hotRegion?.width?:0f).div(100).toInt()
                touchRect.bottom=bollImgRect.bottom

            }
        }

    }

    fun onTouchXy(x:Int,y:Int){
        if (touchRect.contains(x,y)){
            iv_logo.showTouchAnim()
        }
    }


    fun onDestroy(){

    }

    fun onResume(){
        iv_logo.onResume()
        initDanMuKu()

    }

    fun onPause(){
        iv_logo.onPause()
        mDanmakuView.release()
    }
}
