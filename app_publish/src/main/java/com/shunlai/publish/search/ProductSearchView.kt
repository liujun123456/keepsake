package com.shunlai.publish.search

import com.shunlai.common.bean.GoodsBean

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
interface ProductSearchView {
    fun showLoading(value:String)

    fun dismissLoading()

    fun searchSuccess(dates:MutableList<GoodsBean>, totalNum:String)

    fun loadMoreSuccess(dates:MutableList<GoodsBean>)

}
