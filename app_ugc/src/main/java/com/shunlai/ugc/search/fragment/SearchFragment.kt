package com.shunlai.ugc.search.fragment

import androidx.fragment.app.Fragment

/**
 * @author Liu
 * @Date   2021/8/24
 * @mobile 18711832023
 */
open abstract class SearchFragment:Fragment() {
    abstract fun setSearchKey(keyWord:String)
}
