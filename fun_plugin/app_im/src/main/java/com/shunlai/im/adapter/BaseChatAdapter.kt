package com.shunlai.im.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.im.R
import com.shunlai.im.adapter.viewHolder.*
import com.shunlai.im.utils.Constant
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMMessage.*

/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
abstract class BaseChatAdapter(var mContext: Context) : RecyclerView.Adapter<BaseViewHolder>() {

    fun buildViewHolder(type: Int): BaseViewHolder {
        return when (type) {
            Constant.TXT_SEND, Constant.TXT_RECEIVE -> return TextViewProvider(
                type,
                mContext,
                this
            ).getViewHolder()
            Constant.IMAGE_SEND, Constant.IMAGE_RECEIVE -> return ImageViewProvider(
                type,
                mContext,
                this
            ).getViewHolder()
            Constant.VIDEO_SEND, Constant.VIDEO_RECEIVE -> return VideoViewProvider(
                type,
                mContext,
                this
            ).getViewHolder()
            Constant.GOODS_SEND, Constant.GOODS_RECEIVE -> return GoodsViewProvider(
                type,
                mContext,
                this
            ).getViewHolder()
            Constant.BLACK_STATE->return BlackViewProvider(mContext).getViewHolder()
            else -> EmptyViewHolder(View.inflate(mContext, R.layout.item_chat_empty_layout, null))
        }
    }

    fun buildViewType(msg: V2TIMMessage): Int {
        when (msg.elemType) {
            V2TIM_ELEM_TYPE_TEXT -> {
                return if (msg.sender == V2TIMManager.getInstance().loginUser) {
                    Constant.TXT_SEND
                } else {
                    Constant.TXT_RECEIVE
                }
            }
            V2TIM_ELEM_TYPE_IMAGE -> {
                return if (msg.sender == V2TIMManager.getInstance().loginUser) {
                    Constant.IMAGE_SEND
                } else {
                    Constant.IMAGE_RECEIVE
                }
            }
            V2TIM_ELEM_TYPE_VIDEO -> {
                return if (msg.sender == V2TIMManager.getInstance().loginUser) {
                    Constant.VIDEO_SEND
                } else {
                    Constant.VIDEO_RECEIVE
                }
            }
            V2TIM_ELEM_TYPE_CUSTOM -> {
                when (msg.customElem.description) {
                    "goods" -> {
                        return if (msg.sender == V2TIMManager.getInstance().loginUser) {
                            Constant.GOODS_SEND
                        } else {
                            Constant.GOODS_RECEIVE
                        }
                    }
                    "black" -> {
                        return if (msg.sender == V2TIMManager.getInstance().loginUser) {
                            Constant.BLACK_STATE
                        } else {
                            Constant.BLACK_STATE
                        }
                    }
                }
            }
        }
        return -1
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetachedFromWindow()
    }

    abstract fun getItem(position: Int): Any
}
