package com.shunlai.publish.wish

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.publish.Constant
import com.shunlai.publish.R
import com.shunlai.common.bean.GoodsBean
import kotlinx.android.synthetic.main.activity_add_wish.*
import kotlinx.android.synthetic.main.title_photo_sign_layout.*
import org.jetbrains.anko.textColor

class AddWishActivity : BaseActivity(),AddWishView{
    override fun getMainContentResId(): Int=R.layout.activity_add_wish

    override fun getToolBarResID(): Int=R.layout.title_photo_sign_layout

    override fun setTitleColor(): Int=R.color.white

    var chooseUri:Uri?=null

    private val mPresenter by lazy {
        AddWithPresenter(mContext,this)
    }

    override fun afterView() {
        initTitle()
        initListener()
        checkParams()
    }

    private fun initTitle(){
        ll_back.setOnClickListener { finish() }
    }

    private fun initListener(){
        ll_add_image.setOnClickListener {
            ImageUtil.openImage(this,Constant.OPEN_SYSTEM_PHOTOS)
        }
        ll_delete.setOnClickListener {
            ll_add_image.visibility= View.VISIBLE
            rl_show_img.visibility=View.GONE
            chooseUri=null
            checkParams()
        }
        tv_publish.setOnClickListener {
            mPresenter.publishWish(et_brand.text.toString(),
                et_goods_name.text.toString(),et_price.text.toString(),chooseUri)
        }
        et_brand.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                checkParams()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    private fun checkParams(){
        if (TextUtils.isEmpty(et_brand.text.toString())||chooseUri==null){
            tv_publish.textColor= Color.parseColor("#999999")
            tv_publish.setBackgroundResource(R.drawable.gray_radius_24_bg)
        }else{
            tv_publish.textColor= ContextCompat.getColor(mContext,R.color.white)
            tv_publish.setBackgroundResource(R.drawable.black_radius_24_bg)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImg = data?.data
        if (selectedImg!=null){
            chooseUri=selectedImg
            ll_add_image.visibility= View.GONE
            rl_show_img.visibility=View.VISIBLE
            ImageUtil.showRoundImgWithUriAndRadius(iv_show_img,mContext,selectedImg,16f)
        }
        checkParams()
    }

    override fun showLoading(str: String) {
        showBaseLoading()
    }

    override fun dismissLoading() {
        hideBaseLoading()
    }

    override fun addWishSuccess(bean: GoodsBean) {
        val intent=Intent()
        intent.putExtra(RunIntentKey.CHOOSE_GOODS,bean)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

}
