package com.shunlai.location.add

import com.shunlai.location.entity.req.AddAddressReq

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
interface AddLocationView {
    fun showLoading(value:String)
    fun hideLoading()
    fun injectData(req: AddAddressReq)
}
