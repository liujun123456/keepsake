 package com.shunlai.mine.fragment.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.*
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.SkinBean
import com.shunlai.mine.entity.bean.UgcGoodsBean
import com.shunlai.router.RouterManager
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_mine_goods_layout.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

/**
 * @author Liu
 * @Date   2021/5/19
 * @mobile 18711832023
 */
class MineGoodsAdapter(var mContext:Context,var mData:MutableList<UgcGoodsBean>,var isSelf:Boolean): SRecyclerAdapter(mContext) {
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext,R.layout.item_mine_goods_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        return MineGoodsViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as MineGoodsViewHolder
        viewHolder.setData(mData[position])
        viewHolder.itemView.setOnClickListener {
            if(mData[position].type=="3"){
                toast("京东商品，暂未开通哦")
                return@setOnClickListener
            }
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.UGC_ID]=mData[position].ugcId
            params[RunIntentKey.PRODUCT_ID]= mData[position].productId
            RouterManager.startActivityWithParams(BundleUrl.UGC_GOODS_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
        }
    }

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<ImageView>(R.id.iv_empty_image).setImageResource(R.mipmap.goods_empty_icon)
        if (isSelf){
            view.findViewById<TextView>(R.id.tv_empty_value).text="您的橱窗还没有任何东西哦"
            view.findViewById<TextView>(R.id.tv_no_more).text="现在就这些了"
            view.findViewById<TextView>(R.id.tv_no_more).setTextColor(Color.parseColor("#B2B2B2"))
        }else{
            view.findViewById<TextView>(R.id.tv_empty_value).text="他的橱窗还没有任何东西哦"
            view.findViewById<TextView>(R.id.tv_no_more).text="现在就这些了"
            view.findViewById<TextView>(R.id.tv_no_more).setTextColor(Color.parseColor("#B2B2B2"))
        }
        return DefaultLoadMoreViewHolder(view)
    }

    override fun getCount(): Int=mData.size

    inner class MineGoodsViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:UgcGoodsBean){
            mView.iv_comment_img.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,getRealImgSize())
            ImageUtil.showRoundImgWithStringAndRadius(mView.iv_comment_img,mView.context,bean.productImg?:"",16f)
            mView.tv_comment_title.text=bean.productName
            mView.tv_price.text=bean.price
            mView.tv_price.setTextColor(Color.parseColor("#${RunCacheDataUtil.THEME_COLOR}"))
            mView.tv_price_label.setTextColor(Color.parseColor("#${RunCacheDataUtil.THEME_COLOR}"))
            mView.sl_layout.setRating(bean.evaluate?:0)
        }

        private fun getRealImgSize():Int{
            val userWidth=ScreenUtils.getScreenWidth(mContext)-ScreenUtils.dip2px(mContext,40f)-ScreenUtils.dip2px(mContext,8f)
            return userWidth.div(2)
        }
    }
}
