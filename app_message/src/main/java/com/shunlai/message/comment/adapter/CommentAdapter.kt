package com.shunlai.message.comment.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.message.R
import com.shunlai.message.entity.CommentBean
import com.shunlai.message.entity.event.LikeEvent
import com.shunlai.router.RouterManager
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_comment_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class CommentAdapter(var mContext:Context,var mDates:MutableList<CommentBean>,var mListener:CommentItemListener):SRecyclerAdapter(mContext){


    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext, R.layout.item_comment_layout,null)
        return CommentViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as CommentViewHolder
        viewHolder.setData(mDates[position],position)
        viewHolder.itemView.setOnClickListener {
            //没有返回ugcType
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.UGC_ID]= mDates[position].ugcId
            RouterManager.startActivityWithParams(BundleUrl.UGC_DETAIL_ACTIVITY,mContext as FragmentActivity,params)

        }
    }

    override fun getCount(): Int=mDates.size

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text = "暂无评论回复"
        view.findViewById<ImageView>(R.id.iv_empty_image)
            .setImageResource(R.mipmap.empty_evaluate_icon)
        return DefaultLoadMoreViewHolder(view)
    }


    inner class CommentViewHolder(var view:View) :RecyclerView.ViewHolder(view){
        fun setData(bean:CommentBean,position: Int){
            ImageUtil.showCircleImgWithString(view.iv_avatar,mContext,bean.memberAvatar?:"",R.mipmap.user_default_icon)
            view.iv_avatar.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_ID]= bean.memberId
                RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
            }
            view.tv_user_name.text=bean.memberName
            view.tv_comment_content.text=bean.content
            view.tv_comment_date.text=bean.displayTime
            ImageUtil.showRoundImgWithStringAndRadius(view.iv_ucg_image,mContext,bean.image?:"",10f)
            if (bean.isLike==1){
                view.iv_is_like.setImageResource(R.mipmap.fabulous_icon)
            }else{
                view.iv_is_like.setImageResource(R.mipmap.un_fabulous_icon)
            }
            if (bean.type==1){
                view.tv_comment_state.text="回复了您的评论"
            }else{
                view.tv_comment_state.text="评论了您的笔记"
            }

            view.iv_is_like.setOnClickListener {
                mListener.onLikeClick(LikeEvent(bean.ugcCommentId?:"0",position))
            }
            view.tv_do_reply.setOnClickListener {
                mListener.onCommentClick(bean)
            }

        }
    }

    interface CommentItemListener{
        fun onLikeClick(event: LikeEvent)

        fun onCommentClick(bean:CommentBean)
    }
}
