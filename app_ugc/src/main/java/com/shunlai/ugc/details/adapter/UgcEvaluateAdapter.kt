package com.shunlai.ugc.details.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.*
import com.shunlai.im.utils.ScreenUtil
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.ugc.R
import com.shunlai.ugc.entity.BannerInfo
import com.shunlai.ugc.entity.UgcBean
import com.shunlai.ugc.entity.UgcDetailCommentBean
import com.shunlai.ugc.entity.UgcGoodsBean
import com.shunlai.ugc.entity.event.CommentEvent
import com.shunlai.ugc.entity.event.LikeEvent
import com.shunlai.ugc.entity.event.MoreCommentEvent
import com.shunlai.ui.MediaGridInset
import com.shunlai.ui.StarLayout
import kotlinx.android.synthetic.main.item_ugc_detail_layout.view.*
import kotlinx.android.synthetic.main.item_ugc_evaluate_layout.view.*
import kotlinx.android.synthetic.main.item_ugc_evaluate_layout.view.tv_time
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/5/7
 * @mobile 18711832023
 */
class UgcEvaluateAdapter(var mContext:Context,var mData:MutableList<UgcDetailCommentBean>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var ugcBean:UgcBean?=null

    fun setUgcData(ugcBean:UgcBean?){
        this.ugcBean=ugcBean
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==10086){
            val view=View.inflate(mContext,R.layout.item_ugc_detail_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            return UgcDetailViewHolder(view)
        }else{
            val view=View.inflate(mContext,R.layout.item_ugc_evaluate_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            return UgcEvaluateViewHolder(view)
        }
    }

    override fun getItemCount(): Int=mData.size+1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UgcEvaluateViewHolder){
            holder.setData(mData[position-1],position)
        }else{
            holder as UgcDetailViewHolder
            holder.setData(ugcBean)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position==0){
            return 10086
        }else{
            return 10087
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is UgcDetailViewHolder){
            holder.stopVideo()
        }
    }

    inner class UgcEvaluateViewHolder(var mView:View) :RecyclerView.ViewHolder(mView){
        fun setData(bean:UgcDetailCommentBean,position: Int){
            ImageUtil.showCircleImgWithString(mView.iv_user_avatar,mView.context,bean.avatar?:"")
            mView.iv_user_avatar.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_ID]= bean.publishMid
                RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
            }
            mView.tv_user_name.text=bean.nickName
            mView.tv_content.text=bean.content
            mView.tv_time.text=bean.displayTime
            if (bean.likeNum?:0>0){
                mView.tv_like.text=bean.likeNum.toString()
            }else{
                mView.tv_like.text=""
            }
            if (bean.isLike==1){
                mView.tv_like.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.like_choose_icon_style_one,0,0)
            }else{
                mView.tv_like.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.like_icon_style_one,0,0)
            }
            mView.tv_like.setOnClickListener {
                EventBus.getDefault().post(LikeEvent().apply {
                    parentPosition=position
                    commentId=bean.id?:""
                    isLike=bean.isLike?:0
                })
            }
            buildChild(bean.ugcComments,position)
            mView.setOnClickListener {
                EventBus.getDefault().post(CommentEvent().apply {
                    commentId=bean.id
                    pid=bean.id
                    commentUserName=bean.nickName
                    currentPosition=position
                    isReply=1
                    publishMid=bean.publishMid
                })
            }
            if (bean.commentNums?:0>bean.ugcComments.size){
                mView.tv_more_evaluate.visibility=View.VISIBLE
            }else{
                mView.tv_more_evaluate.visibility=View.GONE
            }
            mView.tv_more_evaluate.setOnClickListener {
                EventBus.getDefault().post(MoreCommentEvent().apply {
                    commentId=bean.id
                    currentPosition=position
                    page=bean.childPage
                })
            }
        }

        private fun buildChild(beans:MutableList<UgcDetailCommentBean>,position: Int){
            val mChildAdapter by lazy {
                UgcChildEvaluateAdapter(mContext, beans,position)
            }
            mView.ll_child_evaluate.layoutManager=LinearLayoutManager(mView.context)
            for (i in 0 until mView.ll_child_evaluate.itemDecorationCount){
                mView.ll_child_evaluate.removeItemDecorationAt(i)
            }
            mView.ll_child_evaluate.addItemDecoration(MediaGridInset(1,ScreenUtils.dip2px(mView.context,8f),false,true))
            mView.ll_child_evaluate.adapter=mChildAdapter
        }
    }

    inner class UgcDetailViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        @SuppressLint("SetTextI18n")
        fun setData(bean: UgcBean?){
            if (bean!=null){
                mView.ll_ugc_detail.visibility=View.VISIBLE
                mView.iv_ugc_detail_skeleton.visibility=View.GONE
            }else{
                mView.ll_ugc_detail.visibility=View.GONE
                mView.iv_ugc_detail_skeleton.visibility=View.VISIBLE
            }
            bean?.let {
                if (mData.size==0){
                    mView.ll_empty_value.visibility=View.VISIBLE
                }else{
                    mView.ll_empty_value.visibility=View.GONE
                }
                mView.tv_ugc_content.text=bean.content
                mView.tv_time.text=bean.displayTime
                if (bean.comments==0){
                    mView.tv_comment_count.text="评论"
                }else{
                    mView.tv_comment_count.text="评论 (${bean.comments})"
                }

                if (bean.likeMembers?.size==0){
                    mView.ll_like_member.visibility=View.GONE
                }else{
                    mView.tv_like_count.text="${bean.likes?:0}人觉得很赞"

                    mView.rl_avatar.removeAllViews()
                    bean.likeMembers?.forEachIndexed { index, likeMember ->
                        if (index>=5)return@forEachIndexed
                        val img=ImageView(mContext)
                        val params=RelativeLayout.LayoutParams(ScreenUtils.dip2px(mContext,24f),ScreenUtils.dip2px(mContext,24f))
                        params.leftMargin=ScreenUtils.dip2px(mContext,16f.times(index))
                        img.layoutParams=params
                        ImageUtil.showCircleImgWithString(img,mContext,likeMember.avatar?:"",R.mipmap.user_default_icon)
                        mView.rl_avatar.addView(img,0)
                    }
                }
                if (TextUtils.isEmpty(bean.topic?.tag)){
                    mView.tv_huati.visibility= View.GONE
                }else{
                    mView.tv_huati.visibility= View.VISIBLE
                    if (bean.topic?.activity==true){
                        mView.tv_huati.setCompoundDrawablesWithIntrinsicBounds(R.drawable.activity_loading_bg,0,0,0)
                        (mView.tv_huati.compoundDrawables[0] as AnimationDrawable).start()
                        mView.tv_huati.text=" ${bean.topic?.tag}"
                    }else{
                        mView.tv_huati.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                        mView.tv_huati.text="# ${bean.topic?.tag}"
                    }
                    mView.tv_huati.setOnClickListener {
                        val params= mutableMapOf<String,Any?>()
                        params[RunIntentKey.HT_DETAIL]= GsonUtil.toJson(bean.topic!!)
                        RouterManager.startActivityWithParams(BundleUrl.HUA_TI_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
                    }
                }
                if (it.ugcGoods.isNullOrEmpty()&&it.imageList.isNullOrEmpty()){
                    mView.x_banner.visibility=View.GONE
                    mView.rl_video.visibility=View.GONE
                    mView.sl_layout.visibility=View.GONE
                    val params=mView.tv_ugc_content.layoutParams as  RelativeLayout.LayoutParams
                    params.topMargin=ScreenUtils.dip2px(mContext,16f)
                    mView.tv_ugc_content.layoutParams=params
                }else if (it.ugcGoods.isNullOrEmpty()&&!it.imageList.isNullOrEmpty()){
                    mView.sl_layout.visibility=View.GONE
                    if (it.ugcType=="1"){
                        buildBanner(it.imageList?: mutableListOf(),2)
                    }else if (it.ugcType=="2"){
                        buildVideo(it,2)
                    }
                }else if (!it.ugcGoods.isNullOrEmpty()&&it.imageList.isNullOrEmpty()){
                    mView.x_banner.visibility=View.GONE
                    mView.rl_video.visibility=View.GONE
                    buildGoods(bean.id?:"",it.ugcGoods?: mutableListOf())
                    val params=mView.sl_layout.layoutParams as RelativeLayout.LayoutParams
                    params.setMargins(0,ScreenUtils.dip2px(mContext,16f),0,0)
                    mView.sl_layout.layoutParams=params

                    val params2=mView.tv_ugc_content.layoutParams as  RelativeLayout.LayoutParams
                    params2.topMargin=ScreenUtils.dip2px(mContext,112f)
                    mView.tv_ugc_content.layoutParams=params2
                }else{
                    buildGoods(bean.id?:"",it.ugcGoods?: mutableListOf())
                    if (it.ugcType=="1"){
                        buildBanner(it.imageList?: mutableListOf())
                    }else if (it.ugcType=="2"){
                        buildVideo(it)
                    }
                }
            }
        }

        fun stopVideo(){
            if (mView.rl_video.visibility==View.VISIBLE&&mView.video_view.isPlaying){
                mView.video_view.stop()
            }
            cancelActivityAnim()
        }

        private fun cancelActivityAnim(){
            mView.tv_huati.compoundDrawables[0]?.let {
                (it as AnimationDrawable).stop()
            }
        }

        private fun buildVideo(bean: UgcBean,type:Int=1){
            mView.x_banner.visibility=View.GONE
            mView.tv_play_icon.visibility=View.GONE
            mView.rl_video.visibility=View.VISIBLE
            if (!bean.imageList.isNullOrEmpty()){
                ImageUtil.showPreView(mView.iv_first_frame,mContext,Uri.parse(bean.imageList!![0]))
            }
            val uri=Uri.parse(bean.video)
            mView.video_view.setVideoURI(uri)
            mView.video_view.setOnPreparedListener {
                mView.progress_bar.visibility=View.GONE
                initVideoLayout(it.videoWidth,it.videoHeight)
                mView.tv_play_icon.visibility=View.VISIBLE
            }
            mView.tv_play_icon.setOnClickListener {
                if (mView.video_view.isPlaying){
                    mView.video_view.pause()
                    mView.tv_play_icon.visibility=View.VISIBLE
                }else{
                    mView.rl_load.visibility=View.GONE
                    mView.tv_play_icon.visibility=View.GONE
                    mView.video_view.start()
                }
            }
            mView.video_view.setOnClickListener {
                if (mView.video_view.isPlaying){
                    mView.tv_play_icon.visibility=View.VISIBLE
                    mView.video_view.pause()
                }else{
                    mView.rl_load.visibility=View.GONE
                    mView.tv_play_icon.visibility=View.GONE
                    mView.video_view.start()
                }
            }

            if (type==2){
                val params=mView.tv_ugc_content.layoutParams as  RelativeLayout.LayoutParams
                params.topMargin=ScreenUtils.dip2px(mContext,456f)
                mView.tv_ugc_content.layoutParams=params
            }
        }

        private fun initVideoLayout(videoWidth:Int,videoHeight:Int){
            if (videoWidth <= 0 && videoHeight <= 0) {
                return
            }
            val params: ViewGroup.LayoutParams = mView.video_view.layoutParams
            if (videoWidth>videoHeight){
                params.width=ScreenUtils.getScreenWidth(mContext)
                params.height =mView.video_view.height.times(videoHeight).div(videoWidth)
            }else{
                params.height=ScreenUtils.dip2px(mContext,440f)
                params.width =mView.video_view.width.times(videoWidth).div(videoHeight)
            }
            mView.video_view.layoutParams = params
        }

        private fun buildBanner(data:MutableList<String>,type:Int=1){
            mView.x_banner.visibility=View.VISIBLE
            mView.rl_video.visibility=View.GONE
            if (ugcBean?.width?:0>0&&ugcBean?.height?:0>0){
                val imgWidth=ugcBean?.width!!
                val imgHeight=ugcBean?.height!!
                var bannerHeight=(imgHeight.toFloat()/imgWidth.toFloat()).times(ScreenUtil.getScreenWidth(mContext)).toInt()
                val minHeight=((ScreenUtil.getScreenWidth(mContext).toFloat()/16)*9).toInt()
                val maxHeight=ScreenUtil.dip2px(mContext,440f)
                if (bannerHeight<minHeight){
                    bannerHeight=minHeight
                }
                if (bannerHeight>maxHeight){
                    bannerHeight=maxHeight
                }
                mView.x_banner.layoutParams=RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,bannerHeight)
                if (type==2){
                    val params2=mView.tv_ugc_content.layoutParams as  RelativeLayout.LayoutParams
                    params2.topMargin=bannerHeight+ScreenUtils.dip2px(mContext,16f)
                    mView.tv_ugc_content.layoutParams=params2
                }else{
                    val params=mView.sl_layout.layoutParams as RelativeLayout.LayoutParams
                    params.setMargins(0,bannerHeight-ScreenUtils.dip2px(mContext,24f),0,0)
                    mView.sl_layout.layoutParams=params

                    val params2=mView.tv_ugc_content.layoutParams as  RelativeLayout.LayoutParams
                    params2.topMargin=params.topMargin+ScreenUtils.dip2px(mContext,96f)
                    mView.tv_ugc_content.layoutParams=params2
                }

            }
            val bannerList= mutableListOf<BannerInfo>()
            data.forEach {
                bannerList.add(BannerInfo(it))
            }
            mView.x_banner.setBannerData(R.layout.banner_item_layout,bannerList)
            mView.x_banner.loadImage { _, _, view, position ->
                ImageUtil.showCropImgWithString(view as ImageView,mContext,bannerList[position].imageUrl,R.mipmap.default_icon)
            }
            mView.x_banner.setOnItemClickListener { _, _, _, position ->
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.IMAGE_URL]=bannerList[position].imageUrl
                RouterManager.startActivityWithParams(BundleUrl.PHOTO_SIMPLE_PREVIEW_PATH_ACTIVITY,mContext as FragmentActivity,params)

            }
        }

        private fun buildGoods(ugcId:String,list:MutableList<UgcGoodsBean>){
            mView.ll_goods.removeAllViews()
            if (!list.isNullOrEmpty()){
                if (list.size==1){
                    val goodsBean=list[0]
                    val view=View.inflate(mContext,R.layout.item_detail_ugc_goods_layout,null)
                    view.layoutParams= ViewGroup.LayoutParams(ScreenUtils.getScreenWidth(mView.context)-ScreenUtils.dip2px(mContext,16f),
                        ScreenUtils.dip2px(mView.context,80f))
                    ImageUtil.showRoundImgWithStringAndRadius(view.findViewById(R.id.iv_product_img),mContext,goodsBean.productImg?:"",8f)
                    view.findViewById<TextView>(R.id.tv_product_desc).text=goodsBean.productName
                    view.findViewById<StarLayout>(R.id.star_view).setRating(goodsBean.evaluate?:0)
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
                    mView.ll_goods.addView(view)
                }else{
                    list.forEach {goodsBean->
                        val view=View.inflate(mContext,R.layout.item_detail_ugc_goods_layout,null)
                        view.layoutParams= ViewGroup.LayoutParams(  ScreenUtils.dip2px(mView.context,263f),
                            ScreenUtils.dip2px(mView.context,80f))
                        ImageUtil.showRoundImgWithStringAndRadius(view.findViewById(R.id.iv_product_img),mContext,goodsBean.productImg?:"",16f)
                        view.findViewById<TextView>(R.id.tv_product_desc).text=goodsBean.productName
                        view.findViewById<StarLayout>(R.id.star_view).setRating(goodsBean.evaluate?:0)
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
                        mView.ll_goods.addView(view)
                    }
                }
            }
        }
    }
}
