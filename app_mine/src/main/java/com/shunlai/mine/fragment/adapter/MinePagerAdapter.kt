package com.shunlai.mine.fragment.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.shunlai.mine.fragment.ThemeFragment

/**
 * @author Liu
 * @Date   2021/5/17
 * @mobile 18711832023
 */
class MinePagerAdapter (fm: FragmentManager, var list:MutableList<ThemeFragment>): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = list[position]

    override fun getCount(): Int=list.size
}
