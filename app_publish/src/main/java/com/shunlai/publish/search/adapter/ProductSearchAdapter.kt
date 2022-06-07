package com.shunlai.publish.search.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.publish.R
import com.shunlai.common.bean.GoodsBean
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_product_search_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/12
 * @mobile 18711832023
 */
class ProductSearchAdapter(var mContext:Context, var dates:MutableList<GoodsBean>, var listener:OnItemGoodsChooseListener):SRecyclerAdapter(mContext) {



    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext,R.layout.item_product_search_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        return ProductSearchViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as ProductSearchViewHolder
        viewHolder.setData(dates[position])
    }

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text="搜索结果为空"
        view.findViewById<ImageView>(R.id.iv_empty_image)
            .setImageResource(R.mipmap.goods_search_empty_icon)
        return DefaultLoadMoreViewHolder(view)
    }

    override fun getCount(): Int=dates.size


    inner class ProductSearchViewHolder(var view:View):RecyclerView.ViewHolder(view){
        fun setData(bean: GoodsBean){
            ImageUtil.showRoundImgWithStringAndRadius(view.iv_product_img,view.context,bean.thumb?:"",8f)
            view.tv_product_desc.text=bean.name
            view.tv_real_price.text=bean.price
            if (bean.type=="2"){
                view.tv_platform_price.setText(R.string.taobao_text)
            }else{
                view.tv_platform_price.setText(R.string.jd_text)
            }
            if (TextUtils.isEmpty(bean.shopName)){
                view.tv_shop_name.visibility=View.GONE
            }else{
                view.tv_shop_name.visibility=View.VISIBLE
                view.tv_shop_name.text=bean.shopName
            }
            view.tv_choose.setOnClickListener {
                listener.onItemGoods(bean)
            }
        }
    }

    interface OnItemGoodsChooseListener{
        fun onItemGoods(bean: GoodsBean)
    }
}
