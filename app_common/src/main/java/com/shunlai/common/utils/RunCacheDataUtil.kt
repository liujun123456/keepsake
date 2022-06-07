package com.shunlai.common.utils

/**
 * @author Liu
 * @Date   2021/8/18
 * @mobile 18711832023
 */
object RunCacheDataUtil {

    //埋点
    var lastPage:String?=null

    //C店主题色
    var THEME_COLOR="F279A2"

    //默认发布话题
    var htBean:String?=null

    fun cleanHtCache(){
        htBean=null
    }

    fun setHtCache(bean:String?){
        htBean=bean
    }

    val brandChooseData= mutableListOf<String>()
}
