package com.shunlai.main.ht.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @author Liu
 * @Date   2021/5/8
 * @mobile 18711832023
 */
class HuaTiPagerAdapter(var fm: FragmentManager, var list:MutableList<Fragment>): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = list[position]

    override fun getCount(): Int=list.size

    override fun getPageTitle(position: Int): CharSequence? {
        if (position==0){
            return "最新"
        }
        return "推荐"
    }
}
