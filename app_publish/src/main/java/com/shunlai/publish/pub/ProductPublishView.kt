package com.shunlai.publish.pub

import com.shunlai.publish.entity.HuaTiBean

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
interface ProductPublishView {

    fun showLoading(value:String)

    fun dismissLoading()

    fun onHtListBack(htList:MutableList<HuaTiBean>)
}
