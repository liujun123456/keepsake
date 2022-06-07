package com.shunlai.ugc.search

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.router.RouterManager
import com.shunlai.ugc.R
import com.shunlai.ugc.entity.UgcSearchBean
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_ugc_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class UgcSearchAdapter(var mContext:Context,var mData:MutableList<UgcSearchBean>):SRecyclerAdapter(mContext) {

    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext,R.layout.item_ugc_layout,null)
        return UgcSearchViewHolder(view)
    }

    override fun onBindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as UgcSearchViewHolder
        holder.setData(mData[position])
        holder.itemView.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.UGC_ID]= mData[position].id
            params[RunIntentKey.UGC_TYPE]= mData[position].ugcType.toString()
            RouterManager.startActivityWithParams(BundleUrl.UGC_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
        }
    }

    override fun getCount(): Int=mData.size

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text="搜索结果为空"
        view.findViewById<ImageView>(R.id.iv_empty_image).setImageResource(R.mipmap.empty_search_image)
        return DefaultLoadMoreViewHolder(view)
    }


    class UgcSearchViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:UgcSearchBean){
            mView.tv_ugc_content.text=bean.content
            mView.tv_user_name.text=bean.nickName
            ImageUtil.showRoundImgWithStringAndRadius(mView.ugc_img,mView.context,bean.images?:"",16f,
                leftTop = true,
                rightTop = true,
                leftBottom = false,
                rightBottom = false
            )
            ImageUtil.showCircleImgWithString(mView.iv_user_avatar,mView.context,bean.avatar?:"",R.mipmap.user_default_icon)
            if (TextUtils.isEmpty(bean.likes)||bean.likes=="0"){
                mView.tv_fabulous_count.text=""
                mView.iv_fabulous_label.visibility=View.INVISIBLE
            }else{
                mView.tv_fabulous_count.text=bean.likes
                mView.iv_fabulous_label.visibility=View.VISIBLE
            }
        }
    }
}
