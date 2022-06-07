package com.shunlai.net.bean

import java.io.File

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
class FileRequest {
    var params:MutableMap<String,String> = mutableMapOf()
    var files= mutableListOf<FileModel>()

    class FileModel{
        var key:String?=null
        var file:File?=null
        var fileName:String?=null
    }
}
