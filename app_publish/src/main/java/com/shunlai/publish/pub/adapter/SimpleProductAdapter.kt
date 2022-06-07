package com.shunlai.publish.pub.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.publish.R
import com.shunlai.common.bean.GoodsBean
import kotlinx.android.synthetic.main.item_product_simple_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/12
 * @mobile 18711832023
 */
class SimpleProductAdapter(var mContext:Context,var signGoods:MutableList<GoodsBean>):RecyclerView.Adapter<SimpleProductAdapter.SimpleProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleProductViewHolder {
        val view=View.inflate(mContext, R.layout.item_product_simple_layout,null)
        if (signGoods.size==1){
            view.layoutParams= ViewGroup.LayoutParams(ScreenUtils.getScreenWidth(mContext)-ScreenUtils.dip2px(mContext,16f),
                ScreenUtils.dip2px(mContext,80f))
        }else{
            view.layoutParams= ViewGroup.LayoutParams(ScreenUtils.dip2px(mContext,283f),ScreenUtils.dip2px(mContext,80f))
        }
        return SimpleProductViewHolder(view)
    }

    override fun getItemCount(): Int=signGoods.size

    override fun onBindViewHolder(holder: SimpleProductViewHolder, position: Int) {
        holder.setData(signGoods[position])
    }

    class SimpleProductViewHolder(var view:View):RecyclerView.ViewHolder(view){
        @SuppressLint("SetTextI18n")
        fun setData(bean: GoodsBean){
            ImageUtil.showRoundImgWithStringAndRadius(view.iv_product_img,view.context,bean.thumb?:"",8f)
            if (TextUtils.isEmpty(bean.brandName)){
                view.tv_product_desc.text=bean.name
            }else{
                if (TextUtils.isEmpty(bean.name)){
                    view.tv_product_desc.text=bean.brandName
                }else{
                    view.tv_product_desc.text="${bean.brandName} ${bean.name}"
                }

            }
            view.star_view.setRating(bean.evaluate?:0)
        }

    }
}
