package com.shunlai.ugc.goodsDetail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.shulai.third.ThirdManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.toast
import com.shunlai.im.ImManager
import com.shunlai.im.entity.ChatGoodsBean
import com.shunlai.share.WeChatUtil
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.ugc.R
import com.shunlai.ugc.entity.*
import com.shunlai.ugc.entity.resp.UgcGoodsDetailResp
import com.shunlai.ugc.goodsDetail.adapter.UgcGoodsDetailAdapter
import com.shunlai.ugc.goodsDetail.window.ShareWindow
import kotlinx.android.synthetic.main.activity_ugc_goods_detail_layout.*
import org.jetbrains.anko.textColor


/**
 * @author Liu
 * @Date   2021/4/25
 * @mobile 18711832023
 */
class UgcGoodsDetailActivity : BaseActivity(), UgcGoodsDetailView, ShareWindow.ShareWindowListener {
    override fun getMainContentResId(): Int = R.layout.activity_ugc_goods_detail_layout

    override fun getToolBarResID(): Int = 0

    private val mPresenter by lazy {
        UgcGoodsDetailPresenter(this, this)
    }

    private val shareWindow by lazy {
        ShareWindow(mContext, this)
    }

   private val mUgcId by lazy {
        intent.getStringExtra(RunIntentKey.UGC_ID)
    }

    private val productId by lazy {
        intent.getStringExtra(RunIntentKey.PRODUCT_ID)
    }

    private val mAdapter by lazy {
        UgcGoodsDetailAdapter(
            mContext, mutableListOf(
                BaseUgcGoodsBean(0), BaseUgcGoodsBean(1)
                , BaseUgcGoodsBean(2)
            )
        )
    }

    private var goodsDetail: UgcGoodsDetailResp? = null

    override fun afterView() {
        initRv()
        mPresenter.queryUgcGoodsDetail(productId?:"", mUgcId?:"")
        mPresenter.queryProductComment(productId?:"")
    }


    private fun initRv() {
        rv_goods.layoutManager = LinearLayoutManager(mContext)
        rv_goods.adapter = mAdapter
    }

    fun goBack(view: View) {
        finish()
    }

    fun goHome(view: View) {
        RouterManager.startActivityWithParams(BundleUrl.HOME_ACTIVITY, this)
    }

    fun goChat(view: View) {
        if (goodsDetail?.shareMemberId==PreferenceUtil.getString("userId")){
            toast("您不能跟自己聊天哦!")
            return
        }
        ImManager.markerMessageAsRead(goodsDetail?.shareMemberId.toString())
        val params = mutableMapOf<String, Any?>()
        params[RunIntentKey.TO_USER_ID] = goodsDetail?.shareMemberId
        params[RunIntentKey.TO_USER_NAME] = goodsDetail?.nickName
        params[RunIntentKey.GOODS]=GsonUtil.toJson(ChatGoodsBean().apply {
            goodsId=goodsDetail?.goodsId
            name=goodsDetail?.title
            price=goodsDetail?.finalPrice
            img=if (goodsDetail?.images.isNullOrEmpty()) "" else goodsDetail?.images!![0]
            ugcId=mUgcId
        })
        RouterManager.startActivityWithParams(BundleUrl.CHAT_ACTIVITY, this, params)

    }

    fun goBuy(view: View) {
        ThirdManager.openTbAppById(goodsDetail?.goodsId?:"",mContext)
    }

    fun goShare(view: View) {
        mPresenter.buildPost(
            mUgcId,
            goodsDetail?.shopMemberId.toString(),
            goodsDetail?.goodsId ?: "",
            goodsDetail?.type.toString()
        )
    }


    override fun buildProductInfo(bean: UgcGoodsDetailResp) {
        val bannerBean = GoodsBannerBean()
        bannerBean.type = 0
        bannerBean.banner = bean.images ?: mutableListOf()
        bannerBean.finalPrice = bean.finalPrice
        bannerBean.title = bean.title
        mAdapter.mData[0] = bannerBean
        mAdapter.notifyItemChanged(0)
        goodsDetail = bean
        rl_bottom.visibility = View.VISIBLE
        if (goodsDetail?.type==1||goodsDetail?.type==2){
            go_buy.visibility=View.VISIBLE
        }else if (goodsDetail?.type==4){
            go_buy.visibility=View.VISIBLE
            go_buy.text="心愿单商品"
            go_buy.textColor=Color.parseColor("#4D000000")
            go_buy.setBackgroundResource(R.drawable.gray_24_radius_bg)
            go_buy.isEnabled=false
        }else{
            go_buy.visibility=View.GONE
        }
    }

    override fun imageDetail(data: MutableList<String>) {
        if (data.isNullOrEmpty()){
            mAdapter.mData.removeAt(2)
            mAdapter.notifyDataSetChanged()
        }else{
            data.forEach {
                val imagesBean = GoodsImageBean()
                imagesBean.type = 3
                imagesBean.imgPath = it
                mAdapter.mData.add(imagesBean)
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun buildProductComment(data: MutableList<UgcCommentBean>, totalCount: Long) {
        val commentBean = GoodsCommentBean()
        commentBean.type = 1
        commentBean.data = data
        commentBean.totalCount=totalCount.toString()
        commentBean.productId=productId
        mAdapter.mData[1] = commentBean
        mAdapter.notifyItemChanged(1)
    }

    override fun doShareResult(result: Boolean, imgUrl: String?, error: String?) {
        if (result) {
            shareWindow.showAtLocation(container_layout, Gravity.NO_GRAVITY, 0, 0)
            shareWindow.setImageUrl(imgUrl)
        } else {
            error?.let {
                toast(it)
            }
        }
    }

    override fun showLoading(value: String) {
        showBaseLoading()
    }

    override fun dismissLoading() {
        hideBaseLoading()
    }

    override fun onWeChatShare(bmp: Bitmap) {
        shareWindow.dismiss()
        WeChatUtil.getInstance().shareWeChatWithImg(bmp)
    }

    override fun onCircleShare(bmp: Bitmap) {
        shareWindow.dismiss()
        WeChatUtil.getInstance().shareCircleWithImg(bmp)
    }

    override fun onShowWindow() {
        v_transition.visibility = View.VISIBLE
    }

    override fun onDismissWindow() {
        v_transition.visibility = View.GONE
    }

}
