package com.shunlai.net

import android.os.AsyncTask
import androidx.lifecycle.LifecycleOwner
import com.shunlai.net.bean.FileRequest
import com.shunlai.net.download.FileDownloadCallback
import com.shunlai.net.download.FileDownloadTask
import com.shunlai.net.observable.CoreFileUploadObservable
import com.shunlai.net.observable.CoreGetObservable
import com.shunlai.net.observable.CorePostBodyObservable
import com.shunlai.net.observable.CorePostParamsObservable
import java.io.File

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
abstract class CoreHttpManager: InitManager() {

    init {
        baseConfig=getConfig()
        initRetrofit()
    }

    fun postByModel(life: LifecycleOwner?=null, paramUrl:String, paramObject:Any): CorePostBodyObservable {
        return CorePostBodyObservable(life,paramUrl,paramObject,apiService!!)
    }

    fun postByParams(life:LifecycleOwner?=null,paramUrl:String, paramMap:MutableMap<String,Any>): CorePostParamsObservable {
        return CorePostParamsObservable(life,paramUrl,paramMap,apiService!!)
    }

    fun getByParams(life:LifecycleOwner?=null,paramUrl:String, paramMap:MutableMap<String,Any>): CoreGetObservable {
        return CoreGetObservable(life,paramUrl,paramMap,apiService!!)
    }

    fun uploadFile(life:LifecycleOwner?=null,paramUrl:String, fileReq: FileRequest): CoreFileUploadObservable {
        return CoreFileUploadObservable(life,paramUrl,fileReq,apiService!!)
    }

    fun downLoadFile(url:String,target:File,callBack:FileDownloadCallback): FileDownloadTask {
        return FileDownloadTask(url, target,callBack,okHttpClient)
    }


    abstract fun getConfig(): CoreHttpConfig



}
