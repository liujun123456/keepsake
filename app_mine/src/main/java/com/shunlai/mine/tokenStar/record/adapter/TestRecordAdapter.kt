package com.shunlai.mine.tokenStar.record.adapter

import android.content.Context
import android.text.Layout
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.*
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.LoginMember
import com.shunlai.mine.entity.bean.TestRecordBean
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import kotlinx.android.synthetic.main.item_test_record_layout.view.*
import java.lang.Exception

/**
 * @author Liu
 * @Date   2021/8/26
 * @mobile 18711832023
 */
class TestRecordAdapter(var mContext:Context,var mData:MutableList<TestRecordBean>,var type:Int): SRecyclerAdapter(mContext)  {
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view= View.inflate(mContext,R.layout.item_test_record_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.dip2px(mContext,88f))
        return TestRecordViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as TestRecordViewHolder).setData(mData[position])
    }

    override fun getCount(): Int=mData.size

    inner class TestRecordViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:TestRecordBean){
            val memberValue=PreferenceUtil.getString(Constant.USER_INFO)
            var memberInfo:LoginMember?=null
            try {
                memberInfo= GsonUtil.fromJson(memberValue?:"",LoginMember::class.java)
            }catch (e:Exception){
            }
            ImageUtil.showCircleImgWithString(mView.iv_mine_avatar,mView.context,memberInfo?.avatar)
            mView.tv_mine_name.text=memberInfo?.nickName
            mView.tv_mine_name?.post {
                mView.tv_mine_name.isSelected=true
            }
            ImageUtil.showCircleImgWithString(mView.iv_other_avatar,mView.context,bean.logo)
            mView.tv_other_name.text=bean.nickName
            mView.tv_other_name?.post {
                mView.tv_other_name.isSelected=true
            }
            mView.iv_other_avatar.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_ID]= if (type==0) bean.anotherMemberId else bean.memberId
                RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
            }

            mView.tv_hz.text="${bean.score} Hz"

            if (type==0){
                mView.ll_mine_info.alpha=0.65f
                mView.ll_other_info.alpha=1f
                mView.iv_mine_ring.visibility=View.INVISIBLE
                mView.iv_other_ring.visibility=View.VISIBLE
                mView.iv_compare_logo.setImageResource(R.mipmap.icon_test_right)
            }else{
                mView.ll_mine_info.alpha=1f
                mView.ll_other_info.alpha=0.65f
                mView.iv_mine_ring.visibility=View.VISIBLE
                mView.iv_other_ring.visibility=View.INVISIBLE
                mView.iv_compare_logo.setImageResource(R.mipmap.icon_test_left)
            }
        }
    }
}
