package com.shunlai.mine.shop.edit.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.SceneListBean
import kotlinx.android.synthetic.main.item_shop_bg_layout.view.*

/**
 * @author Liu
 * @Date   2021/7/14
 * @mobile 18711832023
 */
class ShopBgAdapter(var mContext:Context,var sceneList:MutableList<SceneListBean>,var mListener:SceneListener):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view= View.inflate(mContext,R.layout.item_shop_bg_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        return ShopBgViewHolder(view)
    }

    override fun getItemCount(): Int=sceneList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ShopBgViewHolder
        holder.setData(sceneList[position])
        holder.itemView.setOnClickListener {
            mListener.onSceneClick(position)
        }
    }

    class ShopBgViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:SceneListBean){
            val layoutParams=mView.rl_content.layoutParams
            val width=(ScreenUtils.getScreenWidth(mView.context)- ScreenUtils.dip2px(mView.context,152f))/3
            val height=width.times(96).div(72)
            layoutParams.width=width
            layoutParams.height=height
            mView.rl_content.layoutParams=layoutParams

            if (bean.buyFlag=="1"){
                mView.ll_score.visibility=View.INVISIBLE
            }else{
                mView.ll_score.visibility=View.VISIBLE
                mView.tv_score.text=bean.tokenPrice
            }

            if (bean.selectedFlag=="1"){
                mView.rl_content.setBackgroundResource(R.drawable.shop_select_bg)
            }else{
                mView.rl_content.setBackgroundResource(0)
            }

            mView.tv_scene.text=bean.name
            ImageUtil.showRoundImgWithStringAndRadius( mView.iv_scene,mView.context,bean.iconUrl,12f)
        }
    }

    interface SceneListener{
        fun onSceneClick(position: Int)
    }
}
