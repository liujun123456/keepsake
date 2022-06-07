package com.shunlai.mine.order.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.OrderBean
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_order_layout.view.*

/**
 * @author Liu
 * @Date   2021/5/24
 * @mobile 18711832023
 */
class OrderAdapter(var mContext:Context,var data:MutableList<OrderBean>):SRecyclerAdapter(mContext) {
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view= View.inflate(mContext,R.layout.item_order_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        return OrderViewHolder(view)
    }

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text="没有订单信息！"
        return DefaultLoadMoreViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as OrderViewHolder
        viewHolder.setData(data[position])
    }

    override fun getCount(): Int=data.size

    class OrderViewHolder(var mView:View) :RecyclerView.ViewHolder(mView){
        @SuppressLint("SetTextI18n")
        fun setData(bean:OrderBean){
            mView.tv_order_no.text="订单号：${bean.orderNo}"
            mView.tv_goods_name.text=bean.skuName
            mView.tv_price.text=bean.price
            mView.tv_goods_num.text="共${bean.skuNum}件商品，合计"
            mView.tv_all_price.text=bean.orderAmount
            mView.order_time.text=bean.orderTime
            ImageUtil.showRoundImgWithStringAndRadius(mView.iv_product_img,mView.context,bean.skuImage?:"",3f)
            mView.to_evaluate.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.ORDER_BEAN]=GsonUtil.toJson(bean)
                RouterManager.startActivityWithParams(BundleUrl.SIGN_ORDER_ACTIVITY,mView.context as FragmentActivity,params)

            }
        }
    }
}
