package com.shunlai.net.util

import android.text.TextUtils
import android.util.Log
import com.shunlai.net.InitManager

/**
 * @author Liu
 * @Date   2020/8/14
 * @mobile 18711832023
 */

const val tag = "core_http"

fun String.logE() {
    if (InitManager.logEnable && !TextUtils.isEmpty(this)) {
        Log.e(tag, this)
    }
}

fun String.logI() {
    if (InitManager.logEnable && !TextUtils.isEmpty(this)) {
        Log.i(tag, this)
    }
}

fun String.logW() {
    if (InitManager.logEnable && !TextUtils.isEmpty(this)) {
        Log.w(tag, this)
    }
}

fun String.logD() {
    if (InitManager.logEnable && !TextUtils.isEmpty(this)) {
        Log.d(tag, this)
    }
}

fun String.logV() {
    if (InitManager.logEnable && !TextUtils.isEmpty(this)) {
        Log.v(tag, this)
    }
}


