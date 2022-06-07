package com.shunlai.mine.shop.feed.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.shunlai.mine.shop.feed.FeedRecordFragment

/**
 * @author Liu
 * @Date   2021/7/22
 * @mobile 18711832023
 */
class FeedRecordPagerAdapter(var fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        if (position==0){
            return FeedRecordFragment(0)
        }else{
            return FeedRecordFragment(1)
        }
    }

    override fun getCount(): Int=2
}
