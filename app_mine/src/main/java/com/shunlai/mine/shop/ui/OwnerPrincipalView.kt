package com.shunlai.mine.shop.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import java.io.File
import java.util.*

/**
 * @author Liu
 * @Date   2021/7/14
 * @mobile 18711832023
 */
class OwnerPrincipalView:LinearLayout {
    constructor(context:Context):super(context)
    constructor(context:Context,attr: AttributeSet):super(context,attr)
    constructor(context:Context,attr: AttributeSet,defStyleAttr:Int):super(context,attr,defStyleAttr)

    private val showView:ImageView = ImageView(context)
    private val defaultAction= arrayListOf<String>()
    private val touchAction= arrayListOf<String>()
    private var defaultAnim: ValueAnimator?=null
    private var touchAnim: ValueAnimator?=null

    init {
        showView.layoutParams=LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        showView.scaleType=ImageView.ScaleType.FIT_XY
        addView(showView)
    }

    fun setResourcePath(path:String){
        cancelAnim()
        defaultAction.clear()
        touchAction.clear()
        val defaultDir=File("$path/default")
        if (defaultDir.exists()&&!defaultDir.isFile){
            val defaultFiles=defaultDir.listFiles()
            defaultFiles?.forEach {
                it.path.let {path->
                    defaultAction.add(path)
                }
            }
        }
        val touchDir=File("$path/click")
        if (touchDir.exists()&&!touchDir.isFile){
            val touchFiles=touchDir.listFiles()
            touchFiles?.forEach {
                it.path.let {path->
                    touchAction.add(path)
                }
            }
        }
        defaultAction.sort()
        touchAction.sort()
        showDefaultAnim()
    }

    private fun showDefaultAnim(){
        cancelAnim()
        if (defaultAction.isNullOrEmpty())return
        defaultAnim=ValueAnimator.ofInt(0,defaultAction.size-1)
        defaultAnim?.duration=defaultAction.size*33L
        defaultAnim?.interpolator= LinearInterpolator()
        defaultAnim?.addUpdateListener {
            val value=it.animatedValue as Int
            if (currentValue!=value){
                showImageView(defaultAction[value])
            }
            currentValue=value
        }
        defaultAnim?.repeatCount=ValueAnimator.INFINITE
        defaultAnim?.start()
    }

    private var currentValue=-1
    fun showTouchAnim(){
        cancelAnim()
        if (touchAction.isNullOrEmpty())return
        touchAnim=ValueAnimator.ofInt(0,touchAction.size-1)
        touchAnim?.duration=touchAction.size*33L
        touchAnim?.interpolator= LinearInterpolator()
        touchAnim?.addUpdateListener {
            if (it.animatedValue is Int){
                val value=it.animatedValue as Int
                if (currentValue!=value){
                    showImageView(touchAction[value])
                }
                currentValue=value
            }
        }
        touchAnim?.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                showDefaultAnim()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        touchAnim?.start()
    }

    private fun cancelAnim(){
        currentValue=-1
        touchAnim?.cancel()
        defaultAnim?.cancel()
    }

    private fun showImageView(path: String){
        val bitmap=BitmapFactory.decodeFile(path)
        showView.setImageBitmap(bitmap)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        touchAnim?.cancel()
        defaultAnim?.cancel()
    }

    fun onResume(){
        if (defaultAction.isNotEmpty()){
            showDefaultAnim()
        }
    }

    fun onPause(){
        cancelAnim()
    }

}
