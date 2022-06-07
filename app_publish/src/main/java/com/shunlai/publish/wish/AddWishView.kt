package com.shunlai.publish.wish

import com.shunlai.common.bean.GoodsBean

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
interface AddWishView {
    fun showLoading(str:String)
    fun dismissLoading()
    fun addWishSuccess(bean: GoodsBean)
}
