package com.shunlai.location.entity

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
class ProvinceBean {
    var id:String?=""
    var name:String?=""
    var pid:String?=""
    var code:String?=""
    var children:MutableList<ProvinceBean>?= mutableListOf()
}
