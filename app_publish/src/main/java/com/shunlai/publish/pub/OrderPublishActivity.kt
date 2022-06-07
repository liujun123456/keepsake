package com.shunlai.publish.pub

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.PathItem
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.net.util.GsonUtil
import com.shunlai.publish.Constant
import com.shunlai.publish.R
import com.shunlai.common.bean.GoodsBean
import com.shunlai.publish.entity.HuaTiBean
import com.shunlai.publish.entity.OrderBean
import com.shunlai.publish.picker.ui.MediaGridInset
import com.shunlai.publish.pub.adapter.ImageAdapter
import com.shunlai.publish.pub.adapter.SimpleProductAdapter
import com.shunlai.router.RouterManager
import com.shunlai.ui.ListenerScrollView
import com.shunlai.ui.moveRv.MoveCallBack
import kotlinx.android.synthetic.main.activity_order_publish.*
import kotlinx.android.synthetic.main.title_photo_sign_layout.*
import org.jetbrains.anko.textColor

/**
 * @author Liu
 * @Date   2021/5/28
 * @mobile 18711832023
 */
class OrderPublishActivity :BaseActivity(),ImageAdapter.OnHandleViewListener {
    override fun getMainContentResId(): Int=R.layout.activity_order_publish

    override fun getToolBarResID(): Int= R.layout.title_photo_sign_layout

    override fun setTitleColor(): Int = R.color.gray_f7

    private val imageAdapter by lazy {
        ImageAdapter(mContext, mutableListOf(), rv_photo, this)
    }

    private val goods:MutableList<GoodsBean> = mutableListOf()

    private val simpleProductAdapter by lazy {
        SimpleProductAdapter(mContext, goods)
    }
    private val orderBean by lazy {
        intent.getParcelableExtra<OrderBean>(RunIntentKey.ORDER_BEAN)
    }

    override fun afterView() {
        initListener()
        initRv()
    }

    private fun initListener(){
        ll_back.setOnClickListener {
            finish()
        }
        tv_add_hua_ti.setOnClickListener {
            RouterManager.startActivityForResultWithParams(
                BundleUrl.HUA_TI_SEARCH_ACTIVITY,
                this,
                mutableMapOf(),
                Constant.OPEN_HT_REQUEST_CODE
            )
        }
        sl_view.setTopScrollListener(object : ListenerScrollView.TopScrollListener {
            override fun onScroll(top: Int) {
                if (top > ScreenUtils.dip2px(mContext, 40f)) {
                    tv_title_content.setText(R.string.publish_title)
                } else {
                    tv_title_content.text = ""
                }
            }
        })

        ugcTitleListener()
        ugcContentListener()


    }

    private fun ugcTitleListener(){
        et_ugc_title.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                tv_title_label.text = "${s?.length ?: 0}/15"
                checkParams()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    private fun ugcContentListener(){
        et_ugc_content.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                tv_content_label.text = "${s?.length ?: 0}/1000"
                checkParams()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        et_ugc_content.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun initRv(){
        rv_photo.layoutManager = GridLayoutManager(mContext, 3)
        rv_photo.addItemDecoration(
            MediaGridInset(
                3, resources.getDimensionPixelSize(
                    R.dimen.media_grid_spacing
                ), false, true
            )
        )
        rv_photo.adapter = imageAdapter
        val helper = ItemTouchHelper(MoveCallBack(imageAdapter))
        helper.attachToRecyclerView(rv_photo)


        orderBean?.let {
            val goodsBean= GoodsBean()
            goodsBean.name=it.skuName
            goodsBean.price=it.price
            goodsBean.thumb=it.skuImage
            goodsBean.productId=it.skuId
            goodsBean.evaluate=it.evaluate
            simpleProductAdapter.signGoods.add(goodsBean)
        }
        rv_product.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
        rv_product.adapter = simpleProductAdapter
    }

    private fun checkParams() {
        if (TextUtils.isEmpty(et_ugc_title.text.toString()) ||
            TextUtils.isEmpty(et_ugc_content.text.toString())||
            imageAdapter.mData.isNullOrEmpty()) {
            tv_publish.setBackgroundResource(R.drawable.gray_radius_2_bg)
            tv_publish.textColor = Color.parseColor("#999999")
        } else {
            tv_publish.setBackgroundResource(R.drawable.black_radius_2_bg)
            tv_publish.textColor = ContextCompat.getColor(mContext, R.color.white)
        }
    }

    override fun removeItem(item: PathItem, position: Int) {
        imageAdapter.mData.removeAt(position)
        imageAdapter.notifyDataSetChanged()
        checkParams()
    }

    override fun onTakePhoto() {
        val params = mutableMapOf<String, Any?>()
        params[RunIntentKey.IS_ONLY_PICKER] = true
        params[RunIntentKey.CHOOSE_IMAGE_ITEM] = imageAdapter.mData
        RouterManager.startActivityForResultWithParams(
            BundleUrl.PHOTO_PICKER_ACTIVITY,
            this,
            params,
            Constant.OPEN_PICKER_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.OPEN_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val dates = data?.getParcelableArrayListExtra<PathItem>(RunIntentKey.CHOOSE_IMAGE_ITEM)
            dates?.let {
                imageAdapter.mData.clear()
                imageAdapter.mData.addAll(it)
                imageAdapter.notifyDataSetChanged()
            }
            checkParams()
        } else if (requestCode == Constant.OPEN_HT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val htValue = data?.getStringExtra("ht_key_word")
            try {
                val htBean= GsonUtil.fromJson(htValue?:"", HuaTiBean::class.java)
                ll_hua_ti.visibility = View.VISIBLE
                tv_add_hua_ti.visibility = View.GONE
                if (htBean.activity==true){
                    tv_ht_value.setCompoundDrawablesWithIntrinsicBounds(R.drawable.activity_loading_bg,0,0,0)
                    (tv_ht_value.compoundDrawables[0] as AnimationDrawable).start()
                    tv_ht_value.text = " ${htBean.tag}"
                }else{
                    tv_ht_value.text = "#${htBean.tag}"
                }
            }catch (e:Exception){

            }
        }
    }
}
