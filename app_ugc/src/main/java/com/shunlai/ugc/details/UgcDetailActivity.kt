package com.shunlai.ugc.details

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getui.gs.sdk.GsManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.*
import com.shunlai.share.WeChatUtil
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.share.QQUtil
import com.shunlai.ugc.R
import com.shunlai.ugc.UgcApiConfig
import com.shunlai.ugc.details.adapter.UgcEvaluateAdapter
import com.shunlai.ugc.entity.UgcBean
import com.shunlai.ugc.entity.UgcDetailCommentBean
import com.shunlai.ugc.entity.event.CommentEvent
import com.shunlai.ugc.entity.event.LikeEvent
import com.shunlai.ugc.entity.event.MoreCommentEvent
import com.shunlai.ugc.goodsDetail.window.UgcShareWindow
import com.shunlai.ui.MediaGridInset
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_ugc_detail_layout.*
import kotlinx.android.synthetic.main.title_ugc_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/5/7
 * @mobile 18711832023
 */
class UgcDetailActivity:BaseActivity(),UgcDetailView, UgcShareWindow.UgcShareWindowListener {
    override fun getMainContentResId(): Int = R.layout.activity_ugc_detail_layout

    override fun getToolBarResID(): Int=R.layout.title_ugc_layout

    private val mAdapter by lazy {
        UgcEvaluateAdapter(mContext, mutableListOf())
    }

    private val mPresenter by lazy {
        UgcDetailPresenter(this,this)
    }

    private val ugcShareWindow by lazy {
        UgcShareWindow(mContext,this)
    }

    private val isShowComment by lazy {
        intent.getBooleanExtra(RunIntentKey.IS_SHOW_COMMENT,false)
    }

    private val minHeight = 50
    private var oldBottom = 0
    private var newBottom = 0
    private val rect = Rect()

    override fun afterView() {
        EventBus.getDefault().register(this)
        mPresenter.queryComment()
        mPresenter.getDetail()
        initRv()
        initListener()
    }


    private fun initListener(){
        ll_back.setOnClickListener {
            finish()
        }
        bottom_layout.setOnClickListener {
            showCommentInput(CommentEvent())
        }
        main_layout.viewTreeObserver.addOnGlobalLayoutListener {
            updateWindowHeight()
        }
        bottom_input.setOnClickListener {
            hideInput(et_msg_input)
        }
        tv_send.setOnClickListener {
            if (TextUtils.isEmpty(et_msg_input.text.toString())){
                toast("请输入内容")
                return@setOnClickListener
            }
            mPresenter.doComment(et_msg_input.text.toString(),event?.commentId,event?.pid,event?.publishMid,event?.isReply)

        }
        tv_like.setOnClickListener {
            mPresenter.doLike()
        }
        tv_star.setOnClickListener {
            mPresenter.doCollect()
        }
        tv_attention_state.setOnClickListener {
            mPresenter.doAttention()
        }

        iv_more.setOnClickListener {
            mPresenter.bean?.let {
                ugcShareWindow.show(it.publishMid== PreferenceUtil.getString(Constant.USER_ID))
            }
        }
    }

    var event:CommentEvent?=null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showCommentInput(event: CommentEvent){
        this.event=event
        bottom_input.visibility= View.VISIBLE
        et_msg_input.requestFocus()
        et_msg_input.setText("")
        if (TextUtils.isEmpty(event.commentId)){
            et_msg_input.hint = "说点什么"
        }else{
            if (event.isReply==0){
                et_msg_input.hint = "评论 ${event.commentUserName}"
            }else{
                et_msg_input.hint = "回复 ${event.commentUserName}"
            }
        }
        showInput(et_msg_input)
    }

    var childMoreEvent:MoreCommentEvent?=null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showMoreChildComment(event: MoreCommentEvent){
        childMoreEvent=event
        mPresenter.queryMoreChildComment(event)
    }

    private var likeEvent:LikeEvent?=null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun doCommentLike(event: LikeEvent){
        likeEvent=event
        mPresenter.doCommentLike(event.commentId?:"",event.isLike)
    }


