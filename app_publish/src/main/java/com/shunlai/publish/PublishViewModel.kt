package com.shunlai.publish

import android.text.TextUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable
import com.shunlai.net.bean.CoreBaseModel
import com.shunlai.net.bean.FileRequest
import com.shunlai.publish.entity.HuaTiBean
import com.shunlai.publish.entity.req.AddWishReq
import com.shunlai.publish.entity.req.PublishReq
import com.shunlai.publish.entity.resp.*
import java.io.File

/**
 * @author Liu
 * @Date   2021/4/13
 * @mobile 18711832023
 */
class PublishViewModel:ViewModel() {

    val lifecycleOwner: LifecycleOwner?=null

    val uploadResp:MutableLiveData<UploadResp> = MutableLiveData()

    val uploadVideoResp:MutableLiveData<UploadVideoResp> = MutableLiveData()

    val searchResp:MutableLiveData<SearchResp> = MutableLiveData()

    val addWishResp:MutableLiveData<AddWishResp> = MutableLiveData()

    val publishResp:MutableLiveData<BaseResp> = MutableLiveData()

    val recommendHtResp:MutableLiveData<MutableList<HuaTiBean>> = MutableLiveData()

    /**
     * 上传图片
     */
    fun uploadFile(file:File){
        val req = FileRequest().apply {
            files.add(FileRequest.FileModel().apply {
                this.file=file
                this.fileName=file.name
                this.key=file.name
            })
        }
        PublishHttpManager.uploadFile(lifecycleOwner,PublishApiConfig.UPLOAD_IMG,req).subscribe(object :CoreHttpSubscriber<BaseResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                uploadResp.postValue(
                    UploadResp().apply {
                    buildError("上传图片失败")
                })
            }

            override fun onSuccess(t: BaseResp?) {

            }

            override fun onSuccessSource(model: CoreBaseModel) {
                super.onSuccessSource(model)
                if (TextUtils.isEmpty(model.url)){
                    uploadResp.postValue(
                        UploadResp().apply {
                        buildError("上传图片失败")
                    })
                }else{
                    uploadResp.postValue(
                        UploadResp().apply {
                       url=model.url?:""
                    })
                }
            }
        })
    }

    fun uploadVideo(file:File){
        val req = FileRequest().apply {
            files.add(FileRequest.FileModel().apply {
                this.file=file
                this.fileName=file.name
                this.key=file.name
            })
        }
        PublishHttpManager.uploadFile(lifecycleOwner,PublishApiConfig.UPLOAD_VIDEO,req).subscribe(object :CoreHttpSubscriber<UploadVideoResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                uploadVideoResp.postValue(
                    UploadVideoResp().apply {
                        buildError("上传视频失败")
                    })
            }

            override fun onSuccess(t: UploadVideoResp?) {
                uploadVideoResp.postValue(t?:UploadVideoResp().apply {
                    buildError("上传视频失败")
                })
            }
        })

    }


    /**
     * 查询商品
     */
    fun searchGoods(searchKey:String,page:Int){
        val params= mutableMapOf<String,Any>()
        params["page"] = page
        params["size"] = 50
        params[RunIntentKey.UGC_SEARCH_KEY] = searchKey
        PublishHttpManager.getByParams(lifecycleOwner,PublishApiConfig.SEARCH_GOODS, params).subscribe(object :CoreHttpSubscriber<SearchResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                searchResp.postValue(SearchResp().apply {
                    buildError("查询商品失败!")
                })
            }

            override fun onSuccess(t: SearchResp?) {
                searchResp.postValue(t?:SearchResp().apply {
                    buildError("查询商品失败!")
                })
            }

        })
    }


    /**
     * 发布笔记
     */
    fun publish(req: PublishReq){
        PublishHttpManager.postByModel(lifecycleOwner,PublishApiConfig.PUBLISH,req).subscribe(object :CoreHttpSubscriber<BaseResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                publishResp.postValue(BaseResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t: BaseResp?) {
                publishResp.postValue(t?:BaseResp().apply {
                    buildError("发布笔记失败!")
                })
            }
        })
    }


    /**
     * 添加心愿单
     */
    fun addWish(req: AddWishReq){
        val params= mutableMapOf<String,Any>()
        params["brandName"]=req.brandName
        params["name"]=req.name
        req.price?.let {
            params["price"]=it
        }
        params["image"]=req.image
        PublishHttpManager.postByParams(lifecycleOwner,PublishApiConfig.ADD_WISH,params).subscribe(object :CoreHttpSubscriber<AddWishResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                addWishResp.postValue(AddWishResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t: AddWishResp?) {
                addWishResp.postValue(t?:AddWishResp().apply {
                    buildError("添加心愿单失败!")
                })
            }

        })
    }

    fun queryHotHt(){
        PublishHttpManager.getByParams(lifecycleOwner,PublishApiConfig.QUERY_HOT_HT, mutableMapOf())
            .subscribe(object :CoreHttpSubscriber<MutableList<HuaTiBean>>{
                override fun onFailed(throwable: CoreHttpThrowable) {
                    recommendHtResp.postValue(mutableListOf())
                }

                override fun onSuccess(t: MutableList<HuaTiBean>?) {
                    recommendHtResp.postValue(t?: mutableListOf())
                }

            })
    }
}
