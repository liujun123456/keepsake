package com.shunlai.mine.shop.feed

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import androidx.viewpager.widget.ViewPager
import com.shunlai.common.BaseActivity
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.FeedRecordEvent
import com.shunlai.mine.shop.feed.adapter.FeedRecordPagerAdapter
import kotlinx.android.synthetic.main.activity_feed_record_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author Liu
 * @Date   2021/7/22
 * @mobile 18711832023
 */
class FeedRecordActivity:BaseActivity() {
    override fun getMainContentResId(): Int = R.layout.activity_feed_record_layout

    override fun getToolBarResID(): Int = R.layout.public_title_layout

    override fun setTitleColor(): Int= R.color.gray_f7

    override fun afterView() {
        EventBus.getDefault().register(this)
        pager_feed.adapter= FeedRecordPagerAdapter(supportFragmentManager)
        pager_feed.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
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
            pager_feed.currentItem=0
        }
        tv_send.setOnClickListener {
            pager_feed.currentItem=1
        }
        ll_back.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: FeedRecordEvent){
        tv_total_record.text="收到${event.size}次电池投喂"
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
