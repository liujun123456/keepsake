package com.shunlai.main.fragment.discover

import com.shunlai.main.entities.HuaTiBean
import com.shunlai.main.entities.UgcBean

/**
 * @author Liu
 * @Date   2021/5/10
 * @mobile 18711832023
 */
interface DiscoverView {
    fun onHtLoad(data:MutableList<HuaTiBean>)

    fun onHomeRecommendUgc(data: MutableList<UgcBean>)

    fun onMoreHomeRecommendUgc(data: MutableList<UgcBean>)

    fun showLoading(value:String)

    fun dismissLoading()

    fun onAttention(result:Int)

    fun onLike(result:Int)

    fun onCollect(result: Int)

    fun onDeleteUgc(result: Int)
}
