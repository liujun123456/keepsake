package com.shunlai.mine.tokenStar.record

import android.graphics.Color
import androidx.viewpager.widget.ViewPager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.StatusBarUtil
import com.shunlai.mine.R
import com.shunlai.mine.tokenStar.record.adapter.RecordPagerAdapter
import kotlinx.android.synthetic.main.activity_token_record_layout.*
import org.jetbrains.anko.textColor

/**
 * @author Liu
 * @Date   2021/8/19
 * @mobile 18711832023
 */
class TokenRecordActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_token_record_layout

    override fun getToolBarResID(): Int = 0

    override fun afterView() {
        StatusBarUtil.showLightStatusBarIcon(this)
        pager_record.adapter= RecordPagerAdapter(supportFragmentManager)
        initListener()
        pager_record.currentItem=0
    }

    private fun initListener(){
        pager_record.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
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
            pager_record.currentItem=0
        }

        tv_send.setOnClickListener {
            pager_record.currentItem=1
        }
        ll_back.setOnClickListener {
            finish()
        }
    }

    private fun updateTab(position:Int){
        if (position==0){
            tv_receiver.textColor=Color.parseColor("#ffffff")
            tv_send.textColor=Color.parseColor("#999999")
            tv_receiver.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.white_division_bg)
            tv_send.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)

        }else{
            tv_receiver.textColor=Color.parseColor("#999999")
            tv_send.textColor=Color.parseColor("#ffffff")
            tv_receiver.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            tv_send.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.white_division_bg)
        }
    }
}