    private fun updateWindowHeight() {
        oldBottom = rect.bottom
        main_layout.getWindowVisibleDisplayFrame(rect)
        newBottom = rect.bottom
        if (oldBottom - newBottom > minHeight) {
            val params = bottom_input.layoutParams as RelativeLayout.LayoutParams
            params.setMargins(0, 0, 0, oldBottom - newBottom)
            bottom_input.layoutParams = params
        } else if (newBottom - oldBottom > minHeight) {
            val params = bottom_input.layoutParams as RelativeLayout.LayoutParams
            params.setMargins(0, 0, 0, 0)
            bottom_input.layoutParams = params
            bottom_input.visibility=View.GONE
        }
    }


    private fun initTitle(bean: UgcBean){
        ImageUtil.showCircleImgWithString(iv_user_avatar,mContext,bean.avatar?:"")
        tv_user_name.text=bean.nickName
        iv_user_avatar.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.MEMBER_ID]= bean.publishMid
            RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,this,params)
        }

        tv_user_name.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.MEMBER_ID]= bean.publishMid
            RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,this,params)
        }


        if (bean.publishMid!= PreferenceUtil.getString(Constant.USER_ID)){
            tv_attention_state.visibility=View.VISIBLE
        }
        if (bean.isFollow==1){
            tv_attention_state.text="已关注"
            tv_attention_state.setBackgroundResource(R.drawable.have_attention_bg)
        }else{
            tv_attention_state.text="+ 关注"
            tv_attention_state.setBackgroundResource(R.drawable.action_attention_bg)
        }
    }

    private var isCanLoadMore=false
    private fun initRv(){
        rv_evaluate.layoutManager=LinearLayoutManager(mContext)
        rv_evaluate.addItemDecoration(MediaGridInset(1,ScreenUtils.dip2px(mContext,16f),false,true))
        rv_evaluate.adapter=mAdapter
        rv_evaluate.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    if (recyclerView.computeVerticalScrollExtent()+recyclerView.computeVerticalScrollOffset()>=recyclerView.computeVerticalScrollRange()
                        &&isCanLoadMore){
                        mPresenter.queryComment()
                        isCanLoadMore=false
                    }
                }
            }
        })
    }


    override fun initData(ugc: UgcBean?) {
        mAdapter.setUgcData(ugc)
        ugc?.let {bean->
            initTitle(bean)
            if (bean.isLike==0){
                tv_like.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_icon_style_one,0,0,0)
            }else{
                tv_like.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.like_choose_icon_style_one,0,0,0)
            }
            if (bean.likes==0){
                tv_like.text=""
            }else{
                tv_like.text=bean.likes.toString()
            }

            if (bean.isFavorite==0){
                tv_star.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.star_icon_style_one,0,0,0)
            }else{
                tv_star.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.star_icon_choose_style_one,0,0,0)
            }
            if (bean.favorites==0){
                tv_star.text=""
            }else{
                tv_star.text=bean.favorites.toString()
            }
        }
    }

    override fun showLoading(value: String) {
       showBaseLoading()
    }

    override fun hideLoading() {
       hideBaseLoading()
    }


    override fun onDetailCallBack(ugc: UgcBean?) {
        initData(ugc)
        if (isShowComment){
            showCommentInput(CommentEvent())
        }
    }

    override fun onCommentListCallBack(data: MutableList<UgcDetailCommentBean>) {
        mAdapter.mData.addAll(data)
        mAdapter.notifyDataSetChanged()
        if (data.isNotEmpty()){
            isCanLoadMore=true
        }
    }

    override fun onMoreChildCommentCallBack(data: MutableList<UgcDetailCommentBean>) {
        if (data.isNotEmpty()){
            if (childMoreEvent?.page==1){
                mAdapter.mData[childMoreEvent!!.currentPosition!!-1].ugcComments=data
            }else{
                mAdapter.mData[childMoreEvent!!.currentPosition!!-1].ugcComments.addAll(data)
            }
            val page=(childMoreEvent?.page?:1)+1
            mAdapter.mData[childMoreEvent!!.currentPosition!!-1].childPage=page
            mAdapter.notifyItemChanged(childMoreEvent!!.currentPosition!!)
        }
    }

    override fun onDoCommentBack(bean: UgcDetailCommentBean) {
       if (event?.commentId==null){
           mAdapter.mData.add(0,bean)
           var totalComment=mAdapter.ugcBean?.comments?:0
           totalComment+=1
           mAdapter.ugcBean?.comments=totalComment
           mAdapter.notifyDataSetChanged()
           rv_evaluate.scrollToPosition(1)
           mPresenter.trackUgcComment(true,null)
       }else{
           mAdapter.mData[event!!.currentPosition!!-1].ugcComments.add(0,bean)
           if ( mAdapter.mData[event!!.currentPosition!!-1].commentNums!=null){
               val num= mAdapter.mData[event!!.currentPosition!!-1].commentNums?:0+1
               mAdapter.mData[event!!.currentPosition!!-1].commentNums=num
           }
           mAdapter.notifyItemChanged(event!!.currentPosition!!)
       }
    }

    override fun onLikeBack(value: Int) {
       mPresenter.bean?.isLike=value
        var likes=  mPresenter.bean?.likes?:0
        if (value==1){
            likes+=1
            trackUgcLike()
        }else{
            likes-=1
        }
        mPresenter.bean?.likes=likes
        initData(mPresenter.bean)
    }

    override fun onCollectBack(value: Int) {
        mPresenter.bean?.isFavorite=value
        var collects=  mPresenter.bean?.favorites?:0
        if (value==1){
            collects+=1
        }else{
            collects-=1
        }
        mPresenter.bean?.favorites=collects

        initData(mPresenter.bean)
    }

    override fun onAttentionBack(value: Int) {
        mPresenter.bean?.isFollow=value
        initData(mPresenter.bean)
    }

    override fun onCommentLikeBack(value: Int) {
        likeEvent?.let {
            if (it.childPosition==null){
                var likes=mAdapter.mData[it.parentPosition!!-1].likeNum?:0
                if (value==1){
                    trackCommentLike(mAdapter.mData[it.parentPosition!!-1])
                    likes+=1
                }else{
                    likes-=1
                }
                mAdapter.mData[it.parentPosition!!-1].isLike=value
                mAdapter.mData[it.parentPosition!!-1].likeNum=likes
                mAdapter.notifyItemChanged(it.parentPosition!!)
            }else{
                var likes=mAdapter.mData[it.parentPosition!!-1].ugcComments[it.childPosition!!].likeNum?:0
                if (value==1){
                    trackCommentLike(mAdapter.mData[it.parentPosition!!-1].ugcComments[it.childPosition!!])
                    likes+=1
                }else{
                    likes-=1
                }
                mAdapter.mData[it.parentPosition!!-1].ugcComments[it.childPosition!!].isLike=value
                mAdapter.mData[it.parentPosition!!-1].ugcComments[it.childPosition!!].likeNum=likes
                mAdapter.notifyItemChanged(it.parentPosition!!)
            }
        }

    }

    override fun onDeleteUgc(result: Int) {
        if (result==1){
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        rv_evaluate.adapter=null
    }

    override fun onStart() {
        super.onStart()
        rv_evaluate.adapter=mAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @SuppressLint("CheckResult")
    override fun onWeChatShare() {
        if (!mPresenter.bean?.imageList.isNullOrEmpty()){
            Observable.just(mPresenter.bean!!.imageList!![0]).map {
                return@map ImageUtil.getBitmapFromUrl(mContext,it)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe{
                    WeChatUtil.getInstance().shareWeChatWithWeb("${UgcApiConfig.ROOT_URL}/static/app/xwsapp/#/ugc?id=${mPresenter.bean?.id}",mPresenter.bean?.content?:"","有趣的人都在透壳App",it,mPresenter.bean?.ugcType=="2")
                }
        }else{
            toast("分享的照片不存在!")
        }
        ugcShareWindow.dismiss()
    }

    @SuppressLint("CheckResult")
    override fun onCircleShare() {
        if (!mPresenter.bean?.imageList.isNullOrEmpty()){
            Observable.just(mPresenter.bean!!.imageList!![0]).map {
                return@map ImageUtil.getBitmapFromUrl(mContext,it)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe{
                    WeChatUtil.getInstance().shareCircleWithWeb("${UgcApiConfig.ROOT_URL}/static/app/xwsapp/#/ugc?id=${mPresenter.bean?.id}",mPresenter.bean?.content?:"","有趣的人都在透壳App",it,mPresenter.bean?.ugcType=="2")
                }
        }else{
            toast("分享的照片不存在!")
        }
        ugcShareWindow.dismiss()
    }

    override fun onQQShare() {
        if (!mPresenter.bean?.imageList.isNullOrEmpty()){
            QQUtil.getInstance().shareQQWithWeb(this,"${UgcApiConfig.ROOT_URL}/static/app/xwsapp/#/ugc?id=${mPresenter.bean?.id}",
                mPresenter.bean?.content?:"","有趣的人都在透壳App",mPresenter.bean!!.imageList!![0])
        }
        ugcShareWindow.dismiss()
    }

    override fun onQQZONEShare() {
        if (!mPresenter.bean?.imageList.isNullOrEmpty()){
            QQUtil.getInstance().shareQQZONEWithWeb(this,"${UgcApiConfig.ROOT_URL}/static/app/xwsapp/#/ugc?id=${mPresenter.bean?.id}",
                mPresenter.bean?.content?:"","有趣的人都在透壳App",mPresenter.bean!!.imageList!![0])
        }
        ugcShareWindow.dismiss()
    }

    override fun onCopyUrl() {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("Label", "${UgcApiConfig.ROOT_URL}/static/app/xwsapp/#/ugc?id=${mPresenter.bean?.id}")
        cm.setPrimaryClip(mClipData)
        toast("笔记地址已经复制到剪贴板")
    }

    override fun onBuildImg() {
        ugcShareWindow.dismiss()
        mPresenter.bean?.let {
            val params= mutableMapOf<String,Any?>()
            params["ugcBean"]=GsonUtil.toJson(it)
            RouterManager.startActivityWithParams(BundleUrl.UGC_SHARE_ACTIVITY,this,params)
        }
    }

    override fun onBlock() {
        ugcShareWindow.dismiss()
        AlertDialog.Builder(mContext).setTitle("提示").setMessage("确定要拉黑对方吗？").setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }.setPositiveButton("确认") { dialog, _ ->
            dialog.dismiss()
            mPresenter.doBlock()
            toast("拉黑成功!")
        }.show()
    }

    override fun onComplaint() {
        ugcShareWindow.dismiss()
        val params= mutableMapOf<String,Any?>()
        params[RunIntentKey.UGC_ID]=mPresenter.bean?.id
        RouterManager.startActivityWithParams(BundleUrl.COMPLAIN_ACTIVITY,this,params)
    }

    override fun onDelete() {
        ugcShareWindow.dismiss()
        AlertDialog.Builder(mContext).setTitle("提示").setMessage("是否确定删除？").setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }.setPositiveButton("确认") { dialog, _ ->
            dialog.dismiss()
            mPresenter.doUgcDelete()
        }.show()
    }

    private fun trackUgcLike(){
        val bean= mPresenter.bean
        val params = JSONObject()
        bean?.topic?.id?.let {
            params.put("topic_ids", it)
        }
        params.put("uid",bean?.memberId)
        params.put("Page_name","Note_details")
        params.put("note_type",bean?.ugcType)
        params.put("note_id",bean?.ugcId)
        GsManager.getInstance().onEvent("like_note", params)
    }

    private fun trackCommentLike(bean:UgcDetailCommentBean){
        val ugcBean= mPresenter.bean
        val params = JSONObject()
        ugcBean?.topic?.id?.let {
            params.put("topic_ids", it)
        }
        params.put("uid",ugcBean?.memberId)
        params.put("comment_id",bean.commentId)
        params.put("note_type",ugcBean?.ugcType)
        params.put("note_id",ugcBean?.ugcId)
        GsManager.getInstance().onEvent("like_comment", params)
    }
}
