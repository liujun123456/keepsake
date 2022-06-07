package com.shunlai.main.fragment.attention.adapter

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
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.main.ActionInterface
import com.shunlai.main.R
import com.shunlai.main.entities.UgcTjBean
import com.shunlai.router.RouterManager
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import kotlinx.android.synthetic.main.item_tj_ugc_layout.view.*

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class TuiJAdapter(var mContext:Context,var mData:MutableList<UgcTjBean>,var mInterface: ActionInterface):SRecyclerAdapter(mContext) {

    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==1000){
            val view =View.inflate(mContext,R.layout.item_tj_notice_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.dip2px(mContext,64f))
            return TjNoticeViewHolder(view)
        }else{
            val view= View.inflate(mContext, R.layout.item_tj_ugc_layout,null)
            view.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            return TjUgcViewHolder(view)
        }
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is TjUgcViewHolder){
            viewHolder.setData(mData[position-1],position-1)
        }
    }

    override fun getCount(): Int=mData.size+1

    override fun getViewType(position: Int): Int {
        if (position==0){
            return 1000
        }
        return 2000
    }


    class TjNoticeViewHolder(var view:View):RecyclerView.ViewHolder(view)

    inner class TjUgcViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:UgcTjBean,position: Int){
            ImageUtil.showCircleImgWithString(mView.iv_avatar,mView.context,bean.avatar?:"")
            mView.tv_user_name.text=bean.nickName
            mView.tv_user_desc.text=bean.introduce
            mView.ll_img_layout.removeAllViews()
            if (!bean.ugcHeadPictures.isNullOrEmpty()){
                if (bean.ugcHeadPictures?.size==1){
                    buildOneImg(bean.ugcHeadPictures?: mutableListOf())
                }else if (bean.ugcHeadPictures?.size==2){
                    buildTwoImg(bean.ugcHeadPictures?: mutableListOf())
                }else if (bean.ugcHeadPictures?.size==3){
                    buildThreeImg(bean.ugcHeadPictures?: mutableListOf())
                }else if (bean.ugcHeadPictures?.size==4){
                    buildFourImg(bean.ugcHeadPictures?: mutableListOf())
                }else if (bean.ugcHeadPictures?.size==5){
                    buildFiveImg(bean.ugcHeadPictures?: mutableListOf())
                }else{
                    buildSixImg(bean.ugcHeadPictures?: mutableListOf())
                }
            }

            mView.iv_avatar.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_ID]= bean.memberId
                RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
            }

            if (bean.isFollow=="1"){
                mView.findViewById<TextView>(R.id.tv_attention).text="已关注"
                mView.findViewById<TextView>(R.id.tv_attention).setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            }else{
                mView.findViewById<TextView>(R.id.tv_attention).text="关注"
                mView.findViewById<TextView>(R.id.tv_attention).setCompoundDrawablesWithIntrinsicBounds(R.mipmap.black_plus_icon,0,0,0)
            }
            mView.tv_attention.setOnClickListener {
                mInterface.doAttention(position,bean.memberId?:"")
            }
        }

        private fun buildOneImg(list:MutableList<UgcTjBean.TjUgcImage>){
            val view=View.inflate(mView.context,R.layout.item_one_img_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.getScreenWidth(view.context))
            ImageUtil.showCropImgWithString(view.findViewById(R.id.one_img),view.context,list[0].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.one_img),list[0].ugcId?:"")
            mView.ll_img_layout.addView(view)
        }

        private fun buildTwoImg(list:MutableList<UgcTjBean.TjUgcImage>){
            val view=View.inflate(mView.context,R.layout.item_two_image_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.getScreenWidth(view.context).div(2))
            ImageUtil.showCropImgWithString(view.findViewById(R.id.one_img),view.context,list[0].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.one_img),list[0].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.two_img),view.context,list[1].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.two_img),list[1].ugcId?:"")
            mView.ll_img_layout.addView(view)
        }

        private fun buildThreeImg(list:MutableList<UgcTjBean.TjUgcImage>){
            val view=View.inflate(mView.context,R.layout.item_more_image_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.getScreenWidth(view.context).div(3).times(2))
            ImageUtil.showCropImgWithString(view.findViewById(R.id.one_img),view.context,list[0].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.one_img),list[0].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.two_img),view.context,list[1].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.two_img),list[1].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.three_img),view.context,list[2].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.three_img),list[2].ugcId?:"")

            mView.ll_img_layout.addView(view)
        }

        private fun buildFourImg(list:MutableList<UgcTjBean.TjUgcImage>){
            val view=View.inflate(mView.context,R.layout.item_four_image_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.getScreenWidth(view.context))
            ImageUtil.showCropImgWithString(view.findViewById(R.id.one_img),view.context,list[0].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.one_img),list[0].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.two_img),view.context,list[1].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.two_img),list[1].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.three_img),view.context,list[2].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.three_img),list[2].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.four_img),view.context,list[3].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.four_img),list[3].ugcId?:"")

            mView.ll_img_layout.addView(view)
        }
        private fun buildFiveImg(list:MutableList<UgcTjBean.TjUgcImage>){
            val view=View.inflate(mView.context,R.layout.item_five_image_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.getScreenWidth(view.context).div(3).times(2))
            ImageUtil.showCropImgWithString(view.findViewById(R.id.one_img),view.context,list[0].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.one_img),list[0].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.two_img),view.context,list[1].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.two_img),list[1].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.three_img),view.context,list[2].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.three_img),list[2].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.four_img),view.context,list[3].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.four_img),list[3].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.five_img),view.context,list[4].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.five_img),list[4].ugcId?:"")

            view.findViewById<ImageView>(R.id.six_img).visibility=View.INVISIBLE
            mView.ll_img_layout.addView(view)
        }

        private fun buildSixImg(list:MutableList<UgcTjBean.TjUgcImage>){
            val view=View.inflate(mView.context,R.layout.item_five_image_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.getScreenWidth(view.context).div(3).times(2))
            ImageUtil.showCropImgWithString(view.findViewById(R.id.one_img),view.context,list[0].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.one_img),list[0].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.two_img),view.context,list[1].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.two_img),list[1].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.three_img),view.context,list[2].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.three_img),list[2].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.four_img),view.context,list[3].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.four_img),list[3].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.five_img),view.context,list[4].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.five_img),list[4].ugcId?:"")

            ImageUtil.showCropImgWithString(view.findViewById(R.id.six_img),view.context,list[5].headPicture?:"")
            jumpToUgcDetail(view.findViewById(R.id.six_img),list[5].ugcId?:"")

            mView.ll_img_layout.addView(view)
        }

        private fun jumpToUgcDetail(view:ImageView,ugcId:String){
            view.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.UGC_ID]= ugcId
                params[RunIntentKey.UGC_TYPE]= "1"
                RouterManager.startActivityWithParams(BundleUrl.UGC_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
            }

        }
    }
}
