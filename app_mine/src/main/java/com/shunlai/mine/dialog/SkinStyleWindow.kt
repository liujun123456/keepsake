package com.shunlai.mine.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.im.utils.ScreenUtil
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.SkinBean
import com.shunlai.ui.DrawableManager
import kotlinx.android.synthetic.main.item_skin_layout.view.*
import kotlinx.android.synthetic.main.window_skin_style_layout.view.*
import org.jetbrains.anko.textColor

/**
 * @author Liu
 * @Date   2021/5/21
 * @mobile 18711832023
 */
class SkinStyleWindow(var mContext: Context,var mListener:SkinListener): PopupWindow()  {
    private val mAdapter by lazy {
        SkinAdapter(mContext, mutableListOf(),mListener)
    }
    private var currentSkin:SkinBean?=null
    init {
        val view= View.inflate(mContext, R.layout.window_skin_style_layout,null)
        contentView=view
        width= ViewGroup.LayoutParams.MATCH_PARENT
        height=  ViewGroup.LayoutParams.MATCH_PARENT
        isClippingEnabled=false
        isOutsideTouchable = true
        isFocusable=true
        animationStyle= R.style.PopupBottomInAnimation
        initView()
        update()
    }

    fun setData(mData:MutableList<SkinBean>,skin:SkinBean?){
        if (skin?.id?:0==0){
            contentView.ll_bg.setBackgroundResource(R.drawable.white_top_radius_15)
            contentView.tv_title.textColor=Color.parseColor("#181818")
            contentView.iv_close_window.setImageResource(R.mipmap.window_close_icon)
        }else{
            contentView.ll_bg.setBackgroundResource(R.drawable.black_top_radius_15)
            contentView.tv_title.textColor=Color.parseColor("#FFFFFF")
            contentView.iv_close_window.setImageResource(R.mipmap.mine_white_close)
        }
        currentSkin=skin
        mAdapter.mData=mData
        mAdapter.notifyDataSetChanged()
    }

    private fun initView(){
        contentView.rv_skin.layoutManager=GridLayoutManager(mContext,6)
        contentView.rv_skin.adapter=mAdapter
        contentView.iv_close_window.setOnClickListener {
            dismiss()
        }
    }

    inner class SkinAdapter(var mCtx:Context,var mData:MutableList<SkinBean>,var mListener:SkinListener):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view=View.inflate(mCtx,R.layout.item_skin_layout,null)
            val size=(ScreenUtil.getScreenWidth(mCtx)-ScreenUtil.dip2px(mCtx,16f).times(2)).div(6)
            view.layoutParams= ViewGroup.LayoutParams(size,size)
            return SkinViewHolder(view)
        }

        override fun getItemCount(): Int=mData.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as SkinViewHolder
            holder.setData(mData[position])
            holder.itemView.setOnClickListener {
                mListener.onSkinChoose(mData[position])
            }
        }

    }


    inner class SkinViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        @SuppressLint("Range")
        fun setData(bean:SkinBean){
            mView.rl_bg.background=DrawableManager.buildStyleDrawable(Color.parseColor("#${bean.top}"),Color.parseColor("#${bean.bottom}"))
        }
    }

    interface SkinListener{
        fun onSkinChoose(bean: SkinBean)
    }
}
