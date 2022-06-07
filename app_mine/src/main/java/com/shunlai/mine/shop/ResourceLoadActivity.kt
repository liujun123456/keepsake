package com.shunlai.mine.shop

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.toast
import com.shunlai.mine.MineHttpManager
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.mine.entity.bean.ShopBgBean
import com.shunlai.mine.entity.bean.ShopDollBean
import com.shunlai.mine.entity.resp.ResourceResp
import com.shunlai.mine.utils.ShopCacheUtil
import com.shunlai.net.download.FileDownloadCallback
import com.shunlai.net.download.FileDownloadTask
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_resource_load_layout.*
import java.io.File

/**
 * @author Liu
 * @Date   2021/7/19
 * @mobile 18711832023
 */
@SuppressLint("CheckResult","HandlerLeak")
class ResourceLoadActivity:BaseActivity(), FileDownloadCallback {
    override fun getMainContentResId(): Int= R.layout.activity_resource_load_layout

    override fun getToolBarResID(): Int=0

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    private val downLoadList:MutableList<Any> = mutableListOf()

    private var task: FileDownloadTask?=null

    private var currentIndex=0

    private var localZipPath:String?=null

    private var zipParentPath:String?=null

    private var downLoadType=0 //0场景 1主理人

    private var memberId:String?=null

    private var sceneId:String?=null

    private var dollId:String?=null

    private var memberInfo: MemberInfo?=null

    private var mAnim:AnimationDrawable?=null

    override fun afterView() {
        try {
            memberInfo= GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)
        }catch (e: java.lang.Exception){

        }
        memberId=memberInfo?.id?.toString()?:""
        sceneId=memberInfo?.principalSceneId?:"1"
        dollId=memberInfo?.principalModelId?:"1"
        mViewModel.querySourceList()
        initViewModel()
        mAnim=iv_load.drawable as AnimationDrawable
        mAnim?.start()
    }

    private fun initViewModel(){
        mViewModel.resourceResp.observe(this, Observer {
            if (it.isSuccess){
                compareResource(it)
            }else{
                toast(it.errorMsg)
            }
        })
    }


    private fun doDownLoad(bean:Any){
        if (bean is ShopBgBean){
            val pathValues=bean.zipFileUrl!!.split("/")
            zipParentPath=ShopCacheUtil.getShopCacheRootPath(mContext)+"/scene${bean.id}/${bean.version}/"
            localZipPath=zipParentPath+pathValues[pathValues.size-1]
            task=MineHttpManager.downLoadFile(bean.zipFileUrl?:"",File(localZipPath),this)
            downLoadType=0
            task?.execute()
        }else if (bean is ShopDollBean){
            val pathValues=bean.zipFileUrl!!.split("/")
            zipParentPath=ShopCacheUtil.getShopCacheRootPath(mContext)+"/scene${bean.id}/${bean.version}/"
            localZipPath=zipParentPath+pathValues[pathValues.size-1]
            task=MineHttpManager.downLoadFile(bean.zipFileUrl?:"",File(localZipPath),this)
            downLoadType=1
            task?.execute()
        }
    }


    override fun onFileStart() {

    }

    override fun onFileProgress(progress: Int, networkSpeed: Long) {

    }

    override fun onFileDone() {
        ShopCacheUtil.unZipFolder(localZipPath,zipParentPath)
        saveCache()
        currentIndex+=1
        if (currentIndex>=downLoadList.size){
            //下载结束
            doLoadAnim(ll_progress.progress,100,1000)
        }else{
            doDownLoad(downLoadList[currentIndex])
        }
    }

    private fun saveCache(){
        if (downLoadType==0){
            val cache=ShopCacheUtil.getLocalShopBg()
            val currentBean=downLoadList[currentIndex] as ShopBgBean
            currentBean.localPath=zipParentPath
            var index=-1
            cache.forEachIndexed { i, shopBgBean ->
                if (shopBgBean.id==currentBean.id){
                    index=i
                }
            }
            if (index==-1){
                cache.add(currentBean)
            }else{
                cache[index] = currentBean
            }
            ShopCacheUtil.saveLocalShopBg(cache)
        }else{
            val cache=ShopCacheUtil.getLocalShopDoll()
            val currentBean=downLoadList[currentIndex] as ShopDollBean
            currentBean.actionLocalPath=zipParentPath
            var index=-1
            cache.forEachIndexed { i, shopBgBean ->
                if (shopBgBean.id==currentBean.id){
                    index=i
                }
            }
            if (index==-1){
                cache.add(currentBean)
            }else{
                cache[index] = currentBean
            }
            ShopCacheUtil.saveLocalShopDoll(cache)
        }
    }

    override fun onFileFailure() {

    }

    override fun onDestroy() {
        super.onDestroy()
        task?.cancel(true)
        anim?.cancel()
        mAnim?.stop()
    }

    private var anim:ValueAnimator?=null

    @SuppressLint("SetTextI18n")
    private fun doLoadAnim(startProgress:Int, endProgress:Int, duration:Long){
        anim?.cancel()
        anim=ValueAnimator.ofInt(startProgress,endProgress)
        anim?.duration=duration
        anim?.addUpdateListener {
            val value=it.animatedValue as Int
            ll_progress.progress=value
            tv_progress.text="加载进度$value%"
            if (value==100){
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_INFO]=intent.getStringExtra(RunIntentKey.MEMBER_INFO)
                RouterManager.startActivityWithParams(BundleUrl.PERSONAL_SHOP_ACTIVITY,this,params)
                overridePendingTransition(R.anim.splash_close_enter,R.anim.splash_close_exit)
                finish()
            }
        }
        anim?.start()
    }

    /**
     * 比对服务器资源 和本地资源 筛选需要下载的资源
     */
    private fun compareResource(resource: ResourceResp){
        val localScene=ShopCacheUtil.getLocalShopBg()
        val localDoll=ShopCacheUtil.getLocalShopDoll()

        val sceneDownList = mutableListOf<ShopBgBean>()
        val dollDownList = mutableListOf<ShopDollBean>()
        resource.principalModelDTOList.forEach {netSource->
            var isNeedDownLoad=true
            localDoll.forEach {localSource->
                if (netSource.id==localSource.id&&netSource.version==localSource.version){
                    isNeedDownLoad=false
                }
            }
            if (isNeedDownLoad){
                dollDownList.add(netSource)
            }
        }

        resource.principalSceneDTOList.forEach {netSource->
            var isNeedDownLoad=true
            localScene.forEach {localSource->
                if (netSource.id==localSource.id&&netSource.version==localSource.version){
                    isNeedDownLoad=false
                }
            }
            if (isNeedDownLoad){
                sceneDownList.add(netSource)
            }
        }

        if (sceneDownList.isEmpty()&&dollDownList.isEmpty()){
            doLoadAnim(ll_progress.progress,100,1000)
        }else{
            ShopCacheUtil.setNeedDownLoad(sceneDownList,dollDownList)
            downLoadList.addAll(sceneDownList)
            downLoadList.addAll(dollDownList)
            doDownLoad(downLoadList[currentIndex])
            doLoadAnim(0,99,20000)
        }

    }

}
