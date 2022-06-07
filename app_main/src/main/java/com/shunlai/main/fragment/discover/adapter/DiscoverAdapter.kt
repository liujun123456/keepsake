package com.shunlai.main.fragment.discover.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.*
import com.shunlai.main.ActionInterface
import com.shunlai.main.R
import com.shunlai.main.entities.UgcBean
import com.shunlai.main.entities.UgcGoodsBean
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import org.jetbrains.anko.textColor

/**
 * @author Liu
 * @Date   2021/5/6
 * @mobile 18711832023
 * type 1 首页推荐  2 首页关注 3话题
 */
class DiscoverAdapter(var mContext:Context,var mData:MutableList<UgcBean>,var mInterface: ActionInterface,var type:Int):SRecyclerAdapter(mContext) {


    val TYPE_ONE=10001
    val TYPE_TWO=10002
    val TYPE_THREE=10003
    val TYPE_VIDEO=10005
    val TYPE_TEXT=10006

    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==TYPE_TWO){
            val view=View.inflate(mContext,R.layout.item_discover_type_two_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            val realImageHeight=ScreenUtils.getScreenWidth(mContext).minus(ScreenUtils.dip2px(mContext,32f)).div(2)
            view.findViewById<LinearLayout>(R.id.img_video_layout).layoutParams=RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                realImageHeight)
            (view.findViewById<View>(R.id.shadow_view).layoutParams as RelativeLayout.LayoutParams).topMargin=realImageHeight.minus(ScreenUtils.dip2px(mContext,32f))
            return DiscoverTypeTwoViewHolder(view)

        }else if (viewType==TYPE_THREE){
            val view=View.inflate(mContext,R.layout.item_discover_type_three_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            val realImageHeight=ScreenUtils.getScreenWidth(mContext).minus(ScreenUtils.dip2px(mContext,32f)).div(3).times(2)
            view.findViewById<LinearLayout>(R.id.img_video_layout).layoutParams=RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                realImageHeight)
            (view.findViewById<View>(R.id.shadow_view).layoutParams as RelativeLayout.LayoutParams).topMargin=realImageHeight.minus(ScreenUtils.dip2px(mContext,32f))
            return DiscoverTypeThreeViewHolder(view)

        }else if (viewType==TYPE_VIDEO){
            val view=View.inflate(mContext,R.layout.item_discover_type_video_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            return DiscoverTypeVideoViewHolder(view)

        }else if (viewType==TYPE_ONE){
            val view=View.inflate(mContext,R.layout.item_discover_type_one_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            return DiscoverTypeOneViewHolder(view)
        }else{
            val view=View.inflate(mContext,R.layout.item_discover_type_text_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            return DiscoverTypeTextViewHolder(view)
        }
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is DiscoverTypeOneViewHolder){
            viewHolder.setData(mData[position],position)
        }else if (viewHolder is DiscoverTypeTwoViewHolder){
            viewHolder.setData(mData[position],position)
        }else if (viewHolder is DiscoverTypeThreeViewHolder){
            viewHolder.setData(mData[position],position)
        }else if (viewHolder is DiscoverTypeVideoViewHolder){
            viewHolder.setData(mData[position],position)
        }else if (viewHolder is DiscoverTypeTextViewHolder){
            viewHolder.setData(mData[position],position)
        }

        viewHolder.itemView.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.UGC_ID]= mData[position].ugcId
            params[RunIntentKey.UGC_TYPE]= mData[position].ugcType.toString()
            RouterManager.startActivityWithParams(BundleUrl.UGC_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
        }
    }

    override fun getCount(): Int=mData.size

    override fun getViewType(position: Int): Int {
        if (mData[position].ugcType=="2"){
            return TYPE_VIDEO
        }else{
            val imageSize=mData[position].imageList?.size?:0
            if (imageSize==1){
                return TYPE_ONE
            }else if (imageSize==2){
                return TYPE_TWO
            }else if (imageSize>=3){
                return TYPE_THREE
            }
        }
        return TYPE_TEXT
    }

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text="没有笔记数据！"
        return DefaultLoadMoreViewHolder(view)
    }

    inner class DiscoverTypeTextViewHolder(var mItemView:View) :DiscoverViewHolder(mItemView){
        override fun setData(bean: UgcBean, position: Int) {
            super.setData(bean, position)
            mItemView.findViewById<TextView>(R.id.tv_ugc_content).visibility=View.GONE
            mItemView.findViewById<TextView>(R.id.tv_card_ugc_content).text=bean.content
            val layout=mItemView.findViewById<RelativeLayout>(R.id.img_video_layout)
            layout?.post {
                val layoutParams=RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,ScreenUtils.dip2px(mContext,48f))
                layoutParams.topMargin=layout.measuredHeight.minus(ScreenUtils.dip2px(mContext,32f))
                mItemView.findViewById<View>(R.id.shadow_view).layoutParams=layoutParams

            }
        }
    }


    inner class DiscoverTypeOneViewHolder(var mItemView:View) :DiscoverViewHolder(mItemView){
        override fun setData(bean: UgcBean, position: Int) {
            super.setData(bean, position)
            if (!bean.pictureList.isNullOrEmpty()){
                ImageUtil.showCropImgWithString(mItemView.findViewById(R.id.one_img),mView.context,bean.pictureList!![0].imageUrl?:"")
                val width=ScreenUtils.getScreenWidth(mContext).minus(ScreenUtils.dip2px(mContext,32f))
                val imageHeight=bean.pictureList!![0].imageHeight
                val imageWidth=bean.pictureList!![0].imageWidth
                var realHeight=width.times(imageHeight).div(imageWidth)
                if (realHeight>ScreenUtils.dip2px(mContext,400f)){
                    realHeight=ScreenUtils.dip2px(mContext,400f)
                }else if (realHeight<ScreenUtils.dip2px(mContext,224f)){
                    realHeight=ScreenUtils.dip2px(mContext,224f)
                }
                mItemView.findViewById<LinearLayout>(R.id.img_video_layout).layoutParams=RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    realHeight)
                (mItemView.findViewById<View>(R.id.shadow_view).layoutParams as RelativeLayout.LayoutParams).topMargin=realHeight.minus(ScreenUtils.dip2px(mContext,32f))

            }
         }
    }

    inner class DiscoverTypeTwoViewHolder(var mItemView:View) :DiscoverViewHolder(mItemView){
        override fun setData(bean: UgcBean, position: Int) {
            super.setData(bean, position)
            if (!bean.imageList.isNullOrEmpty()){
                ImageUtil.showRoundImgWithStringAndRadius(mItemView.findViewById(R.id.one_img),mView.context,bean.imageList!![0],12f,
                    leftTop = true,
                    rightTop = false,
                    leftBottom = true,
                    rightBottom = false
                )
                ImageUtil.showRoundImgWithStringAndRadius(mItemView.findViewById(R.id.two_img),mView.context,bean.imageList!![1],12f,
                    leftTop = false,
                    rightTop = true,
                    leftBottom = false,
                    rightBottom = true
                )
            }
        }
    }

    inner class DiscoverTypeThreeViewHolder(var mItemView:View) :DiscoverViewHolder(mItemView){
        override fun setData(bean: UgcBean, position: Int) {
            super.setData(bean, position)
            if (!bean.imageList.isNullOrEmpty()){
                ImageUtil.showRoundImgWithStringAndRadius(mItemView.findViewById(R.id.one_img),mView.context,bean.imageList!![0],12f,
                    leftTop = true,
                    rightTop = false,
                    leftBottom = true,
                    rightBottom = false
                )
                ImageUtil.showRoundImgWithStringAndRadius(mItemView.findViewById(R.id.two_img),mView.context,bean.imageList!![1],12f,
                    leftTop = false,
                    rightTop = true,
                    leftBottom = false,
                    rightBottom = false
                )
                ImageUtil.showRoundImgWithStringAndRadius(mItemView.findViewById(R.id.three_img),mView.context,bean.imageList!![2],12f,
                    leftTop = false,
                    rightTop = false,
                    leftBottom = false,
                    rightBottom = true
                )
            }
            val imageSize=bean.imageList?.size?:0
            if (imageSize>3){
                mItemView.findViewById<TextView>(R.id.tv_count).visibility=View.VISIBLE
                mItemView.findViewById<TextView>(R.id.tv_count).text="${imageSize-3}"
            }else{
                mItemView.findViewById<TextView>(R.id.tv_count).visibility=View.INVISIBLE
            }
        }
    }

    inner class DiscoverTypeVideoViewHolder(var mItemView:View) :DiscoverViewHolder(mItemView){
        override fun setData(bean: UgcBean, position: Int) {
            super.setData(bean, position)
            if (!bean.pictureList.isNullOrEmpty()){
                ImageUtil.showCropImgWithString(mItemView.findViewById(R.id.one_img),mView.context,bean.pictureList!![0].imageUrl?:"")

                val width=ScreenUtils.getScreenWidth(mContext).minus(ScreenUtils.dip2px(mContext,32f))
                val imageHeight=bean.pictureList!![0].imageHeight
                val imageWidth=bean.pictureList!![0].imageWidth
                var realHeight=width.times(imageHeight).div(imageWidth)
                if (realHeight>ScreenUtils.dip2px(mContext,400f)){
                    realHeight=ScreenUtils.dip2px(mContext,400f)
                }else if (realHeight<ScreenUtils.dip2px(mContext,224f)){
                    realHeight=ScreenUtils.dip2px(mContext,224f)
                }
                mItemView.findViewById<RelativeLayout>(R.id.img_video_layout).layoutParams=RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    realHeight)
                (mItemView.findViewById<View>(R.id.shadow_view).layoutParams as RelativeLayout.LayoutParams).topMargin=realHeight.minus(ScreenUtils.dip2px(mContext,32f))



                mItemView.findViewById<ImageView>(R.id.iv_video_play).setOnClickListener {
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.CAMERA_IMG_PATH]=bean.imageList!![0]
                    params[RunIntentKey.CAMERA_VIDEO_PATH]= Uri.parse(bean.video)
                    RouterManager.startActivityWithParams(BundleUrl.VIDEO_VIEW_ACTIVITY,mView.context as FragmentActivity,params)
                }
            }

        }
    }


    open inner class DiscoverViewHolder(var mView:View) :RecyclerView.ViewHolder(mView){
        @SuppressLint("SetTextI18n")
        open fun setData(bean:UgcBean, position: Int){

            mView.findViewById<ImageView>(R.id.iv_avatar).setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_ID]= bean.memberId
                RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
            }
            ImageUtil.showCircleImgWithString(mView.findViewById(R.id.iv_avatar),mView.context,bean.avatar?:"",R.mipmap.user_default_icon)
            mView.findViewById<TextView>(R.id.tv_user_name).text=bean.nickName



            if (type==2){
                mView.findViewById<TextView>(R.id.tv_attention).visibility=View.GONE
            }else{
                if (bean.memberId==PreferenceUtil.getString(Constant.USER_ID)){
                    mView.findViewById<TextView>(R.id.tv_attention).visibility=View.GONE
                }else{
                    mView.findViewById<TextView>(R.id.tv_attention).visibility=View.VISIBLE
                }

                if (bean.isFollow=="1"){
                    mView.findViewById<TextView>(R.id.tv_attention).text="已关注"
                    mView.findViewById<TextView>(R.id.tv_attention).setBackgroundResource(R.drawable.have_attention_bg)

                }else{
                    mView.findViewById<TextView>(R.id.tv_attention).text="+ 关注"
                    mView.findViewById<TextView>(R.id.tv_attention).setBackgroundResource(R.drawable.action_attention_bg)
                 }
                mView.findViewById<TextView>(R.id.tv_attention).setOnClickListener {
                    mInterface.doAttention(position,bean.memberId?:"")
                }
            }

            if (type==2||type==3){
                mView.findViewById<TextView>(R.id.tv_time).visibility=View.VISIBLE
                mView.findViewById<TextView>(R.id.tv_time).text=bean.displayTime
            }else{
                mView.findViewById<TextView>(R.id.tv_time).visibility=View.GONE
            }

            if (!TextUtils.isEmpty(bean.distanceText)){
                mView.findViewById<TextView>(R.id.tv_user_desc).visibility=View.VISIBLE
                mView.findViewById<TextView>(R.id.tv_user_desc).setCompoundDrawablesWithIntrinsicBounds(R.mipmap.token_star_icon,0,0,0)
                mView.findViewById<TextView>(R.id.tv_user_desc).text="${bean.score}Hz · ${bean.distanceText}"
            }else{
                mView.findViewById<TextView>(R.id.tv_user_desc).visibility=View.GONE
            }

            mView.findViewById<ImageView>(R.id.iv_home_more).setOnClickListener {
                mInterface.doMore(position,bean.ugcId?:"",bean.memberId?:"")
            }


            mView.findViewById<TextView>(R.id.tv_ugc_content).visibility=View.VISIBLE
            mView.findViewById<TextView>(R.id.tv_ugc_content).text=bean.content

            if (!TextUtils.isEmpty(bean.topic?.tag)&&type!=3){
                mView.findViewById<TextView>(R.id.tv_huati).visibility=View.VISIBLE
                if (bean.topic?.activity==true){
                    mView.findViewById<TextView>(R.id.tv_huati).setCompoundDrawablesWithIntrinsicBounds(R.drawable.activity_loading_bg,0,0,0)
                    (mView.findViewById<TextView>(R.id.tv_huati).compoundDrawables[0] as AnimationDrawable).start()
                    mView.findViewById<TextView>(R.id.tv_huati).text=" ${bean.topic?.tag}"
                }else{
                    mView.findViewById<TextView>(R.id.tv_huati).setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                    mView.findViewById<TextView>(R.id.tv_huati).text="# ${bean.topic?.tag}"
                }
                mView.findViewById<TextView>(R.id.tv_huati).setOnClickListener {
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.HT_DETAIL]= GsonUtil.toJson(bean.topic!!)
                    RouterManager.startActivityWithParams(BundleUrl.HUA_TI_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
                }
            }else{
                mView.findViewById<TextView>(R.id.tv_huati).visibility=View.GONE
            }

            if (bean.likes==0){
                mView.findViewById<TextView>(R.id.tv_home_like).text=""
            }else{
                if (bean.likes?:0>999){
                    mView.findViewById<TextView>(R.id.tv_home_like).text="999+"
                }else{
                    mView.findViewById<TextView>(R.id.tv_home_like).text=bean.likes.toString()
                }

            }
            if (bean.whetherLikes){
                mView.findViewById<TextView>(R.id.tv_home_like).setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_choose_icon_style_one,0,0,0)
            }else{
                mView.findViewById<TextView>(R.id.tv_home_like).setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_icon_style_one,0,0,0)
            }
            mView.findViewById<TextView>(R.id.tv_home_like).setOnClickListener {
                mInterface.doLike(position,bean.ugcId?:"",bean.whetherLikes)
            }

            if (bean.comments==0){
                mView.findViewById<TextView>(R.id.tv_home_eva).text=""
            }else{
                mView.findViewById<TextView>(R.id.tv_home_eva).text=bean.comments.toString()
            }
            mView.findViewById<TextView>(R.id.tv_home_eva).setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.UGC_ID]= bean.ugcId
                params[RunIntentKey.UGC_TYPE]= bean.ugcType.toString()
                params[RunIntentKey.IS_SHOW_COMMENT]= true
                RouterManager.startActivityWithParams(BundleUrl.UGC_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
            }


            if (bean.favorites==0){
                mView.findViewById<TextView>(R.id.tv_home_star).text=""
            }else{
                if (bean.favorites?:0>999){
                    mView.findViewById<TextView>(R.id.tv_home_star).text="999+"
                }else{
                    mView.findViewById<TextView>(R.id.tv_home_star).text=bean.favorites.toString()
                }
            }
            if (bean.whetherFavorites){
                mView.findViewById<TextView>(R.id.tv_home_star).setCompoundDrawablesWithIntrinsicBounds(R.mipmap.star_icon_choose_style_one,0,0,0)
            }else{
                mView.findViewById<TextView>(R.id.tv_home_star).setCompoundDrawablesWithIntrinsicBounds(R.mipmap.star_icon_style_one,0,0,0)
            }
            mView.findViewById<TextView>(R.id.tv_home_star).setOnClickListener {
                mInterface.doCollect(position,bean.ugcId?:"",bean.whetherFavorites)
            }

            if (bean.topping==1){  //如果置顶  精选不显示
                mView.findViewById<TextView>(R.id.tv_top_flag).visibility=View.VISIBLE
                mView.findViewById<TextView>(R.id.tv_selected_flag).visibility=View.GONE
            }else{  //如果不置顶 判断是否精选
                if (bean.selectedFlag==1){
                    mView.findViewById<TextView>(R.id.tv_selected_flag).visibility=View.VISIBLE
                }else{
                    mView.findViewById<TextView>(R.id.tv_selected_flag).visibility=View.GONE
                }
                mView.findViewById<TextView>(R.id.tv_top_flag).visibility=View.GONE
            }

            buildGoods(bean.ugcId?:"",bean.ugcGoods?: mutableListOf())

        }

        private fun buildGoods(ugcId:String,list:MutableList<UgcGoodsBean>){
            mView.findViewById<HorizontalScrollView>(R.id.sl_goods).visibility=View.VISIBLE
            mView.findViewById<LinearLayout>(R.id.ll_goods).removeAllViews()
            if (!list.isNullOrEmpty()){
                if (list.size==1){
                    val goodsBean=list[0]
                    val view=View.inflate(mView.context,R.layout.item_ugc_goods_layout,null)
                    view.layoutParams= ViewGroup.LayoutParams(ScreenUtils.getScreenWidth(mContext).minus(ScreenUtils.dip2px(mContext,32f)),
                        ScreenUtils.dip2px(mView.context,80f))

                    ImageUtil.showRoundImgWithStringAndRadius(view.findViewById(R.id.iv_product_img),mView.context,goodsBean.productImg?:"",8f)
                    view.findViewById<TextView>(R.id.tv_product_desc).text=goodsBean.productName
                    view.setOnClickListener {
                        if(goodsBean.type=="3"){
                            toast("京东商品，暂未开通哦")
                            return@setOnClickListener
                        }
                        val params= mutableMapOf<String,Any?>()
                        params[RunIntentKey.UGC_ID]=ugcId
                        params[RunIntentKey.PRODUCT_ID]= goodsBean.productId
                        RouterManager.startActivityWithParams(BundleUrl.UGC_GOODS_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
                    }
                    view.findViewById<TextView>(R.id.tv_go_eva).setOnClickListener {
                        mInterface.doEva(goodsBean)
                    }
                    if (goodsBean.isRecommend==null){
                        if (goodsBean.evaluate?:0>=3){
                            view.findViewById<LinearLayout>(R.id.ll_eva_bg).setBackgroundResource(R.drawable.ugc_item_tj_bg)
                            view.findViewById<ImageView>(R.id.iv_eva_image).setImageResource(R.mipmap.recommend_icon)
                            view.findViewById<TextView>(R.id.tv_eva_name).text = "推荐"
                            view.findViewById<TextView>(R.id.tv_eva_name).textColor=Color.parseColor("#FF4D3300")
                        }else{
                            view.findViewById<LinearLayout>(R.id.ll_eva_bg).setBackgroundResource(R.drawable.ugc_item_bl_bg)
                            view.findViewById<ImageView>(R.id.iv_eva_image).setImageResource(R.mipmap.un_recommend_icon)
                            view.findViewById<TextView>(R.id.tv_eva_name).text = "避雷"
                            view.findViewById<TextView>(R.id.tv_eva_name).textColor=Color.parseColor("#FF49264D")
                        }
                    }else{
                        if (goodsBean.isRecommend==1){
                            view.findViewById<LinearLayout>(R.id.ll_eva_bg).setBackgroundResource(R.drawable.ugc_item_tj_bg)
                            view.findViewById<ImageView>(R.id.iv_eva_image).setImageResource(R.mipmap.recommend_icon)
                            view.findViewById<TextView>(R.id.tv_eva_name).text = "推荐"
                            view.findViewById<TextView>(R.id.tv_eva_name).textColor=Color.parseColor("#FF4D3300")
                        }else{
                            view.findViewById<LinearLayout>(R.id.ll_eva_bg).setBackgroundResource(R.drawable.ugc_item_bl_bg)
                            view.findViewById<ImageView>(R.id.iv_eva_image).setImageResource(R.mipmap.un_recommend_icon)
                            view.findViewById<TextView>(R.id.tv_eva_name).text = "避雷"
                            view.findViewById<TextView>(R.id.tv_eva_name).textColor= Color.parseColor("#FF49264D")
                        }
                    }

                    mView.findViewById<LinearLayout>(R.id.ll_goods).addView(view)
                }else{
                    list.forEachIndexed { index, goodsBean ->
                        val view=View.inflate(mView.context,R.layout.item_ugc_goods_layout,null)
                        val params= LinearLayout.LayoutParams(ScreenUtils.dip2px(mView.context,311f),
                            ScreenUtils.dip2px(mView.context,80f))
                        if (index!=list.size-1){
                            params.rightMargin=ScreenUtils.dip2px(mView.context,16f)
                        }
                        view.layoutParams=params

                        ImageUtil.showRoundImgWithStringAndRadius(view.findViewById(R.id.iv_product_img),mView.context,goodsBean.productImg?:"",8f)
                        view.findViewById<TextView>(R.id.tv_product_desc).text=goodsBean.productName
                        view.setOnClickListener {
                            if(goodsBean.type=="3"){
                                toast("京东商品，暂未开通哦")
                                return@setOnClickListener
                            }
                            val params= mutableMapOf<String,Any?>()
                            params[RunIntentKey.UGC_ID]=ugcId
                            params[RunIntentKey.PRODUCT_ID]= goodsBean.productId
                            RouterManager.startActivityWithParams(BundleUrl.UGC_GOODS_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
                        }

                        view.findViewById<TextView>(R.id.tv_go_eva).setOnClickListener {
                            mInterface.doEva(goodsBean)
                        }

                        if (goodsBean.isRecommend==null){
                            if (goodsBean.evaluate?:0>=3){
                                view.findViewById<LinearLayout>(R.id.ll_eva_bg).setBackgroundResource(R.drawable.ugc_item_tj_bg)
                                view.findViewById<ImageView>(R.id.iv_eva_image).setImageResource(R.mipmap.recommend_icon)
                                view.findViewById<TextView>(R.id.tv_eva_name).text = "推荐"
                                view.findViewById<TextView>(R.id.tv_eva_name).textColor= Color.parseColor("#FF4D3300")
                            }else{
                                view.findViewById<LinearLayout>(R.id.ll_eva_bg).setBackgroundResource(R.drawable.ugc_item_bl_bg)
                                view.findViewById<ImageView>(R.id.iv_eva_image).setImageResource(R.mipmap.un_recommend_icon)
                                view.findViewById<TextView>(R.id.tv_eva_name).text = "避雷"
                                view.findViewById<TextView>(R.id.tv_eva_name).textColor= Color.parseColor("#FF49264D")
                            }
                        }else{
                            if (goodsBean.isRecommend==1){
                                view.findViewById<LinearLayout>(R.id.ll_eva_bg).setBackgroundResource(R.drawable.ugc_item_tj_bg)
                                view.findViewById<ImageView>(R.id.iv_eva_image).setImageResource(R.mipmap.recommend_icon)
                                view.findViewById<TextView>(R.id.tv_eva_name).text = "推荐"
                                view.findViewById<TextView>(R.id.tv_eva_name).textColor= Color.parseColor("#FF4D3300")
                            }else{
                                view.findViewById<LinearLayout>(R.id.ll_eva_bg).setBackgroundResource(R.drawable.ugc_item_bl_bg)
                                view.findViewById<ImageView>(R.id.iv_eva_image).setImageResource(R.mipmap.un_recommend_icon)
                                view.findViewById<TextView>(R.id.tv_eva_name).text = "避雷"
                                view.findViewById<TextView>(R.id.tv_eva_name).textColor= Color.parseColor("#FF49264D")
                            }
                        }
                        mView.findViewById<LinearLayout>(R.id.ll_goods).addView(view)
                    }
                }
            }else{
                mView.findViewById<HorizontalScrollView>(R.id.sl_goods).visibility=View.GONE
            }
        }
    }
}
