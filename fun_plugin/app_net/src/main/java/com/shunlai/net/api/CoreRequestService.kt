package com.shunlai.net.api

import com.shunlai.net.bean.CoreBaseModel
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
interface CoreRequestService{

    @GET
    fun getByMap(@Url url:String,@QueryMap paramMap:MutableMap<String,Any>):Observable<CoreBaseModel>

    @POST
    @FormUrlEncoded
    fun postByMap(@Url url:String,@FieldMap paramMap: MutableMap<String, Any>):Observable<CoreBaseModel>

    @POST
    fun postByModel(@Url url:String,@Body paramObject:Any):Observable<CoreBaseModel>

    @POST
    fun uploadFile(@Url url: String, @Body body: MultipartBody): Call<CoreBaseModel>


}
