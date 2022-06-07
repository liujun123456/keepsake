package com.shunlai.main.fragment.attention

import com.shunlai.main.entities.UgcBean
import com.shunlai.main.entities.UgcTjBean

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
interface AttentionView {
    fun onUgcLoad(mData:MutableList<UgcBean>)

    fun onMoreUgcLoad(mData:MutableList<UgcBean>)

    fun onTjLoad(mData:MutableList<UgcTjBean>)

    fun showLoading(value:String)

    fun dismissLoading()

    fun onAttention(result:Int)

    fun onLike(result:Int)

    fun onCollect(result: Int)

    fun onDeleteUgc(result: Int)
}
