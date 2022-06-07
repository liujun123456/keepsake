package com.shunlai.im.adapter.viewHolder

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.im.R
import com.shunlai.im.adapter.BaseChatAdapter
import com.shunlai.im.entity.ChatGoodsBean
import com.shunlai.im.utils.Constant
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.tencent.imsdk.v2.V2TIMMessage
import kotlinx.android.synthetic.main.item_chat_goods_right_layout.view.*

/**
 * @author Liu
 * @Date   2021/6/3
 * @mobile 18711832023
 */
class GoodsViewProvider(var type:Int, var mContext: Context, var mAdapter: BaseChatAdapter) {
    fun getViewHolder(): BaseViewHolder{
        return if (type== Constant.GOODS_RECEIVE){
            val view=View.inflate(mContext, R.layout.item_chat_goods_left_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            GoodsLeftHolder(view)
        }else{
            val view=View.inflate(mContext, R.layout.item_chat_goods_right_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            GoodsRightHolder(view)
        }
    }

    inner class GoodsLeftHolder(view: View): BaseChatViewHolder(view){
        override fun setData(msg: Any, position: Int) {
            try {
                msg as V2TIMMessage
                setTimestamp(msg,position,mAdapter)
                loadAvatar(msg.faceUrl,msg.sender?:"")
                val elem=msg.customElem
                val data=String(elem.data)
                val goods=GsonUtil.fromJson(data,ChatGoodsBean::class.java)
                ImageUtil.showRoundImgWithStringAndRadius(view.findViewById(R.id.iv_goods),view.context,goods.img?:"",8f)
                view.findViewById<TextView>(R.id.tv_desc).text=goods.name
                view.findViewById<TextView>(R.id.tv_price).text="￥${goods.price}"
                view.setOnClickListener {
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.UGC_ID]=goods.ugcId
                    params[RunIntentKey.PRODUCT_ID]= goods.goodsId
                    RouterManager.startActivityWithParams(BundleUrl.UGC_GOODS_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
                }
            }catch (e:Exception){
                Log.e("GoodsLeftHolder----->",e.toString())

            }

        }

    }

    inner class GoodsRightHolder(view: View): BaseChatViewHolder(view){
        override fun setData(msg: Any, position: Int) {
            try {
                msg as V2TIMMessage
                setTimestamp(msg,position,mAdapter)
                loadAvatar(msg.faceUrl,msg.sender?:"")
                val elem=msg.customElem
                val data=String(elem.data)
                val goods=GsonUtil.fromJson(data,ChatGoodsBean::class.java)
                ImageUtil.showRoundImgWithStringAndRadius(view.findViewById(R.id.iv_goods),view.context,goods.img?:"",8f)
                view.findViewById<TextView>(R.id.tv_desc).text=goods.name
                view.findViewById<TextView>(R.id.tv_price).text="￥${goods.price}"
                view.setOnClickListener {
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.UGC_ID]=goods.ugcId
                    params[RunIntentKey.PRODUCT_ID]= goods.goodsId
                    RouterManager.startActivityWithParams(BundleUrl.UGC_GOODS_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
                }
                if (msg.status== V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL){
                    view.iv_send_fail.visibility=View.VISIBLE
                }else{
                    view.iv_send_fail.visibility=View.INVISIBLE
                }
            }catch (e:Exception){
                Log.e("GoodsRightHolder----->",e.toString())
            }
        }
    }
}
