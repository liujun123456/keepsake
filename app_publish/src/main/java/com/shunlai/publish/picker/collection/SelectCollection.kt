package com.shunlai.publish.picker.collection

import com.shunlai.common.bean.PathItem
import com.shunlai.publish.picker.entity.Item

/**
 * @author Liu
 * @Date   2021/4/9
 * @mobile 18711832023
 */
object SelectCollection {
    val selectItem:MutableList<PathItem> = mutableListOf()

    fun getDataIndex(item:Item):Int{
        selectItem.forEachIndexed { index, data ->
            if (item.id==data.id)return index
        }
        return 0
    }

    fun containsItem(item:Item):Boolean{
        selectItem.forEach {
            if (it.id==item.id) return true
        }
        return false
    }

    fun removeItem(item:Item){
        var removeIndex=-1
        selectItem.forEachIndexed { index, pathItem ->
            if (pathItem.id==item.id){
                removeIndex=index
                return@forEachIndexed
            }
        }
        if (removeIndex!=-1){
            selectItem.removeAt(removeIndex)
        }
    }

    fun addItem(item:Item){
        val path=item.uri.toString()
        selectItem.add(PathItem(item.id,path,item.duration,if (item.isVideo) 2 else 1))
    }
}
