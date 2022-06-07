package com.shunlai.ugc.goodsDetail.adapter

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.router.RouterManager
import com.shunlai.ugc.R
import com.shunlai.ugc.entity.*
import com.shunlai.ui.MediaGridInset
import kotlinx.android.synthetic.main.item_goods_banner_info_layout.view.*
import kotlinx.android.synthetic.main.item_goods_comment_info_layout.view.*

/**
 * @author Liu
 * @Date   2021/6/3
 * @mobile 18711832023
 */
class UgcGoodsDetailAdapter(var mContext: Context, var mData: MutableList<BaseUgcGoodsBean>) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 10086) {
            val view = View.inflate(mContext, R.layout.item_goods_banner_info_layout, null)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return BannerViewHolder(view)
        } else if (viewType == 10087) {
            val view = View.inflate(mContext, R.layout.item_goods_comment_info_layout, null)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return CommentViewHolder(view)
        } else if (viewType == 10088) {
            val view = View.inflate(mContext, R.layout.item_goods_img_title_layout, null)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return ImageTitleViewHolder(view)
        } else {
            val img = ImageView(mContext)
            img.scaleType=ImageView.ScaleType.FIT_XY
            img.layoutParams = ViewGroup.LayoutParams(
                ScreenUtils.getScreenWidth(mContext),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return ImageViewHolder(img)
        }
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BannerViewHolder) {
            holder.setData(mData[position])
        } else if (holder is CommentViewHolder) {
            holder.setData(mData[position])
        } else if (holder is ImageTitleViewHolder) {
            holder.setData(mData[position])
        } else if (holder is ImageViewHolder) {
            holder.setData(mData[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (mData[position].type == 0) {
            return 10086
        } else if (mData[position].type == 1) {
            return 10087
        } else if (mData[position].type == 2) {
            return 10088
        } else {
            return 10089
        }
    }

    class BannerViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {
        fun setData(bean: BaseUgcGoodsBean) {
            if (bean is GoodsBannerBean) {
                if (TextUtils.isEmpty(bean.finalPrice)||bean.finalPrice=="0"){
                    mView.tv_price.visibility=View.GONE
                    mView.tv_price_label.visibility=View.GONE
                }else{
                    mView.tv_price.visibility=View.VISIBLE
                    mView.tv_price_label.visibility=View.VISIBLE
                    mView.tv_price.text = bean.finalPrice
                }
                mView.tv_product_desc.text = bean.title

                val bannerList = mutableListOf<BannerInfo>()
                bean.banner.forEach {
                    bannerList.add(BannerInfo(it))
                }
                mView.x_banner.setBannerData(bannerList)
                mView.x_banner.loadImage { _, _, view, position ->
                    ImageUtil.showCropImgWithString(view as ImageView, mView.context,bannerList[position].imageUrl,R.mipmap.default_icon)

                }
                mView.x_banner.setOnItemClickListener{ _, _, _, position ->
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.IMAGE_URL]=bannerList[position].imageUrl
                    RouterManager.startActivityWithParams(BundleUrl.PHOTO_SIMPLE_PREVIEW_PATH_ACTIVITY,mView.context as FragmentActivity,params)
                }

            }
        }
    }

    class CommentViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {
        fun setData(bean: BaseUgcGoodsBean) {
            if (bean is GoodsCommentBean){
                mView.tv_comment_count.text="共${bean.totalCount}条"
                val layoutManager=LinearLayoutManager(mView.context, RecyclerView.HORIZONTAL,false)
                layoutManager.initialPrefetchItemCount=5
                mView.rv_comment.layoutManager=layoutManager
                mView.rv_comment.addItemDecoration(MediaGridInset(1, ScreenUtils.dip2px(mView.context,5f), false,false))
                mView.rv_comment.adapter=ProductCommentAdapter(mView.context,bean.data)
                mView.tv_more.setOnClickListener {
                    val params = mutableMapOf<String,Any?>()
                    params[RunIntentKey.PRODUCT_ID]=bean.productId?:""
                    RouterManager.startActivityWithParams(BundleUrl.UGC_COMMENT_ACTIVITY,mView.context as FragmentActivity,params)
                }
            }
        }
    }

    class ImageTitleViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {
        fun setData(bean: BaseUgcGoodsBean) {

        }
    }

    class ImageViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {
        fun setData(bean: BaseUgcGoodsBean) {
            if (bean is GoodsImageBean){
                if (bean.imgPath?.endsWith("gif")==true){
                    ImageUtil.showGoodsGif(mView as ImageView,mView.context, Uri.parse(bean.imgPath))
                }else{
                    ImageUtil.showGoodsImg(mView as ImageView,mView.context, Uri.parse(bean.imgPath))
                }
            }
        }
    }
}
