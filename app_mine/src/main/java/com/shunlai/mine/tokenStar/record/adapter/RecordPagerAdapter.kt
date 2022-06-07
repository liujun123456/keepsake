package com.shunlai.mine.tokenStar.record.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.shunlai.mine.tokenStar.record.TokenRecordFragment

/**
 * @author Liu
 * @Date   2021/8/26
 * @mobile 18711832023
 */
class RecordPagerAdapter(var fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        if (position==0){
            return TokenRecordFragment(0)
        }else{
            return TokenRecordFragment(1)
        }
    }

    override fun getCount(): Int=2
}
