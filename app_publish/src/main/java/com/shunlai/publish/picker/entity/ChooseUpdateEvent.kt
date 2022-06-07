package com.shunlai.publish.picker.entity

/**
 * @author Liu
 * @Date   2021/4/9
 * @mobile 18711832023
 * 0刷新全部 1删除 2新增
 */
class ChooseUpdateEvent(var action:Int=0,var removeIndex:Int=-1) {
}
