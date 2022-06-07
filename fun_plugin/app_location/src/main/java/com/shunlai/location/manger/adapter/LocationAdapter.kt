package com.shunlai.location.manger.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.location.R
import com.shunlai.location.entity.LocationBean
import kotlinx.android.synthetic.main.item_loc_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
class LocationAdapter(var mContext: Context,var mData:MutableList<LocationBean>,var mListener:LocalActionListener):RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
       val view:View=View.inflate(mContext, R.layout.item_loc_layout,null)
        return LocationViewHolder(view)
    }

    override fun getItemCount(): Int=mData.size

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.setData(mData[position],position)
        holder.itemView.setOnClickListener {
            mListener.onItemClick(mData[position])
        }
    }

    inner class LocationViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean :LocationBean,position: Int){
            mView.tv_name.text=bean.contact
            mView.tv_phone.text=bean.phone
            mView.tv_address.text=bean.address
            if (bean.isDefault==1){
                mView.tv_set_default.setCompoundDrawablesWithIntrinsicBounds(R.drawable.black_oval_bg,0,0,0)
            }else{
                mView.tv_set_default.setCompoundDrawablesWithIntrinsicBounds(R.drawable.empty_black_oval_bg,0,0,0)
            }
            mView.tv_set_default.setOnClickListener {
                mListener.setDefault(bean)
            }
            mView.ll_remove.setOnClickListener {
                mListener.onRemoveLoc(bean,position)
            }
            mView.tv_edit.setOnClickListener {
                mListener.onItemEdit(bean)
            }
        }
    }


    interface LocalActionListener{
        fun onRemoveLoc(bean:LocationBean,position: Int)

        fun setDefault(bean:LocationBean)

        fun onItemEdit(bean:LocationBean)

        fun onItemClick(bean:LocationBean)
    }

}
