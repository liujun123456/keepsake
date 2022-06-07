package com.shunlai.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes

/**
 * @author Liu
 * @Date   2021/4/13
 * @mobile 18711832023
 */
class StarLayout: ViewGroup {

    private var starWidth = 0

    private var starHeight = 0

    private var childMargin=0

    private var mContext:Context

    private var chooseIcon:Drawable?=null

    private var unChooseIcon:Drawable?=null

    private var maxStar=0

    private var rating=0

    private var isIndicator=false

    constructor(mCtx:Context):super(mCtx){
        mContext=mCtx
    }
    constructor(mCtx:Context, attrs: AttributeSet):super(mCtx,attrs){
        mContext=mCtx
        initView(attrs)
    }
    constructor(mCtx:Context, attrs: AttributeSet, defStyleAttr: Int):super(mCtx,attrs,defStyleAttr){
        mContext=mCtx
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet){
        val typedArray: TypedArray = mContext.obtainStyledAttributes(attrs, R.styleable.StarLayout)
        maxStar=typedArray.getInt(R.styleable.StarLayout_maxStar,5)
        chooseIcon=typedArray.getDrawable(R.styleable.StarLayout_chooseIcon)
        unChooseIcon=typedArray.getDrawable(R.styleable.StarLayout_unChooseIcon)
        starWidth=typedArray.getDimension(R.styleable.StarLayout_star_width,13f).toInt()
        starHeight=typedArray.getDimension(R.styleable.StarLayout_star_height,13f).toInt()
        childMargin=typedArray.getDimension(R.styleable.StarLayout_child_margin,5f).toInt()
        rating=typedArray.getInt(R.styleable.StarLayout_rating,0)
        isIndicator=typedArray.getBoolean(R.styleable.StarLayout_isIndicator,false)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth=starWidth*maxStar+childMargin*(maxStar-1)
        setMeasuredDimension(mWidth, starHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        removeAllViews()
        addChild()
        for (pos in 0 until maxStar) {
            layoutChildView(getChildAt(pos),pos)
        }
    }

    private fun layoutChildView(child:View,position:Int){
        if (position==0){
            child.layout(0,0,starWidth,starHeight)
        }else{
            child.layout((starWidth+childMargin)*position,0,(starWidth+childMargin)*position+starWidth,starHeight)
        }
    }

    private fun addChild(){
        for (pos in 0 until maxStar) {
            addView(buildChild(pos))
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun buildChild(position: Int):ImageView{
        val imageView= ImageView(mContext)
        imageView.layoutParams= LayoutParams(starWidth,starHeight)
        if (rating==0){
            imageView.setImageDrawable(unChooseIcon)
        }else{
            if (position<rating){
                imageView.setImageDrawable(chooseIcon)
            }else{
                imageView.setImageDrawable(unChooseIcon)
            }
        }
        imageView.setOnTouchListener { v, event ->
            if (!isIndicator){
                rating=position+1
                mListener?.onRatingChange(rating)
                requestLayout()
            }
            false
        }
        return imageView
    }

    fun setRating(rating:Int){
        this.rating=rating
        mListener?.onRatingChange(rating)
        requestLayout()
    }

    fun setIcon(chooseIcon:Drawable,unChooseIcon:Drawable){
        this.chooseIcon=chooseIcon
        this.unChooseIcon=unChooseIcon
        requestLayout()
    }

    fun setRatingChangeListener(mListener:OnRatingChangeListener){
        this.mListener=mListener
    }

    var mListener:OnRatingChangeListener?=null

    interface OnRatingChangeListener{
        fun onRatingChange(rating:Int)
    }
}
