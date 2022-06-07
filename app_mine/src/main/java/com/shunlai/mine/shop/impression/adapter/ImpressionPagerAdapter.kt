package com.shunlai.mine.shop.impression.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.shunlai.mine.shop.impression.fragment.MineReceiverImpressionFragment
import com.shunlai.mine.shop.impression.fragment.MineSendImpressionFragment

/**
 * @author Liu
 * @Date   2021/5/17
 * @mobile 18711832023
 */
class ImpressionPagerAdapter (var fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment{
        if (position==0){
            return MineReceiverImpressionFragment()
        }else{
            return MineSendImpressionFragment()
        }
    }

    override fun getCount(): Int=2
}
