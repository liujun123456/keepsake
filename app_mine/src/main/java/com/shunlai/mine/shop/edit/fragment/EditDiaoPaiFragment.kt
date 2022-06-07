package com.shunlai.mine.shop.edit.fragment

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.BaseFragment
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.OwnerUgcBean
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.fragment_diao_pai_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/7/14
 * @mobile 18711832023
 */
class EditDiaoPaiFragment(var signBoard:OwnerUgcBean?=null):BaseFragment() {

    override fun createView(): Int=R.layout.fragment_diao_pai_layout

    override fun createTitle(): Int=0

    override fun afterView() {
        ImageUtil.showRoundImgWithResAndRadius(iv_d_p_un_choose,mContext, R.mipmap.empty_diao_pai,12f)
        ImageUtil.showRoundImgWithResAndRadius(iv_d_p_choose,mContext, R.mipmap.diao_pai_icon,12f)
        initListener()
    }

    private fun initListener(){
        rl_un_choose.setOnClickListener {
            doChoose(false)
        }
        rl_choose.setOnClickListener {
            doChoose(true)
        }
        ll_set_d_P.setOnClickListener {
            RouterManager.startActivityWithParams(BundleUrl.PERSONAL_SHOP_EDIT_UGC_ACTIVITY,mContext as FragmentActivity,
                mutableMapOf())
        }
        doChoose(signBoard!=null)
    }

    private fun doChoose(bol:Boolean){
        if (bol){
            v_un_choose.setBackgroundResource(R.drawable.shop_un_select_bg)
            iv_un_choose_label.visibility=View.GONE
            v_choose.visibility= View.VISIBLE
            iv_choose_label.visibility=View.VISIBLE
            ll_set_d_P.visibility=View.VISIBLE
        }else{
            v_un_choose.setBackgroundResource(R.drawable.shop_select_bg)
            iv_un_choose_label.visibility=View.VISIBLE
            v_choose.visibility= View.GONE
            iv_choose_label.visibility=View.GONE
            ll_set_d_P.visibility=View.GONE
            EventBus.getDefault().post(OwnerUgcBean())
        }
    }
}
