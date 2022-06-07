package com.shunlai.net

import okhttp3.Interceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
abstract class CoreHttpConfig{

    open fun enableLog():Boolean{
        return true
    }

    open fun getReadTimeoutBySeconds():Long{
        return 60
    }

    open fun getWriteTimeoutBySeconds():Long{
        return 60
    }

    open fun getConnectTimeoutBySeconds():Long{
        return 60
    }

    open fun getInterceptor():List<Interceptor>{
        return emptyList()
    }

    open fun getCallAdapterFactory(): CallAdapter.Factory{
        return RxJava2CallAdapterFactory.create()
    }

    open fun getConverterFactory(): Converter.Factory{
        return GsonConverterFactory.create()
    }

    abstract fun getBaseUrl():String


}
