package com.shunlai.main.ht.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.main.R
import com.shunlai.main.entities.HuaTiBean
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.item_huati_layout2.view.*

/**
 * @author Liu
 * @Date   2021/5/6
 * @mobile 18711832023
 */
class HuaTiAdapter2(var mContext:Context, var mData:MutableList<HuaTiBean>):RecyclerView.Adapter<HuaTiAdapter2.HuaTiViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HuaTiViewHolder {
        val view=View.inflate(mContext,R.layout.item_huati_layout2,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ScreenUtils.dip2px(mContext,40f)))
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
            if (bean.activity==true){
                mView.tv_ht_name.text = bean.tag
                mView.iv_activity.visibility=View.VISIBLE
                (mView.iv_activity.drawable as AnimationDrawable).start()
            }else{
                mView.tv_ht_name.text = "# ${bean.tag}"
                mView.iv_activity.visibility=View.GONE
                (mView.iv_activity.drawable as AnimationDrawable).stop()
            }

            mView.tv_ht_name.post {
                mView.tv_ht_name.isSelected=true
                val params=mView.layoutParams
                params.width=mView.tv_ht_name.measuredWidth+ScreenUtils.dip2px(mContext,36f)
                mView.layoutParams=params
                ImageUtil.showRoundImgWithStringAndRadius(mView.iv_ht_img,mView.context,bean.homeImgUrl?:"",12f)
            }

        }
    }

}
