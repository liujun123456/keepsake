package com.shunlai.mine.shop.impression

import android.graphics.Color
import android.graphics.Typeface
import androidx.viewpager.widget.ViewPager
import com.shunlai.common.BaseActivity
import com.shunlai.mine.R
import com.shunlai.mine.shop.impression.adapter.ImpressionPagerAdapter
import kotlinx.android.synthetic.main.activity_shop_impression_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*

/**
 * @author Liu
 * @Date   2021/7/7
 * @mobile 18711832023
 */
class ShopImpressionActivity:BaseActivity() {
    override fun getMainContentResId(): Int =R.layout.activity_shop_impression_layout

    override fun getToolBarResID(): Int = R.layout.public_title_layout

    override fun setTitleColor(): Int=R.color.gray_f7

    override fun afterView() {
        pager_impression.adapter= ImpressionPagerAdapter(supportFragmentManager)
        pager_impression.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                updateTab(position)
            }
        })
        tv_receiver.setOnClickListener {
            pager_impression.currentItem=0
        }
        tv_send.setOnClickListener {
            pager_impression.currentItem=1
        }
        ll_back.setOnClickListener {
            finish()
        }

    }

    private fun updateTab(position:Int){
        if (position==0){
            tv_receiver.setTextColor(Color.parseColor("#000000"))
            tv_receiver.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            tv_receiver.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.division_bg)
            tv_send.setTextColor(Color.parseColor("#999999"))
            tv_send.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
            tv_send.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        }else{
            tv_receiver.setTextColor(Color.parseColor("#999999"))
            tv_receiver.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
            tv_receiver.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            tv_send.setTextColor(Color.parseColor("#000000"))
            tv_send.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            tv_send.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.division_bg)
        }
    }
}
