package com.shunlai.location.manger

import com.shunlai.location.entity.LocationBean

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
interface LocationManagerView {
    fun onLocationList(locs:MutableList<LocationBean>)
    fun showLoading(value:String)
    fun hideLoading()
    fun onRemoveResult(boolean: Boolean)
    fun onDefaultResult(boolean: Boolean)
}
