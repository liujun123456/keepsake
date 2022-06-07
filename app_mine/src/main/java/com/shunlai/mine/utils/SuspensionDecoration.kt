package com.shunlai.mine.utils

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.entity.bean.ImpressionBean

/**
 * @author Liu
 * @Date   2021/7/7
 * @mobile 18711832023
 */
class SuspensionDecoration(var mContext:Context,var mData:MutableList<ImpressionBean>): ItemDecoration() {
    private var titleHeight=0
    private val fontColor: Int = Color.parseColor("#FF000000")
    private var mPaint: Paint? = null
    private var mBounds: Rect? = null
    init {
        titleHeight=ScreenUtils.dip2px(mContext,70f)
        mPaint=Paint()
        mBounds=Rect()
        mPaint?.textSize= TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            16f,
            mContext.resources.displayMetrics
        )
        mPaint?.color = fontColor
        mPaint?.typeface= Typeface.DEFAULT_BOLD
        mPaint?.isAntiAlias=true
    }

    override fun getItemOffsets(outRect: Rect,view: View,parent: RecyclerView,state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        var position=(view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        position -= 1  //SRecyclerView默认加了一个头viewHolder 所以计算时 position要减1
        if (position==0&&position<mData.size){
            outRect.set(0, titleHeight, 0, 0);
        }else if (position>0&&position<mData.size){
            if (mData[position].sendTime!=mData[position-1].sendTime){
                outRect.set(0, titleHeight, 0, 0);
            }else{
                outRect.set(0, 0, 0, 0);
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        for (i in 0 until childCount){
            val child= parent.getChildAt(i)
            var position=(child.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
            position -= 1  //SRecyclerView默认加了一个头viewHolder 所以计算时 position要减1
            if (position==0&&position<mData.size){
                c.drawText(mData[position].sendTime?:"", ScreenUtils.dip2px(mContext,16f).toFloat(), (child.top-ScreenUtils.dip2px(mContext,30f)).toFloat(), mPaint!!)
            }else if (position>0&&position<mData.size){
                if (mData[position].sendTime!=mData[position-1].sendTime){
                    c.drawText(mData[position].sendTime?:"", ScreenUtils.dip2px(mContext,16f).toFloat(), (child.top-ScreenUtils.dip2px(mContext,30f)).toFloat(), mPaint!!)
                }
            }
        }
    }
}
