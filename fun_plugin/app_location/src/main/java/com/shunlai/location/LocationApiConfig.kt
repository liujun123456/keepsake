package com.shunlai.location

import com.shunlai.common.BaseApiConfig

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
object LocationApiConfig: BaseApiConfig() {

    const val ADD_ADDRESS="member/addAddress"   //新增和编辑地址

    const val ADDRESS_LIST="member/addressList"    //地址列表

    const val DELETE_ADDRESS="member/deleteAddress"  //删除地址

    const val ADDRESS_DEFAULT="member/settingDefault"  //设置为默认地址

    const val GET_PROVINCES="member/getProvinces"   //获取省市区
}
