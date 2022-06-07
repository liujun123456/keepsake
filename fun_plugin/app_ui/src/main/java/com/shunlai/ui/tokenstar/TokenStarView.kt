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
class TokenStarView:View, GestureDetector.OnGestureListener {
    private val paintBgCircle=Paint()
    private val paintPointCircle=Paint()
    private val contactPaint=Paint()
    private val mPoints= mutableListOf<StarPointBean>()
    private var mWidth:Int=0
    private var mHeight:Int=0
    private var outerRadius:Float=0f
    private var middleRadius:Float=0f
    private var innerRadius:Float=0f
    private var textHeight=0f
    private val REFRESH_MSG=1008611
    private var gestureDetector:GestureDetector?=null
    private var isAutoRotate=true
    private var mListener:TokenStarItemClickListener?=null

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
        gestureDetector=GestureDetector(context,this)
        paintBgCircle.color=Color.parseColor("#1AFFFFFF")
        paintBgCircle.isAntiAlias=true
        paintBgCircle.style= Paint.Style.STROKE
        paintBgCircle.strokeWidth=ScreenUtils.dip2px(context,1f).toFloat()
        paintBgCircle.pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
        paintBgCircle.isDither = true

        paintPointCircle.color=Color.parseColor("#FFFFFF")
        paintPointCircle.isAntiAlias=true
        paintPointCircle.style= Paint.Style.FILL
        paintPointCircle.strokeWidth=1f
        paintPointCircle.textSize=ScreenUtils.dip2px(context,11f).toFloat()
        paintPointCircle.textAlign= Paint.Align.CENTER
        paintPointCircle.isDither = true

        contactPaint.color=Color.parseColor("#4b2EE6D6")
        contactPaint.isAntiAlias=true
        contactPaint.style= Paint.Style.STROKE
        contactPaint.strokeWidth=ScreenUtils.dip2px(context,1f).toFloat()
        contactPaint.pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
        contactPaint.isDither = true

        //计算文字的相应偏移量
        val fontMetrics: Paint.FontMetrics = paintPointCircle.fontMetrics
        textHeight=fontMetrics.bottom-fontMetrics.top
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth=MeasureSpec.getSize(widthMeasureSpec)
        mHeight=mWidth
        initPointData()
        setMeasuredDimension(mWidth,mWidth)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector?.onTouchEvent(event)?:super.onTouchEvent(event)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            //draw 外环圆
            it.drawCircle(width.toFloat()/2,height.toFloat()/2,outerRadius,paintBgCircle)

            //draw 中环圆
            it.drawCircle(width.toFloat()/2,height.toFloat()/2,middleRadius,paintBgCircle)

            //draw 内环圆
            it.drawCircle(width.toFloat()/2,height.toFloat()/2,innerRadius,paintBgCircle)

