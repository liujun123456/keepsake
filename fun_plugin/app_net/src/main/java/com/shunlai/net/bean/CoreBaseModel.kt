package com.shunlai.net.bean

import com.google.gson.JsonElement

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
class CoreBaseModel(var code:Int?=null,var msg:String?=null,var data: JsonElement?=null,var url:String?=""){
    override fun toString(): String {
        return "CoreBaseModel(code=$code, msg=$msg, data=$data, url=$url)"
    }
}
