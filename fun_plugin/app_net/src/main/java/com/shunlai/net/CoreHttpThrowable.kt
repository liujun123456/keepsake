package com.shunlai.net

/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
class CoreHttpThrowable(var code:Int,var msg:String) {

    override fun toString(): String {
        return "CoreHttpThrowable(Code=$code, Msg='$msg')"
    }
}
