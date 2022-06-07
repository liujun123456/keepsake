package com.shunlai.publish.pub

import android.net.Uri
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.bean.PathItem
import com.shunlai.common.bean.PublishSuccessEvent
import com.shunlai.common.utils.*
import com.shunlai.net.util.GsonUtil
import com.shunlai.publish.PublishViewModel
import com.shunlai.publish.R
import com.shunlai.common.bean.GoodsBean
import com.shunlai.publish.entity.HuaTiBean
import com.shunlai.publish.entity.req.PublishReq
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.File

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
class ProductPublishPresenter(var mCtx:FragmentActivity,var mView:ProductPublishView) {
    val selectItem by lazy {
        mCtx.intent.getParcelableArrayListExtra<PathItem>(RunIntentKey.CHOOSE_IMAGE_ITEM)?: mutableListOf<PathItem>()
    }

    val signGoods by lazy {
        mCtx.intent.getParcelableArrayListExtra<GoodsBean>(RunIntentKey.SIGN_GOODS_LIST)?: mutableListOf<GoodsBean>()
    }

    val ugcContent by lazy {
        mCtx.intent.getStringExtra(RunIntentKey.UGC_CONTENT)?:""
    }

    var publishType=0

    var ugcHt:HuaTiBean?=null

    private val mViewModel by lazy {
        ViewModelProvider(mCtx).get(PublishViewModel::class.java)
    }

    private var currentUpload=0

    var req= PublishReq()

    init {
        initVieModel()
        try {
            ugcHt=GsonUtil.fromJson(mCtx.intent.getStringExtra(RunIntentKey.UGC_HT)?:"",HuaTiBean::class.java)
        }catch (e:Exception){

        }
        if (ugcHt==null){
            try {
                ugcHt=GsonUtil.fromJson(RunCacheDataUtil.htBean?:"",HuaTiBean::class.java)
            }catch (e:Exception){

            }
         }
     }

    private fun initVieModel(){
        mViewModel.uploadResp.observe(mCtx, Observer {
            if (it.isSuccess){
                req.images.add(it.url)
            }
            currentUpload++
            if (currentUpload>=selectItem.size){
                realPublish()
            }else{
                uploadFile(selectItem[currentUpload].path)
            }
        })
        mViewModel.uploadVideoResp.observe(mCtx, Observer {
            if (it.isSuccess){
                req.images.add(it.thumb?:"")
                req.video=it.url?:""
                if ((selectItem[0].duration/1000).toInt()==0){
                    req.videoTime=1
                }else{
                    req.videoTime=(selectItem[0].duration/1000).toInt()
                }
                realPublish()
            }else{
                mView.dismissLoading()
                toast(it.errorMsg)
            }
        })
        mViewModel.publishResp.observe(mCtx, Observer {
            mView.dismissLoading()
            if (it.isSuccess){
                toast("笔记发布成功!")
                trackPublish()
                PreferenceUtil.removeValueWithKey(RunIntentKey.PUBLISH_SAVE)
                EventBus.getDefault().post(PublishSuccessEvent())
                mCtx.finish()
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.recommendHtResp.observe(mCtx, Observer {
            mView.onHtListBack(it)
        })
    }

    fun getHotHt(){
        mViewModel.queryHotHt()
    }

    fun publish(content:String){
        if (TextUtils.isEmpty(content)){
            toast(R.string.publish_content_notice)
            return
        }
        req= PublishReq()
        req.content=content
        mView.showLoading("发布中")
        if (selectItem.isNullOrEmpty()){
            realPublish()
        }else{
            currentUpload=0
            publishType=selectItem[0].type
            if (publishType==1){
                uploadFile(selectItem[currentUpload].path)
            }else{
                uploadVideo(selectItem[currentUpload].path)
            }
        }
    }

    private fun realPublish(){
        if (!TextUtils.isEmpty(ugcHt?.id)){
            req.topicId=ugcHt?.id?:""
        }
        req.ugcType=publishType
        signGoods.forEach {
            req.ugcGoods.add(it.buildUgcGoods())
        }
        mViewModel.publish(req)
    }

    private fun uploadVideo(path:String){
        val file = File(PathUtils.getPath(mCtx, Uri.parse(path)))
        mViewModel.uploadVideo(file)
    }

    private fun uploadFile(path: String){
        val file = File(PathUtils.getPath(mCtx, Uri.parse(path)))
        mViewModel.uploadFile(FileUtil.luBanPicture(mCtx, file))
    }

    private fun trackPublish(){
        val params = JSONObject()
        params.put("pubslishnote_note_type",publishType)
        params.put("is_title",true)
        params.put("topic_ids",req.topicId)
        params.put("writer_id",PreferenceUtil.getString(com.shunlai.common.utils.Constant.USER_ID))
        params.put("product_num",req.ugcGoods.size)
        req.ugcGoods.forEachIndexed { index, ugcGoods ->
            if (ugcGoods.type=="1"){
                params.put("product_${index+1}_source","taobao")
            }else if (ugcGoods.type=="4"){
                params.put("product_${index+1}_source","wis")
            }
            params.put("product_${index+1}_price",ugcGoods.price)
            params.put("product_${index+1}_brand",ugcGoods.brandName)
            params.put("product_${index+1}_star",ugcGoods.evaluate)

        }
    }
}
