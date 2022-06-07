package com.shunlai.ugc.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.shunlai.ugc.search.fragment.SearchFragment

/**
 * @author Liu
 * @Date   2021/8/24
 * @mobile 18711832023
 */
class SearchPagerAdapter(fm: FragmentManager, var list:MutableList<SearchFragment>): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getCount(): Int=list.size
}
