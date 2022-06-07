package com.shunlai.mine.account.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.TokenScoreBean
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_token_score_layout.view.*

/**
 * @author Liu
 * @Date   2021/5/28
 * @mobile 18711832023
 */
class TokenListAdapter(var mContext:Context,var mData:MutableList<TokenScoreBean>):SRecyclerAdapter(mContext) {
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext,R.layout.item_token_score_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        return TokenListViewHolder(view)

    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as TokenListViewHolder
        viewHolder.setData(mData[position])
    }

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text="没有token明细！"
        return DefaultLoadMoreViewHolder(view)
    }

    override fun getCount(): Int=mData.size

    class TokenListViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:TokenScoreBean){
            mView.tv_score_name.text=bean.desc
            mView.tv_score_time.text=bean.createTime
            if (bean.growth?:0>=0){
                mView.tv_score_num.text=bean.growth.toString()
                mView.tv_score_num.setTextColor(Color.parseColor("#0d0d0d"))
            }else{
                mView.tv_score_num.text=bean.growth.toString()
                mView.tv_score_num.setTextColor(Color.parseColor("#999999"))
            }
        }
    }
}
