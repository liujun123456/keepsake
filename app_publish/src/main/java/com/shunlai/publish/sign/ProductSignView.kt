package com.shunlai.publish.sign

import com.shunlai.common.bean.GoodsBean

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
interface ProductSignView {
    fun showLoading(value:String)

    fun dismissLoading()

    fun addSignGoods(bean: GoodsBean)

    fun goSearchPage(dates:MutableList<GoodsBean>)

    fun trackSignGoods(bean: GoodsBean)
}
