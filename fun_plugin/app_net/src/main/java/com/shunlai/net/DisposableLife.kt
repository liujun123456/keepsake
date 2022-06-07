package com.shunlai.net


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
class DisposableLife(var life: LifecycleOwner, var disposable: Disposable): LifecycleObserver {

    init {
        life.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        life.lifecycle.removeObserver(this)
        if (!disposable.isDisposed){
            disposable.dispose()
        }
    }
}
