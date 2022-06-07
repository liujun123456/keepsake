package com.shunlai.main.ht.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.main.R
import com.shunlai.main.entities.HuaTiBean
import com.shunlai.ui.MediaGridInset
import kotlinx.android.synthetic.main.item_all_ht_layout.view.*
import kotlinx.android.synthetic.main.item_hot_ht_layout.view.*

/**
 * @author Liu
 * @Date   2021/7/6
 * @mobile 18711832023
 */
class HuaTiListAdapter(var mContext:Context,var hotList:MutableList<HuaTiBean>,var allList:MutableList<HuaTiBean>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==10086){
            val view=View.inflate(mContext,R.layout.item_hot_ht_layout,null)
            return HotHtViewHolder(view)
        }else{
            val view=View.inflate(mContext,R.layout.item_all_ht_layout,null)
            return AllHtViewHolder(view)
        }

    }

    override fun getItemCount(): Int =2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HotHtViewHolder){
            holder.setData(hotList)
        }else if (holder is AllHtViewHolder){
            holder.setData(allList)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position==0){
            10086
        }else{
            10087
        }
    }

    fun setHotAdapter(mData:MutableList<HuaTiBean>){
        hotList=mData
        notifyItemChanged(0)
    }

    fun setAllAdapter(mData:MutableList<HuaTiBean>){
        allList=mData
        notifyItemChanged(1)
    }



    class HotHtViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(mData:MutableList<HuaTiBean>){
            mView.rv_recommend_hua_ti.layoutManager= GridLayoutManager(mView.context,3)
            mView.rv_recommend_hua_ti.addItemDecoration(
                MediaGridInset(3,
                    ScreenUtils.dip2px(mView.context,16f),false,true)
            )
            mView.rv_recommend_hua_ti.adapter=HuaTiAdapter(mView.context,mData,1)
        }

    }

    class AllHtViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(mData:MutableList<HuaTiBean>){
            mView.rv_all_hua_ti.layoutManager= GridLayoutManager(mView.context,3)
            mView.rv_all_hua_ti.addItemDecoration(
                MediaGridInset(3,
                    ScreenUtils.dip2px(mView.context,16f),false,true)
            )
            mView.rv_all_hua_ti.adapter=HuaTiAdapter(mView.context,mData,1)
        }

    }
}
