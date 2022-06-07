package com.shunlai.mine.entity.bean

/**
 * @author Liu
 * @Date   2021/7/19
 * @mobile 18711832023
 */
class ShopDollBean {
    var actionLocalPath:String?=""
    var version:String?=""
    var id:String?=""
    var zipFileUrl:String?=""
    var width:Int?=0
    var height:Int?=0
    var hotRegion:HotRegion?=null


    class HotRegion{
        var width:Float?=0f
        var height:Float?=0f
        var top:Float?=0f
        var left:Float?=0f
    }
}
