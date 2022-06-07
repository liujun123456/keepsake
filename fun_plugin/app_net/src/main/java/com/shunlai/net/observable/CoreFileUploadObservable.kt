package com.shunlai.net.observable

import androidx.lifecycle.LifecycleOwner
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.api.CoreRequestService
import com.shunlai.net.bean.CoreBaseModel
import com.shunlai.net.bean.FileRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
class CoreFileUploadObservable(private var life: LifecycleOwner?, private var paramUrl: String, private var fileReq: FileRequest, private var api: CoreRequestService) : CoreObservable() {
    override fun <T> subscribe(subscriber: CoreHttpSubscriber<T>) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val call = buildUploadReq()
                val response = call.execute()
                launch(Dispatchers.Main) {
                    try {
                        parseOnNext(subscriber, response.body()!!)
                    }catch (e:Exception){
                        parseOnError(subscriber, e)
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main){
                    parseOnError(subscriber, e)
                }
            }
        }
    }

    private fun buildUploadReq(): Call<CoreBaseModel> {
        val builder = MultipartBody.Builder()
        val params=fileReq.params
        val mIterator=params.iterator()
        while (mIterator.hasNext()){
            val next=mIterator.next()
            builder.addFormDataPart(next.key, next.value)
        }
        fileReq.files.forEach {
            it.file?.let {file->
                val fileBody=file.asRequestBody("image/*".toMediaTypeOrNull())
                builder.addFormDataPart("file", it.fileName, fileBody)
            }
        }
        builder.setType("multipart/form-data".toMediaTypeOrNull()!!)
        return api.uploadFile(paramUrl,builder.build())
    }
}
