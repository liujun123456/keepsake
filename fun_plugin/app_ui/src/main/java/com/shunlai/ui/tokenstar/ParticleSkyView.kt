package com.shunlai.ui.tokenstar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import com.shunlai.common.utils.ScreenUtils
import kotlin.random.Random

/**
 * @author Liu
 * @Date   2021/8/17
 * @mobile 18711832023
 */
@SuppressLint("HandlerLeak")
class ParticleSkyView: View {
    constructor(mContext: Context):super(mContext)
    constructor(mContext: Context, attrs: AttributeSet):super(mContext,attrs)
    constructor(mContext: Context, attrs: AttributeSet, defStyleAttr: Int):super(mContext,attrs,defStyleAttr)

    private val mList= mutableListOf<ParticleBean>()
    var mWidth:Int=0
    var mHeight:Int=0
    private val paintPointCircle= Paint()

    private val PARTICLE_MSG=1008612
    private val PARTICLE_REFRESH_MSG=1008613

    private val particleHandler=object :Handler(){
        override fun handleMessage(msg: Message) {
           if (msg.what==PARTICLE_MSG){
               sendParticle()
               sendEmptyMessageDelayed(PARTICLE_MSG,500)
           }else if (msg.what==PARTICLE_REFRESH_MSG){
               countParticle()
               sendEmptyMessageDelayed(PARTICLE_REFRESH_MSG,30)
           }
        }
    }

    init {
        paintPointCircle.color= Color.parseColor("#2EE6D6")
        paintPointCircle.isAntiAlias=true
        paintPointCircle.style= Paint.Style.FILL
        paintPointCircle.strokeWidth=1f
        paintPointCircle.isDither = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth=MeasureSpec.getSize(widthMeasureSpec)
        mHeight=MeasureSpec.getSize(heightMeasureSpec)
        initData()
        setMeasuredDimension(mWidth,mWidth)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            mList.forEach {bean->
                paintPointCircle.alpha=bean.birthAlpha
                it.drawCircle(bean.realPointX,bean.realPointY,ScreenUtils.dip2px(context,bean.radius).toFloat(),paintPointCircle)
            }
        }
    }

    private fun initData(){
        particleHandler.removeMessages(PARTICLE_MSG)
        particleHandler.removeMessages(PARTICLE_REFRESH_MSG)
        particleHandler.sendEmptyMessageDelayed(PARTICLE_MSG,500)
        particleHandler.sendEmptyMessageDelayed(PARTICLE_REFRESH_MSG,30)
    }

    /**
     * 计算粒子在空间中的点
     */
    private fun countParticle(){
        val iterator=mList.iterator()
        while (iterator.hasNext()){
            val bean=iterator.next()
            bean.birthAlpha=bean.birthAlpha-1
            if (System.currentTimeMillis()-bean.birthTime>10000||bean.birthAlpha<=0){
                iterator.remove()
            }else{
                if (bean.startPointX>mWidth/2){
                    bean.realPointX-=bean.speed
                }else{
                    bean.realPointX+=bean.speed
                }

                if (bean.startPointY>mHeight/2){
                    bean.realPointY-=bean.speed
                }else{
                    bean.realPointY+=bean.speed
                }
            }
        }
        postInvalidate()
    }

    /**
     * 发送粒子
     */
    private fun sendParticle(){
        val speed= Random.nextInt(1,4)
        val birthAlpha=Random.nextInt(150,256)
        val radius=Random.nextInt(1,3).toFloat()
        val birthAngle=Random.nextInt(0,360)
        var startPointX=0f
        var startPointY=0f
        if (birthAngle in 0..90){
            startPointX=mWidth.toFloat()
            startPointY=mHeight.times(birthAngle).div(90).toFloat()
        }else if (birthAngle in 91..180){
            startPointX=mWidth.times(birthAngle-90).div(90).toFloat()
            startPointY=mHeight.toFloat()
        }else if (birthAngle in 181..270){
            startPointX=0f
            startPointY=mHeight.times(birthAngle-180).div(90).toFloat()
        }else{
            startPointX=mWidth.times(birthAngle-270).div(90).toFloat()
            startPointY=0f
        }
        mList.add(
            ParticleBean(
                startPointX,
                startPointY,
                startPointX,
                startPointY,
                speed,
                radius,
                birthAlpha,
                System.currentTimeMillis()
            )
        )
    }

    fun onStart(){
        initData()
    }

    fun onStop(){
        particleHandler.removeMessages(PARTICLE_MSG)
        particleHandler.removeMessages(PARTICLE_REFRESH_MSG)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onStop()
    }

    /**
     * 粒子实体类
     */
    class ParticleBean(var startPointX:Float,
                       var startPointY:Float,
                       var realPointX:Float,
                       var realPointY:Float,
                       var speed:Int,
                       var radius:Float,
                       var birthAlpha:Int,
                       var birthTime:Long)
}
