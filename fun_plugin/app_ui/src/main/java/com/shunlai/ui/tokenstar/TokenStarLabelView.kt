package com.shunlai.ui.tokenstar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.common.utils.toast
import com.shunlai.ui.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author Liu
 * @Date   2021/8/13
 * @mobile 18711832023
 */
@SuppressLint("HandlerLeak")
class TokenStarLabelView:View {
    private val paintBgCircle=Paint()
    private val paintPointCircle=Paint()
    private val mPoints= mutableListOf<StarPointBean>()
    private var mWidth:Int=0
    private var mHeight:Int=0
    private var outerRadius:Float=0f
    private var innerRadius:Float=0f
    private val REFRESH_MSG=1008611
    private var isAutoRotate=true

    private val mHandler= object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what==REFRESH_MSG){
                countAngle()
                sendEmptyMessageDelayed(REFRESH_MSG,30)
            }
        }
    }

    constructor(mContext:Context):super(mContext)
    constructor(mContext: Context, attrs: AttributeSet):super(mContext,attrs){
        initView(attrs)
    }

    constructor(mContext: Context, attrs: AttributeSet, defStyleAttr: Int):super(mContext,attrs,defStyleAttr){
        initView(attrs)
    }


    fun initView(attrs: AttributeSet) {
        val typedArray=context.obtainStyledAttributes(attrs,R.styleable.TokenStarView)
        isAutoRotate=typedArray.getBoolean(R.styleable.TokenStarView_is_auto_rotate,true)
        paintBgCircle.color=Color.parseColor("#40FFFFFF")
        paintBgCircle.isAntiAlias=true
        paintBgCircle.style= Paint.Style.STROKE
        paintBgCircle.strokeWidth=ScreenUtils.dip2px(context,1f).toFloat()
        paintBgCircle.isDither = true

        paintPointCircle.color=Color.parseColor("#A6FFFFFF")
        paintPointCircle.isAntiAlias=true
        paintPointCircle.style= Paint.Style.FILL
        paintPointCircle.strokeWidth=1f
        paintPointCircle.textAlign= Paint.Align.CENTER
        paintPointCircle.isDither = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth=MeasureSpec.getSize(widthMeasureSpec)
        mHeight=mWidth
        initPointData()
        setMeasuredDimension(mWidth,mWidth)
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            //draw 外环圆
            it.drawCircle(width.toFloat()/2,height.toFloat()/2,outerRadius,paintBgCircle)

            //draw 内环圆
            it.drawCircle(width.toFloat()/2,height.toFloat()/2,innerRadius,paintBgCircle)

            mPoints.forEach { bean->
                it.drawCircle(bean.pointX,bean.pointy,ScreenUtils.dip2px(context,2f).toFloat(),paintPointCircle)
            }
        }
    }

    fun initPointData(){
        mPoints.clear()
        outerRadius=(mWidth/2).times(0.85).toFloat()
        innerRadius=(mWidth/2).times(0.5).toFloat()
        mPoints.add(StarPointBean(45.0))
        mPoints.add(StarPointBean(90.0))
        mPoints.add(StarPointBean(270.0))
        countPoint()
        onStart()
    }

    private fun countAngle(){
        mPoints.forEachIndexed { index, starPointBean ->
            if (index==1||index==2){
                var realValue= starPointBean.angle+1.5
                if (realValue<=0){
                    realValue=360.0
                }
                starPointBean.angle=realValue
            }else{
                var realValue= starPointBean.angle-1.5
                if (realValue>=360){
                    realValue=0.0
                }
                starPointBean.angle=realValue
            }
        }
        countPoint()
    }

    private fun countPoint(){
        mPoints.forEachIndexed { index, starPointBean ->
            if (index==0){
                starPointBean.pointX=getPointXFromAngle(innerRadius,starPointBean.angle)
                starPointBean.pointy=getPointYFromAngle(innerRadius,starPointBean.angle)
            }else{
                starPointBean.pointX=getPointXFromAngle(outerRadius,starPointBean.angle)
                starPointBean.pointy=getPointYFromAngle(outerRadius,starPointBean.angle)
            }
        }
        postInvalidate()
    }

    private fun getPointXFromAngle(radius:Float,angle:Double):Float{
        return (mWidth/2)+ radius * cos(Math.toRadians(angle)).toFloat()
    }

    private fun getPointYFromAngle(radius:Float,angle:Double):Float{
        return (mWidth/2)+ radius * sin(Math.toRadians(angle)).toFloat()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onStop()
    }

    fun onStart(){
        onStop()
        if (isAutoRotate){
            mHandler.sendEmptyMessageDelayed(REFRESH_MSG,30)
        }
    }

    fun onStop(){
        mHandler.removeMessages(REFRESH_MSG)
    }


    class StarPointBean(var angle:Double,
                        var pointX:Float=0f,
                        var pointy:Float=0f,
                        var id:String?="",
                        var ranking:Int=0,
                        var bitmap:Bitmap?=null,
                        var nickName:String?=null)
}
