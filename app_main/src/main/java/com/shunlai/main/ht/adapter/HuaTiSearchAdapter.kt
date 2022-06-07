package com.shunlai.main.ht.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.main.R
import com.shunlai.main.entities.HuaTiBean
import kotlinx.android.synthetic.main.item_search_hua_ti_layout.view.*

/**
 * @author Liu
 * @Date   2021/5/10
 * @mobile 18711832023
 */
class HuaTiSearchAdapter(var mContext:Context,var mData:MutableList<HuaTiBean>,var mListener:KeywordItemClick):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext,R.layout.item_search_hua_ti_layout,null)
        return HuaTiSearchViewHolder(view)

    }

    override fun getItemCount(): Int=mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as HuaTiSearchViewHolder
        holder.setData(mData[position])
        holder.itemView.setOnClickListener {
            mListener.onItemClick(mData[position])
        }
    }

    class HuaTiSearchViewHolder(var view:View) :RecyclerView.ViewHolder(view){
        @SuppressLint("SetTextI18n")
        fun setData(bean:HuaTiBean){
            if (bean.activity==true){
                view.tv_label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.activity_loading_bg,0,0,0)
                (view.tv_label.compoundDrawables[0] as AnimationDrawable).start()
                view.tv_label.text=" ${bean.tag}"
            }else{
                view.tv_label.text="# ${bean.tag}"
            }

        }
    }

    interface KeywordItemClick{
        fun onItemClick(bean:HuaTiBean)

    }

}
