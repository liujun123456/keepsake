package com.shunlai.main.ht.fragment

import com.shunlai.main.entities.UgcBean

/**
 * @author Liu
 * @Date   2021/5/12
 * @mobile 18711832023
 */
interface HtUgcView {
    fun onNewUgcLoad(mData:MutableList<UgcBean>)

    fun onNewMoreUgcLoad(mData:MutableList<UgcBean>)

    fun onRecommendUgcLoad(mData:MutableList<UgcBean>)

    fun onMoreRecommendUgcLoad(mData:MutableList<UgcBean>)

    fun showLoading(value:String)

    fun dismissLoading()

    fun onAttention(result:Int)

    fun onLike(result:Int)

    fun onCollect(result: Int)

    fun onDeleteUgc(result: Int)
}
