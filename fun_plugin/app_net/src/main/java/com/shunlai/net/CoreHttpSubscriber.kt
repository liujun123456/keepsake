package com.shunlai.net

import com.shunlai.net.bean.CoreBaseModel

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
interface CoreHttpSubscriber<T>{
    fun onFailed(throwable: CoreHttpThrowable)

    fun onSuccess(t:T?)

    fun onComplete(){}

    fun onSuccessSource(model: CoreBaseModel){

    }

}
