package com.shunlai.ui

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.ugc_action_window.view.*
import org.jetbrains.anko.textColor

/**
 * @author Liu
 * @Date   2021/5/18
 * @mobile 18711832023
 * type 0投诉 1删除 2拉黑
 */
class UgcActionWindow(var mContext: Context,var mListener:ActionListener): PopupWindow() {
    private var type=0
    init {
        val view= View.inflate(mContext, R.layout.ugc_action_window,null)
        contentView=view
        width= ViewGroup.LayoutParams.MATCH_PARENT
        height=  ViewGroup.LayoutParams.MATCH_PARENT
        isClippingEnabled=false
        isOutsideTouchable = true
        isFocusable=true
        animationStyle= R.style.PopupBottomInAnimation
        setOnDismissListener {
            contentView.tv_sure_delete.visibility=View.GONE
        }
        initView(view)
        update()
    }

    private fun initView(view: View){
        view.tv_action.setOnClickListener {
            if (type==0){
                mListener.onComplaintAction()
            }else if (type==1){
                if (view.tv_sure_delete.visibility==View.VISIBLE){
                    mListener.onDeleteAction()
                }else{
                    view.tv_sure_delete.visibility=View.VISIBLE
                    return@setOnClickListener
                }
            }else{
                mListener.onBlockAction()
            }
            dismiss()
        }
        view.tv_cancel.setOnClickListener {
            dismiss()
        }
        view.setOnClickListener {
            dismiss()
        }
    }

    fun showWindow(type:Int){
        this.type=type
        if (type==0){
            contentView.tv_action.text="投诉"
            contentView.tv_action.textColor=Color.parseColor("#666666")
        }else if (type==1){
            contentView.tv_action.text="删除"
            contentView.tv_action.textColor=Color.parseColor("#F24B49")
        }else{
            contentView.tv_action.text="拉黑"
            contentView.tv_action.textColor=Color.parseColor("#666666")
        }
        showAtLocation((mContext as FragmentActivity).window.decorView, Gravity.NO_GRAVITY,0,0)
    }

    interface ActionListener{
        fun onDeleteAction()
        fun onComplaintAction()
        fun onBlockAction(){

        }
    }
}
