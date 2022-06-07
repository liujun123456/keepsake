package com.shunlai.ugc.details.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.router.RouterManager
import com.shunlai.ugc.R
import com.shunlai.ugc.entity.UgcDetailCommentBean
import com.shunlai.ugc.entity.event.CommentEvent
import com.shunlai.ugc.entity.event.LikeEvent
import kotlinx.android.synthetic.main.item_child_evaluate_layout.view.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/5/7
 * @mobile 18711832023
 */
class UgcChildEvaluateAdapter(var mContext:Context, var mData:MutableList<UgcDetailCommentBean>,var ptPosition:Int): RecyclerView.Adapter<UgcChildEvaluateAdapter.UgcChildEvaluateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UgcChildEvaluateViewHolder {
        val view=View.inflate(mContext,R.layout.item_child_evaluate_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        return UgcChildEvaluateViewHolder(view)
    }

    override fun getItemCount(): Int=mData.size

    override fun onBindViewHolder(holder: UgcChildEvaluateViewHolder, position: Int) {
        holder.setData(mData[position],position)
    }

    inner class UgcChildEvaluateViewHolder(var mView:View) :RecyclerView.ViewHolder(mView){
        fun setData(bean:UgcDetailCommentBean,position: Int){
            ImageUtil.showCircleImgWithString(mView.iv_user_avatar,mView.context,bean.avatar?:"")
            mView.iv_user_avatar.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_ID]= bean.publishMid
                RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
            }
            mView.tv_user_name.text=bean.nickName
            mView.tv_hui_fu.text=bean.replyNickName
            mView.tv_content.text=bean.replyContent
            mView.tv_time.text=bean.replyTime
            if (bean.likeNum?:0>0){
                mView.tv_likes.text=bean.likeNum.toString()
            }else{
                mView.tv_likes.text=""
            }

            if (bean.isLike==1){
                mView.tv_likes.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.like_choose_icon_style_one,0,0)
            }else{
                mView.tv_likes.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.like_icon_style_one,0,0)
            }
            mView.tv_likes.setOnClickListener {
                EventBus.getDefault().post(LikeEvent().apply {
                    parentPosition=ptPosition
                    childPosition=position
                    commentId=bean.id?:""
                    isLike=bean.isLike?:0
                })
            }
            mView.setOnClickListener {
                EventBus.getDefault().post(CommentEvent().apply {
                    commentId=bean.commentId
                    pid=bean.id
                    commentUserName=bean.nickName
                    isReply=1
                    publishMid=bean.publishMid
                    currentPosition=ptPosition
                })
            }
        }

    }
}
