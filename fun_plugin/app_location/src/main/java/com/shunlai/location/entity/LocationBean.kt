package com.shunlai.location.entity

import com.shunlai.location.entity.req.AddAddressReq

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
class LocationBean {
    var id:Int?=0
    var memberId:Int?=0
    var address:String?=""
    var street:String?=""
    var phone:String?=""
    var contact:String?=""
    var isDefault:Int?=0
    var cityId:Int?=0
    var provinceId:Int?=0
    var areaId:Int?=0
    var cityName:String?=""
    var provinceName:String?=""
    var area:String?=""

    fun buildLocReq(): AddAddressReq{
        val req=AddAddressReq()
        req.addressId=id.toString()
        req.cityId=cityId.toString()
        req.areaId=areaId.toString()
        req.provinceId=provinceId.toString()
        req.cityName=cityName
        req.provinceName=provinceName
        req.area=area
        req.street=street
        req.phone=phone
        req.contact=contact
        req.isDefault=isDefault
        return req
    }
}
