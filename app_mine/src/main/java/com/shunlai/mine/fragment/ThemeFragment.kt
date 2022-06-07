package com.shunlai.mine.fragment

import androidx.fragment.app.Fragment
import com.shunlai.mine.entity.bean.SkinBean

/**
 * @author Liu
 * @Date   2021/5/27
 * @mobile 18711832023
 * @desc  用于皮肤切换
 */
abstract class ThemeFragment:Fragment() {
    var skinBean:SkinBean= SkinBean(0,"F7F7F7","F7F7F7")
    open fun setSkinTheme(theme:SkinBean){
        skinBean=theme
        notifyTheme()
    }

    abstract fun notifyTheme()
}
