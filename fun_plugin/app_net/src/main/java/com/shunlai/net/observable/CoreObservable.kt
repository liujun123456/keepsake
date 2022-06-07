package com.shunlai.net.observable

import androidx.lifecycle.LifecycleOwner
import com.google.gson.JsonSyntaxException
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable
import com.shunlai.net.DisposableLife
import com.shunlai.net.bean.CoreBaseModel
import com.shunlai.net.bean.ErrorCode
import com.shunlai.net.util.CoreHttpUtil
import com.shunlai.net.util.GsonUtil
import com.shunlai.net.util.logE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
abstract class CoreObservable {

    abstract fun <T> subscribe(subscriber: CoreHttpSubscriber<T>)

    private val BAD_RESULT="参数返回异常!"

    private val UNDIFINE="未知错误"

    private val TIMEOUT_TIPS: String = "网络请求超时，请检测您的网络状态后重试!"

    private val NETWORK_UNABLE: String =  "发生未知错误，请您重新尝试!"

    fun <T> buildObservable(paramObservable: Observable<T>):Observable<T>{
        return paramObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 数据转换
     */
    protected fun <T> parseOnNext(subscribeListener: CoreHttpSubscriber<T>, model: CoreBaseModel){
        model.toString().logE()
        if (model.code!=0){
            subscribeListener.onFailed(CoreHttpThrowable(model.code?: ErrorCode.BAD_RESULT_CODE,model.msg?:UNDIFINE))
            return
        }

        try {

            if (model.data==null){
                subscribeListener.onSuccess(null)
                subscribeListener.onSuccessSource(model)
                return
            }
            val data= GsonUtil.fromJson<T>(model.data, CoreHttpUtil.getClassType(subscribeListener))

            subscribeListener.onSuccess(data)
            subscribeListener.onSuccessSource(model)

        }catch (e: JsonSyntaxException){

            subscribeListener.onFailed(CoreHttpThrowable(ErrorCode.BAD_RESULT_CODE,e.toString()))
        }
    }

    protected fun <T> parseOnError(subscribeListener: CoreHttpSubscriber<T>, e: Throwable){
        if (e is SocketTimeoutException || e is TimeoutException) {

            subscribeListener.onFailed(CoreHttpThrowable(ErrorCode.INTENT_ERROR, TIMEOUT_TIPS))
        }
        e.message?.logE()
        subscribeListener.onFailed(CoreHttpThrowable(ErrorCode.INTENT_ERROR, NETWORK_UNABLE))
    }

    protected fun contactDisposable(life: LifecycleOwner?, disposable: Disposable){
        life?.let {
            DisposableLife(life,disposable)
        }
    }
}
