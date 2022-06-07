package com.shunlai.main.ht.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.main.R
import com.shunlai.main.entities.HuaTiBean
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.item_huati_layout.view.*

/**
 * @author Liu
 * @Date   2021/5/6
 * @mobile 18711832023
 */
class HuaTiAdapter(var mContext:Context,var mData:MutableList<HuaTiBean>,var type:Int=0):RecyclerView.Adapter<HuaTiAdapter.HuaTiViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HuaTiViewHolder {
        val view=View.inflate(mContext,R.layout.item_huati_layout,null)
        if (type==0){
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams(ScreenUtils.dip2px(mContext,96f),ScreenUtils.dip2px(mContext,128f)))
        }else{
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getRealHeight())

        }
        return HuaTiViewHolder(view)
    }

    override fun getItemCount(): Int=mData.size

    override fun onBindViewHolder(holder: HuaTiViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.HT_DETAIL]=GsonUtil.toJson(mData[position])
            RouterManager.startActivityWithParams(BundleUrl.HUA_TI_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
        }
        holder.setData(mData[position])
    }

    inner class HuaTiViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        @SuppressLint("SetTextI18n")
        fun setData(bean:HuaTiBean){
            mView.tv_ht_name.text = bean.tag
            mView.tv_ht_name.post {
                mView.tv_ht_name.isSelected=true
            }
            mView.tv_hot_member.text="${bean.pnum}人参与"
            if (type==0){
                mView.tv_hot_member.visibility=View.GONE
            }else{
                mView.tv_hot_member.visibility=View.VISIBLE
            }
            if (bean.activity==true){
                mView.ll_activity.visibility=View.VISIBLE
                (mView.iv_activity.drawable as AnimationDrawable).start()
            }else{
                mView.ll_activity.visibility=View.GONE
                (mView.iv_activity.drawable as AnimationDrawable).stop()
            }

            ImageUtil.showRoundImgWithStringAndRadius(mView.iv_ht_img,mView.context,bean.homeImgUrl?:"",16f)
        }
    }

    private fun getRealHeight():Int{
        val itemWidth=(ScreenUtils.getScreenWidth(mContext)-ScreenUtils.dip2px(mContext,16f).times(4)).div(3)
        return itemWidth.times(128).div(96)
    }
}
