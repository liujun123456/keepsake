package com.shunlai.mine.tokenStar.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunCacheDataUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.BrandBean
import kotlinx.android.synthetic.main.item_brand_choose_layout.view.*

/**
 * @author Liu
 * @Date   2021/8/12
 * @mobile 18711832023
 */
class BrandChooseAdapter(var mContext:Context,var mData:MutableList<BrandBean>,var mListener:OnBrandClickListener):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       val view= View.inflate(mContext,R.layout.item_brand_choose_layout,null)
        return BrandChooseViewHolder(view)
    }

    override fun getItemCount(): Int=mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as BrandChooseViewHolder
        holder.setData(mData[position])
        holder.itemView.setOnClickListener {
            mListener.onBrandClick(mData[position],position)
        }
    }

    inner class BrandChooseViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:BrandBean){
            mView.tv_brand_name.text=bean.brandName
            if (TextUtils.isEmpty(bean.logo)){
                mView.iv_brand.visibility=View.GONE
            }else{
                mView.iv_brand.visibility=View.VISIBLE
                ImageUtil.showRoundImgWithStringAndRadius(mView.iv_brand,mView.context,bean.logo,8f)
            }

            if (RunCacheDataUtil.brandChooseData.contains(bean.brandCode)){
                mView.ll_brand_bg.setBackgroundResource(R.drawable.brand_choose_bg)
            }else{
                mView.ll_brand_bg.setBackgroundResource(R.drawable.brand_un_choose_bg)
            }
        }
    }

    interface OnBrandClickListener{
        fun onBrandClick(bean:BrandBean,position: Int)
    }

}
