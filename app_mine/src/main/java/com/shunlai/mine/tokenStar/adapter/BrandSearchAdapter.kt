package com.shunlai.mine.tokenStar.adapter

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunCacheDataUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.BrandBean
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_search_brand_layout.view.*

/**
 * @author Liu
 * @Date   2021/8/27
 * @mobile 18711832023
 */
class BrandSearchAdapter(var mContext:Context,var mData:MutableList<BrandBean>,var mListener:BrandSearchListener):SRecyclerAdapter(mContext) {
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view= View.inflate(mContext,R.layout.item_search_brand_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.dip2px(mContext,88f))
        return BrandSearchViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as BrandSearchViewHolder
        viewHolder.setData(mData[position],position)
    }

    override fun getCount(): Int=mData.size

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<ImageView>(R.id.iv_empty_image).setImageResource(R.mipmap.search_empty_img)
        view.findViewById<TextView>(R.id.tv_empty_value).text="没有搜到结果，会持续完善品牌库"
        view.findViewById<TextView>(R.id.tv_no_more).text="现在就这些了"
        view.findViewById<TextView>(R.id.tv_no_more).setTextColor(Color.parseColor("#B2B2B2"))
        return DefaultLoadMoreViewHolder(view)
    }

    inner class BrandSearchViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean: BrandBean,position: Int){
            if (TextUtils.isEmpty(bean.logo)){
                mView.iv_brand.visibility=View.GONE
            }else{
                mView.iv_brand.visibility=View.VISIBLE
                ImageUtil.showRoundImgWithStringAndRadius(mView.iv_brand,mView.context,bean.logo,8f)
            }
            mView.tv_brand_name.text=bean.brandName
            if (RunCacheDataUtil.brandChooseData.contains(bean.brandCode)){
                mView.tv_brand_state.setBackgroundResource(R.drawable.gray_empty_radius_20)
                mView.tv_brand_state.text="已选"
            }else{
                mView.tv_brand_state.text="选ta"
                mView.tv_brand_state.setBackgroundResource(R.drawable.gray_24_radius_bg)
            }
            mView.tv_brand_state.setOnClickListener {
                mListener.onItemClick(bean,position)
            }
        }
    }
    interface BrandSearchListener{
        fun onItemClick(bean: BrandBean,position:Int)

    }
}
