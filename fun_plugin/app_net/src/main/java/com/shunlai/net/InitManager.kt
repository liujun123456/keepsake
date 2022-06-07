package com.shunlai.net

import com.shunlai.net.api.CoreRequestService
import com.shunlai.net.interceptor.DefaultInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
open class InitManager {
    private var retrofit: Retrofit?= null

    protected var baseConfig: CoreHttpConfig?=null

    protected var apiService: CoreRequestService?=null

    protected var okHttpClient:OkHttpClient?=null

    companion object{
        @JvmField
        var logEnable=false
    }


    protected fun initRetrofit(){
        if (baseConfig ==null){
            throw RuntimeException("please setBaseConfig before initialized")
        }
        baseConfig?.let {
            logEnable =it.enableLog()
            val client= OkHttpClient.Builder()
            client.addInterceptor(DefaultInterceptor())
            for (interceptor: Interceptor in it.getInterceptor()){
                client.addInterceptor(interceptor)
            }
            okHttpClient=client.connectTimeout(it.getConnectTimeoutBySeconds(),
                TimeUnit.SECONDS)
                .readTimeout(it.getReadTimeoutBySeconds(), TimeUnit.SECONDS)
                .writeTimeout(it.getWriteTimeoutBySeconds(), TimeUnit.SECONDS)
                .build()
            val builder= Retrofit.Builder()
                .baseUrl(it.getBaseUrl())
                .addConverterFactory(it.getConverterFactory())
                .addCallAdapterFactory(it.getCallAdapterFactory()).client(okHttpClient)

            retrofit =builder.build()

            apiService=retrofit?.create(CoreRequestService::class.java)
        }
    }
}
