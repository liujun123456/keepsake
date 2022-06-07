package com.shunlai.publish.sign

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.*
import com.shunlai.net.util.GsonUtil
import com.shunlai.publish.R
import com.shunlai.publish.entity.OrderBean
import com.shunlai.router.RouterManager
import com.shunlai.ui.StarLayout
import kotlinx.android.synthetic.main.activity_order_sign_layout.*
import kotlinx.android.synthetic.main.item_sign_order_layout.view.*
import kotlinx.android.synthetic.main.title_photo_sign_layout.*
import org.jetbrains.anko.textColor

/**
 * @author Liu
 * @Date   2021/5/27
 * @mobile 18711832023
 */
class OrderSignActivity:BaseActivity() {
    override fun getMainContentResId(): Int= R.layout.activity_order_sign_layout

    override fun getToolBarResID(): Int=R.layout.title_photo_sign_layout

    private var orderBean: OrderBean?=null

    override fun afterView() {
        orderBean=GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.ORDER_BEAN)?:"",OrderBean::class.java)
        buildGoods()
        initListener()
    }

    private fun buildGoods(){
        orderBean?.let {
            val view: View = View.inflate(mContext,R.layout.item_sign_order_layout,null)
            val params=
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.topMargin= ScreenUtils.dip2px(mContext,30f)
            view.layoutParams=params
            ImageUtil.showRoundImgWithStringAndRadius(view.iv_product_img,mContext,it.skuImage?:"",3f)
            view.tv_sign_title.text=it.skuName
            if (TextUtils.isEmpty(it.price)||it.price=="0"){
                view.ll_sign_price.visibility=View.GONE
            }else{
                view.tv_sign_price.text=it.price
            }
            view.star_view.setRatingChangeListener(object : StarLayout.OnRatingChangeListener{
                override fun onRatingChange(rating: Int) {
                    it.evaluate=rating
                    updateContentView()
                    updateSignStarNotice(it,view)
                }
            })
            view.star_view.setRating(it.evaluate)
            ll_product_list.addView(view)
        }
    }

    private fun initListener(){
        ll_back.setOnClickListener { finish() }
        tv_complete.setOnClickListener {
            if (orderBean?.evaluate==0){
                toast("请完成商品打星!")
                return@setOnClickListener
            }
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.ORDER_BEAN]=orderBean
            RouterManager.startActivityWithParams(BundleUrl.PUBLISH_ORDER_ACTIVITY,this,params)
        }
    }

    private fun updateContentView(){
        if (orderBean?.evaluate==0){
            tv_complete.textColor=Color.parseColor("#999999")
            tv_complete.setBackgroundResource(R.drawable.gray_radius_24_bg)

        }else{
            tv_complete.textColor= Color.parseColor("#ffffff")
            tv_complete.setBackgroundResource(R.drawable.black_radius_24_bg)
        }
    }

    private fun updateSignStarNotice(bean:OrderBean,view:View){
        when (bean.evaluate) {
            5 -> {
                view.tv_star_notice.visibility=View.VISIBLE
                view.tv_star_notice.setText(R.string.star_notice_recommend)
            }
            0 -> {
                view.tv_star_notice.visibility=View.VISIBLE
                view.tv_star_notice.setText(R.string.star_notice)
            }
            1 -> {
                view.tv_star_notice.visibility=View.VISIBLE
                view.tv_star_notice.setText(R.string.star_notice_one)
            }
            2 -> {
                view.tv_star_notice.visibility=View.VISIBLE
                view.tv_star_notice.setText(R.string.star_notice_two)
            }
            3 -> {
                view.tv_star_notice.visibility=View.VISIBLE
                view.tv_star_notice.setText(R.string.star_notice_three)
            }
            4 -> {
                view.tv_star_notice.visibility=View.VISIBLE
                view.tv_star_notice.setText(R.string.star_notice_four)
            }
            else -> {
                view.tv_star_notice.visibility=View.INVISIBLE
            }
        }
    }
}
