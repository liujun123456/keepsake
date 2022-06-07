package com.shunlai.mine.shop.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @author Liu
 * @Date   2021/7/9
 * @mobile 18711832023
 */
class ShopGoodsPagerAdapter (fm: FragmentManager, var list:MutableList<Fragment>): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = list[position]

    override fun getCount(): Int=list.size
}