            mPoints.forEachIndexed { index, bean ->
                if (index in 0 until 4){  //绘制外环点
                    if (bean.bitmap==null){
                        paintPointCircle.alpha=40
                        it.drawCircle(bean.pointX,bean.pointy,ScreenUtils.dip2px(context,4f).toFloat(),paintPointCircle)
                    }else{
                        paintPointCircle.alpha=90
                        if (bean.id==PreferenceUtil.getString(Constant.USER_ID)){
                            paintPointCircle.alpha=255
                            it.drawLine(bean.pointX,bean.pointy,width.toFloat()/2,height.toFloat()/2,contactPaint)
                        }
                        it.drawCircle(bean.pointX,bean.pointy,ScreenUtils.dip2px(context,9f).toFloat(),paintPointCircle)
                        val bitmapRadius=ScreenUtils.dip2px(context,8f)
                        it.drawBitmap(bean.bitmap!!,null,
                            RectF(bean.pointX.minus(bitmapRadius),
                                bean.pointy.minus(bitmapRadius),
                                bean.pointX.plus(bitmapRadius),
                                bean.pointy.plus(bitmapRadius)),paintPointCircle)
                        it.drawText(bean.nickName?:"",bean.pointX,bean.pointy.plus(bitmapRadius).plus(textHeight),paintPointCircle)
                    }
                }else if (index in 4 until 7){ //绘制中环点
                    if (bean.bitmap==null){
                        paintPointCircle.alpha=40
                        it.drawCircle(bean.pointX,bean.pointy,ScreenUtils.dip2px(context,4f).toFloat(),paintPointCircle)
                    }else{
                        paintPointCircle.alpha=115
                        if (bean.id==PreferenceUtil.getString(Constant.USER_ID)){
                            paintPointCircle.alpha=255
                            it.drawLine(bean.pointX,bean.pointy,width.toFloat()/2,height.toFloat()/2,contactPaint)
                        }
                        it.drawCircle(bean.pointX,bean.pointy,ScreenUtils.dip2px(context,13f).toFloat(),paintPointCircle)
                        val bitmapRadius=ScreenUtils.dip2px(context,12f)
                        it.drawBitmap(bean.bitmap!!,null,
                            RectF(bean.pointX.minus(bitmapRadius),
                                bean.pointy.minus(bitmapRadius),
                                bean.pointX.plus(bitmapRadius),
                                bean.pointy.plus(bitmapRadius)),paintPointCircle)
                        it.drawText(bean.nickName?:"",bean.pointX,bean.pointy.plus(bitmapRadius).plus(textHeight),paintPointCircle)
                    }
                }else{  //绘制内环点
                    if (bean.bitmap==null){
                        paintPointCircle.alpha=40
                        it.drawCircle(bean.pointX,bean.pointy,ScreenUtils.dip2px(context,4f).toFloat(),paintPointCircle)
                    }else{
                        paintPointCircle.alpha=165
                        if (bean.id==PreferenceUtil.getString(Constant.USER_ID)){
                            paintPointCircle.alpha=255
                            it.drawLine(bean.pointX,bean.pointy,width.toFloat()/2,height.toFloat()/2,contactPaint)
                        }
                        it.drawCircle(bean.pointX,bean.pointy,ScreenUtils.dip2px(context,17f).toFloat(),paintPointCircle)
                        val bitmapRadius=ScreenUtils.dip2px(context,16f)
                        it.drawBitmap(bean.bitmap!!,null,
                            RectF(bean.pointX.minus(bitmapRadius),
                                bean.pointy.minus(bitmapRadius),
                                bean.pointX.plus(bitmapRadius),
                                bean.pointy.plus(bitmapRadius)),paintPointCircle)
                        it.drawText(bean.nickName?:"",bean.pointX,bean.pointy.plus(bitmapRadius).plus(textHeight),paintPointCircle)
                    }
                }
            }
        }
    }

    fun setOnItemClickListener(listener: TokenStarItemClickListener){
        mListener=listener
    }

    fun initPointData(){
        mPoints.clear()
        outerRadius=(mWidth/2).times(0.829).toFloat()
        middleRadius=(mWidth/2).times(0.6).toFloat()
        innerRadius=(mWidth/2).times(0.368).toFloat()
        mPoints.add(StarPointBean(45.0))
        mPoints.add(StarPointBean(135.0))
        mPoints.add(StarPointBean(225.0))
        mPoints.add(StarPointBean(315.0))

        mPoints.add(StarPointBean(60.0))
        mPoints.add(StarPointBean(180.0))
        mPoints.add(StarPointBean(300.0))

        mPoints.add(StarPointBean(0.0))
        mPoints.add(StarPointBean(120.0))
        mPoints.add(StarPointBean(240.0))

        countPoint()
        onStart()
    }

    private fun countAngle(){
        mPoints.forEachIndexed { index, starPointBean ->
            if (index==4||index==5||index==6){
                var realValue= starPointBean.angle-0.15
                if (realValue<=0){
                    realValue=360.0
                }
                starPointBean.angle=realValue
            }else{
                var realValue= starPointBean.angle+0.15
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
            if (index==0||index==1||index==2||index==3){
                starPointBean.pointX=getPointXFromAngle(outerRadius,starPointBean.angle)
                starPointBean.pointy=getPointYFromAngle(outerRadius,starPointBean.angle)
            }else if (index==4||index==5||index==6){
                starPointBean.pointX=getPointXFromAngle(middleRadius,starPointBean.angle)
                starPointBean.pointy=getPointYFromAngle(middleRadius,starPointBean.angle)
            }else{
                starPointBean.pointX=getPointXFromAngle(innerRadius,starPointBean.angle)
                starPointBean.pointy=getPointYFromAngle(innerRadius,starPointBean.angle)
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

    fun setData(mData:StarUserBean){
        val pointIndex=mPoints.size.minus(mData.ranking)
        if (pointIndex<0||pointIndex>9)return
        mPoints[pointIndex].bitmap=mData.bitmap
        mPoints[pointIndex].nickName=mData.name
        mPoints[pointIndex].id=mData.id
        mPoints[pointIndex].ranking=mData.ranking
        postInvalidate()
    }



    class StarPointBean(var angle:Double,
                        var pointX:Float=0f,
                        var pointy:Float=0f,
                        var id:String?="",
                        var ranking:Int=0,
                        var bitmap:Bitmap?=null,
                        var nickName:String?=null)

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean{
        e?.let {
            mPoints.forEach {bean->
                val clickRadius=ScreenUtils.dip2px(context,20f)
                val rect=RectF(bean.pointX.minus(clickRadius),
                    bean.pointy.minus(clickRadius),
                    bean.pointX.plus(clickRadius),
                    bean.pointy.plus(clickRadius))
                if (rect.contains(it.x,it.y)){
                    mListener?.onStarClick(bean)
                }
            }
        }
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean=true

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean=false

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean=false

    override fun onLongPress(e: MotionEvent?) {

    }

    interface TokenStarItemClickListener{
        fun onStarClick(bean:StarPointBean)

    }
}
