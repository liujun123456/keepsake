package com.shunlai.ui

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.shunlai.common.bean.GoodsBean
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.toast
import kotlinx.android.synthetic.main.window_quick_pub_layout.view.*

/**
 * @author Liu
 * @Date   2021/9/23
 * @mobile 18711832023
 */
class QuickPubWindow(private var mContext: Context,private var mListener:OnQuickPubListener):PopupWindow() {

    private var goods:GoodsBean?=null

    init {
        val view= View.inflate(mContext, R.layout.window_quick_pub_layout,null)
        contentView=view
        width= ViewGroup.LayoutParams.MATCH_PARENT
        height=  ViewGroup.LayoutParams.MATCH_PARENT
        isClippingEnabled=false
        isOutsideTouchable = true
        isFocusable=true
        animationStyle= R.style.PopupBottomInAnimation
        initView(view)
        update()
    }

    private fun initView(view:View){
        view.tv_cancel.setOnClickListener {
            dismiss()
        }
        view.et_ugc_content.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    checkSign()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        view.tv_publish.setOnClickListener {
            if (TextUtils.isEmpty(view.et_ugc_content.text.toString())){
                toast("请输入笔记内容")
                return@setOnClickListener
            }
            if (goods?.evaType==0){
                toast("请先完成表态!")
                return@setOnClickListener
            }
            dismiss()
            mListener.doPublish(goods!!,view.et_ugc_content.text.toString())
        }
        view.iv_go_publish.setOnClickListener {
            dismiss()
            mListener.goPublish(goods!!,view.et_ugc_content.text.toString())
        }

    }

    fun setShowGoods(ugcGoods: GoodsBean){
        setDefaultView()
        goods=ugcGoods
        ImageUtil.showRoundImgWithStringAndRadius(
            contentView.iv_product_img,
            mContext,
            goods?.thumb ?: "",
            10f)
        if (TextUtils.isEmpty(goods?.brandName)) {
            contentView.tv_sign_title.text = goods?.name
        } else {
            if (TextUtils.isEmpty(goods?.name)) {
                contentView.tv_sign_title.text = goods?.brandName
            } else {
                contentView.tv_sign_title.text = "${goods?.brandName} ${goods?.name}"
            }
        }

        if (TextUtils.isEmpty(goods?.price) || goods?.price == "0") {
            contentView.ll_sign_price.visibility = View.GONE
        } else {
            contentView.tv_sign_price.text = goods?.price
        }

        contentView.rl_recommend.setOnClickListener {
            setDefaultSign()
            goods?.evaType=1
            contentView.rl_recommend.setBackgroundResource(R.mipmap.recommend_actived)
            contentView.iv_recommend_choose.visibility=View.VISIBLE
            contentView.rl_un_recommend.alpha=0.4f
            checkSign()
        }
        contentView.rl_un_recommend.setOnClickListener {
            setDefaultSign()
            goods?.evaType=2
            contentView.rl_un_recommend.setBackgroundResource(R.mipmap.no_recommend_actived)
            contentView.iv_un_recommend_choose.visibility=View.VISIBLE
            contentView.rl_recommend.alpha=0.4f
            checkSign()
        }

    }

    private fun setDefaultView(){
        contentView.et_ugc_content.setText("")
        contentView.iv_go_publish.visibility=View.GONE
        setDefaultSign()
    }

    private fun setDefaultSign(){
        contentView.rl_recommend.setBackgroundResource(R.mipmap.recommend_normal)
        contentView.iv_recommend_choose.visibility=View.GONE
        contentView.rl_recommend.alpha=1.0f

        contentView.rl_un_recommend.setBackgroundResource(R.mipmap.no_recommend_normal)
        contentView.iv_un_recommend_choose.visibility=View.GONE
        contentView.rl_un_recommend.alpha=1.0f
    }

    private fun checkSign(){
        if (goods?.evaType==0){
            contentView.iv_go_publish.visibility=View.GONE
        }else{
            contentView.iv_go_publish.visibility=View.VISIBLE
        }
        if (TextUtils.isEmpty(contentView.et_ugc_content.text.toString())||goods?.evaType==0){
            contentView.tv_publish.alpha=0.4f
        }else{
            contentView.tv_publish.alpha=1.0f
        }
    }


    interface OnQuickPubListener{
        fun doPublish(goods:GoodsBean,content:String)
        fun goPublish(goods:GoodsBean,content:String)
    }
}
