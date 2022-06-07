package com.shunlai.publish.sign

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.getui.gs.sdk.GsManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.*
import com.shunlai.publish.Constant
import com.shunlai.publish.R
import com.shunlai.common.bean.GoodsBean
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_product_sign.*
import kotlinx.android.synthetic.main.item_sign_product_layout.view.*
import org.jetbrains.anko.textColor
import org.json.JSONObject
import java.util.ArrayList

class ProductSignActivity : BaseActivity(), ProductSignView {
    override fun getMainContentResId(): Int = R.layout.activity_product_sign

    override fun getToolBarResID(): Int = 0

    private val currentSignGoods: MutableList<GoodsBean> = mutableListOf()

    private val mPresenter by lazy {
        ProductSignPresenter(mContext, this)
    }

    override fun afterView() {
        currentSignGoods.addAll(intent.getParcelableArrayListExtra(RunIntentKey.SIGN_GOODS_LIST) ?: mutableListOf())
        initListener()
        buildSignView()
    }

    private fun initListener() {
        tv_clip.setOnClickListener {
            mPresenter.clipSearch()
        }
        iv_close.setOnClickListener { finish() }
    }

    fun doComplete(view: View) {
        if (currentSignGoods.size == 0) {
            toast(R.string.sign_notice)
            return
        }

        if (!checkStar()) {
            toast(R.string.sign_star_notice)
            return
        }
        val intent=Intent()
        intent.putParcelableArrayListExtra(RunIntentKey.PUBLISH_GOODS,currentSignGoods as ArrayList<out Parcelable>)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    fun goWish(view: View) {
        RouterManager.startActivityForResultWithParams(
            BundleUrl.ADD_WISH_ACTIVITY, this,
            mutableMapOf(), Constant.OPEN_WISH_REQUEST_CODE
        )
    }

    fun goSearch(view: View) {
        RouterManager.startActivityForResultWithParams(
            BundleUrl.PHOTO_SEARCH_ACTIVITY,
            this,
            mutableMapOf(),
            Constant.OPEN_SEARCH_REQUEST_CODE
        )
    }

    private fun buildSignView() {
        updateContentView()
        ll_product_list.removeAllViews()
        currentSignGoods.forEach { goods ->
            val view: View = View.inflate(mContext, R.layout.item_sign_product_layout, null)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = ScreenUtils.dip2px(mContext, 16f)
            view.layoutParams = params
            ImageUtil.showRoundImgWithStringAndRadius(
                view.iv_product_img,
                mContext,
                goods.thumb ?: "",
                10f
            )
            if (TextUtils.isEmpty(goods.brandName)) {
                view.tv_sign_title.text = goods.name
            } else {
                if (TextUtils.isEmpty(goods.name)) {
                    view.tv_sign_title.text = goods.brandName
                } else {
                    view.tv_sign_title.text = "${goods.brandName} ${goods.name}"
                }
            }
            if (TextUtils.isEmpty(goods.price) || goods.price == "0") {
                view.ll_sign_price.visibility = View.GONE
            } else {
                view.tv_sign_price.text = goods.price
            }

            if (goods.evaType==1){
                view.rl_recommend.setBackgroundResource(R.mipmap.recommend_actived)
                view.iv_recommend_choose.visibility=View.VISIBLE
                view.rl_un_recommend.alpha=0.4f
            }else if (goods.evaType==2){
                view.rl_un_recommend.setBackgroundResource(R.mipmap.no_recommend_actived)
                view.iv_un_recommend_choose.visibility=View.VISIBLE
                view.rl_recommend.alpha=0.4f
            }

            view.rl_delete_card.setOnClickListener {
                currentSignGoods.remove(goods)
                buildSignView()
            }
            view.rl_recommend.setOnClickListener {
                goods.evaType=1
                buildSignView()
            }
            view.rl_un_recommend.setOnClickListener {
                goods.evaType=2
                buildSignView()
            }
            ll_product_list.addView(view)
        }
    }

    private fun checkStar(): Boolean {
        currentSignGoods.forEach {
            if (it.evaType == 0) {
                return false
            }
        }
        return true
    }


    private fun updateContentView() {
        ll_wish.visibility=View.VISIBLE
        if (currentSignGoods.size == 0 || currentSignGoods.size == 3) {
            if (currentSignGoods.size == 3) {
                ll_clip.visibility = View.GONE
                ll_search.visibility = View.GONE
                if (checkStar()) {
                    tv_complete.textColor = ContextCompat.getColor(mContext, R.color.white)
                    tv_complete.setBackgroundResource(R.drawable.black_radius_24_bg)
                } else {
                    tv_complete.textColor = Color.parseColor("#999999")
                    tv_complete.setBackgroundResource(R.drawable.gray_radius_24_bg)
                }
                ll_wish.visibility=View.GONE

            } else {
                tv_clip.text = mContext.resources.getString(R.string.clip_text)
                ll_clip.visibility = View.VISIBLE
                ll_search.visibility = View.VISIBLE
                tv_complete.textColor = Color.parseColor("#999999")
                tv_complete.setBackgroundResource(R.drawable.gray_radius_24_bg)
            }
        } else {
            tv_clip.text =
                mContext.resources.getString(R.string.clip_text_has_value, currentSignGoods.size)
            ll_clip.visibility = View.VISIBLE
            ll_search.visibility = View.VISIBLE
            if (checkStar()) {
                tv_complete.textColor = ContextCompat.getColor(mContext, R.color.white)
                tv_complete.setBackgroundResource(R.drawable.black_radius_24_bg)
            } else {
                tv_complete.textColor = Color.parseColor("#999999")
                tv_complete.setBackgroundResource(R.drawable.gray_radius_24_bg)
            }
        }
    }


    override fun showLoading(value: String) {
        showBaseLoading()
    }

    override fun dismissLoading() {
        hideBaseLoading()
    }


    override fun addSignGoods(bean: GoodsBean) {
        currentSignGoods.forEach {
            if (it.productId == null || it.productId == bean.productId) {
                toast("请不要添加两件同样的商品")
                return
            }
        }
        currentSignGoods.add(bean)
        buildSignView()
    }

    override fun goSearchPage(dates: MutableList<GoodsBean>) {
        val params = mutableMapOf<String, Any?>()
        params[RunIntentKey.SEARCH_RESULT] = dates
        params[RunIntentKey.SEARCH_KEY] = mPresenter.searchKey
        RouterManager.startActivityForResultWithParams(
            BundleUrl.PHOTO_SEARCH_ACTIVITY,
            this,
            params,
            Constant.OPEN_SEARCH_REQUEST_CODE
        )
    }

    /**
     * 神策---粘贴并解析
     */
    override fun trackSignGoods(bean: GoodsBean) {
        val params = JSONObject()
        params.put("page_name", screenUrl)
        params.put("product_name", bean.name)
        params.put("store_name", bean.shopName)
        params.put("product_source", bean.type)
        params.put("product_price", bean.price)
        GsManager.getInstance().onEvent("PasteParse", params)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.OPEN_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bean = data?.getParcelableExtra<GoodsBean>(RunIntentKey.CHOOSE_GOODS)
            bean?.let {
                addSignGoods(bean)
            }
        } else if (requestCode == Constant.OPEN_WISH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bean = data?.getParcelableExtra<GoodsBean>(RunIntentKey.CHOOSE_GOODS)
            bean?.let {
                addSignGoods(bean)
            }
        }
    }
}
