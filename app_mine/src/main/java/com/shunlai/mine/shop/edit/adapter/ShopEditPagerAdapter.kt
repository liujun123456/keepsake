package com.shunlai.mine.shop.edit.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @author Liu
 * @Date   2021/7/14
 * @mobile 18711832023
 */
class ShopEditPagerAdapter  (fm: FragmentManager, var list:MutableList<Fragment>): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = list[position]

    override fun getCount(): Int=list.size
}
