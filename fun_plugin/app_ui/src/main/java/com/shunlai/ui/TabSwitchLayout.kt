package com.shunlai.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.wrapContent


/**
 * @author  jasonLiu on 2017/10/23.
 */
class TabSwitchLayout: LinearLayout{

    var checkImg:Int=0
    var unCheckImg:Int=0
    var checkColor:Int=0
    var unCheckColor:Int=0
    private var isChecked:Boolean=false
    lateinit var text:String
    var isInit:Boolean=true
    var isUpdateIcon=true
    var isAnim=false

    var mContext: Context
    lateinit var textView:TextView
    lateinit var checkImgView:ImageView
    lateinit var unCheckImgView:ImageView
    lateinit var imgLayout:RelativeLayout
    lateinit var scaleAnimation:Animation
    lateinit var narrowAnimation:Animation


    constructor(context: Context):super(context,null){
        mContext=context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs,0){
        mContext=context
        initView()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        mContext=context
        initView()
    }

    private fun initView(){
        orientation= VERTICAL
        gravity= Gravity.CENTER

        scaleAnimation=AnimationUtils.loadAnimation(mContext, R.anim.scale_spread_anim)
        narrowAnimation=AnimationUtils.loadAnimation(mContext,R.anim.scale_narrow_anim)

        textView= TextView(mContext)
        textView.layoutParams= LayoutParams(wrapContent, wrapContent)
        textView.textSizeDimen= R.dimen.tab_switch_10sp

        checkImgView= ImageView(mContext)
        checkImgView.layoutParams=LayoutParams(wrapContent, wrapContent)

        unCheckImgView= ImageView(mContext)
        unCheckImgView.layoutParams=LayoutParams(wrapContent, wrapContent)

        imgLayout= RelativeLayout(mContext)
        imgLayout.layoutParams=LayoutParams(wrapContent, wrapContent)


        imgLayout.addView(checkImgView)
        imgLayout.addView(unCheckImgView)

        addView(imgLayout)
        addView(textView)
    }
    fun initIcon(checkImg:Int,unCheckImg:Int,text:String,checkColor:Int,unCheckColor: Int){
        this.checkImg=checkImg
        this.unCheckImg=unCheckImg
        this.text=text
        this.checkColor=checkColor
        this.unCheckColor=unCheckColor

        renderView()
        updateIcon()
    }
    fun hideText(hide:Boolean){
        if (hide)textView.visibility= View.GONE
        else textView.visibility= View.VISIBLE
    }


    fun setCheckState(isChecked:Boolean){
        this.isChecked=isChecked
        isInit=false
        updateIcon()

    }
    private fun renderView(){
        textView.text=text
        checkImgView.imageResource=checkImg
        unCheckImgView.imageResource=unCheckImg
    }
    private fun updateIcon(){
        if (!isUpdateIcon)return
        if (isChecked){
            textView.textColor= ContextCompat.getColor(mContext,checkColor)
            if (isInit){
                unCheckImgView.visibility= View.INVISIBLE
                checkImgView.visibility= View.VISIBLE
            }else{
                beginAnimation()
            }
        }else{
            textView.textColor=ContextCompat.getColor(mContext,unCheckColor)
            if (isInit){
                unCheckImgView.visibility= View.VISIBLE
                checkImgView.visibility= View.INVISIBLE
            }else{
                beginNarrowAnimation()
            }
        }
    }
    private fun beginAnimation(){
        if (isAnim){
            scaleAnimation.setAnimationListener( object : Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    unCheckImgView.visibility= View.INVISIBLE
                    checkImgView.visibility= View.VISIBLE
                }

                override fun onAnimationStart(animation: Animation?) {

                }

            })
            checkImgView.startAnimation(scaleAnimation)
        }else{
            unCheckImgView.visibility= View.INVISIBLE
            checkImgView.visibility= View.VISIBLE
        }

    }
    private fun beginNarrowAnimation(){
        unCheckImgView.visibility= View.VISIBLE
        if (isAnim){
            narrowAnimation.setAnimationListener( object : Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    checkImgView.visibility= View.INVISIBLE
                }
                override fun onAnimationStart(animation: Animation?) {

                }

            })
            checkImgView.startAnimation(narrowAnimation)
        }
        checkImgView.visibility= View.INVISIBLE

    }
}
