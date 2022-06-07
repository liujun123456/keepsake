package com.shunlai.mine.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.DollListBean
import com.shunlai.mine.entity.bean.SceneListBean
import kotlinx.android.synthetic.main.dialog_token_sum_layout.*

/**
 * @author Liu
 * @Date   2021/7/15
 * @mobile 18711832023
 */
class TokenSumDialog(var mContext: Context, var themeResId:Int,var mListener:OnTokenSumListener): Dialog(mContext,themeResId)  {
    var mScene:SceneListBean?=null
    var mDoll:DollListBean?=null
    var allToken:Int=0
    var isCheckScene=true
    var isCheckDoll=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_token_sum_layout)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        tv_cancel.setOnClickListener {
            dismiss()
        }
        tv_confirm.setOnClickListener {
            mListener.onTokenConfirm(isCheckScene,isCheckDoll)
        }
    }

    fun setData(scene: SceneListBean?,doll: DollListBean?,totalToken:Int){
        mScene=scene
        mDoll=doll
        allToken=totalToken
        ll_content_sum.removeAllViews()
        scene?.let {
            isCheckScene=true
            val view= View.inflate(mContext,R.layout.item_token_sum_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.dip2px(mContext,128f))
            view.findViewById<TextView>(R.id.tv_price).text=it.tokenPrice
            view.findViewById<TextView>(R.id.tv_name).text=it.name
            view.findViewById<CheckBox>(R.id.cb_box).setOnCheckedChangeListener { _, isChecked ->
                isCheckScene=isChecked
                sumResult()
            }
            ImageUtil.showRoundImgWithStringAndRadius(findViewById(R.id.iv_show_img),mContext,it.iconUrl,12f)
            ll_content_sum.addView(view)
        }
        doll?.let {
            isCheckDoll=true
            val view= View.inflate(mContext,R.layout.item_token_sum_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.dip2px(mContext,128f))
            view.findViewById<TextView>(R.id.tv_price).text=it.tokenPrice
            view.findViewById<TextView>(R.id.tv_name).text=it.name
            view.findViewById<CheckBox>(R.id.cb_box).setOnCheckedChangeListener { _, isChecked ->
                isCheckDoll=isChecked
                sumResult()
            }
            ImageUtil.showRoundImgWithStringAndRadius(view.findViewById(R.id.iv_show_img),mContext,it.iconUrl,12f)
            ll_content_sum.addView(view)
        }
        all_token.text=totalToken.toString()
        sumResult()
    }

    private fun sumResult(){
        var sum=0
        if (isCheckScene){
            sum=sum.plus((mScene?.tokenPrice?:"0").toInt())
        }
        if (isCheckDoll){
            sum=sum.plus((mDoll?.tokenPrice?:"0").toInt())
        }
        total_sum.text=sum.toString()
        if (allToken<sum){
            tv_confirm.text="攒Token币"
        }else{
            tv_confirm.text="确认兑换"
        }
    }

    interface OnTokenSumListener{
        fun onTokenConfirm(isCheckScene:Boolean,isCheckDoll:Boolean)
    }
}
