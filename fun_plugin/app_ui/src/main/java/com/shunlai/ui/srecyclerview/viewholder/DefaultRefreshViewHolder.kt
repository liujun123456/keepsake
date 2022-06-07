package com.shunlai.ui.srecyclerview.viewholder

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.TimeUtil
import com.shunlai.ui.srecyclerview.SRecyclerView
import com.shunlai.ui.srecyclerview.ScreenUtils
import kotlinx.android.synthetic.main.refresh_header_view.view.*

/**
 * @author Liu
 * @Date   2019-11-26
 * @mobile 18711832023
 */
class DefaultRefreshViewHolder(var view:View):RefreshViewHolder(view){


    @SuppressLint("SetTextI18n")
    override fun notifyHeight(height: Int, status: Int) {
        super.notifyHeight(height, status)
        val lastTime=PreferenceUtil.getString("lastRefreshTime")
        if (TextUtils.isEmpty(lastTime)){
            view.tv_last_refresh_time.text="上次刷新时间：暂无"
        }else{
            view.tv_last_refresh_time.text="上次刷新时间：$lastTime"
        }
        if (status== SRecyclerView.Refresh){
            view.iv_label.visibility=View.GONE
            view.progressBar1.visibility=View.VISIBLE
            PreferenceUtil.putString("lastRefreshTime",TimeUtil.getTime(System.currentTimeMillis()))
        }else{
            view.iv_label.visibility=View.VISIBLE
            view.progressBar1.visibility=View.GONE
            if (height>ScreenUtils.dip2px(view.context,64f)){
                view.tv_refresh.text="松开立即刷新"
                if (view.iv_label.rotation==0f){
                    doRotation(180f)
                }
            }else{
                view.tv_refresh.text="下拉可以刷新"
                if (view.iv_label.rotation==180f){
                    doRotation(0f)
                }
            }
        }
    }

    private fun doRotation(rotation:Float){
        view.iv_label.animate().rotation(rotation)
    }
}
