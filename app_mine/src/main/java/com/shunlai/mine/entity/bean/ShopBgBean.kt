package com.shunlai.mine.entity.bean

/**
 * @author Liu
 * @Date   2021/7/19
 * @mobile 18711832023
 */
class ShopBgBean {
    var id:String?=""
    var name:String?=""
    var zipFileUrl:String?=""
    var version:String?=""
    var location:SceneLocation?=null
    var localPath:String?=""
    var bgWidth:Int=1
    var bgHeight:Int=1
    var themeColor:String=""

    class SceneLocation{
        var top:Float?=0f
        var left:Float?=0f
        var width:Float?=0f
    }
}
