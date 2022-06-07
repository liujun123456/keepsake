package com.shunlai.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.shunlai.main.fragment.TabUgcFragment
import com.shunlai.main.fragment.attention.AttentionFragment
import com.shunlai.main.fragment.discover.DiscoverFragment

/**
 * @author Liu
 * @Date   2021/5/6
 * @mobile 18711832023
 */
class HomePagerAdapter(var fm: FragmentManager, var list:MutableList<String>): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        if (position==0){
            return DiscoverFragment()
        }else if (position==1){
            return AttentionFragment()
        }else{
            return TabUgcFragment(list[position-2])
        }
    }

    override fun getCount(): Int=list.size+2

    override fun getPageTitle(position: Int): CharSequence? {
        if (position==0){
            return "推荐"
        }else if (position==1){
            return "关注"
        }else {
            return list[position-2]
        }
    }
}
