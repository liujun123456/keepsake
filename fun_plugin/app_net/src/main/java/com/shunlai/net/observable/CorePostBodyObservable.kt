package com.shunlai.net.observable

import androidx.lifecycle.LifecycleOwner
import com.shunlai.net.bean.CoreBaseModel
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.api.CoreRequestService
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
class CorePostBodyObservable(private var life: LifecycleOwner?, private var paramUrl:String, private var paramObject:Any, private var api: CoreRequestService): CoreObservable()  {
    override fun <T> subscribe(subscriber: CoreHttpSubscriber<T>) {
        val observable=buildObservable(api.postByModel(paramUrl,paramObject))
        observable.subscribe(object : Observer<CoreBaseModel> {
            override fun onComplete() {
                subscriber.onComplete()
            }

            override fun onSubscribe(d: Disposable) {
                contactDisposable(life,d)
            }

            override fun onNext(t: CoreBaseModel) {
                parseOnNext(subscriber,t)
            }

            override fun onError(e: Throwable) {
                parseOnError(subscriber,e)
            }
        })
    }
}
