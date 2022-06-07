package com.shunlai.mine.account

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.toast
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.LoginMember
import com.shunlai.share.WeChatUtil
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_account_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*

/**
 * @author Liu
 * @Date   2021/4/25
 * @mobile 18711832023
 */
class AccountActivity:BaseActivity(), com.shunlai.share.LoginResultListener {
    override fun getMainContentResId(): Int =R.layout.activity_account_layout

    override fun getToolBarResID(): Int= R.layout.public_title_layout

    var member: LoginMember?=null

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    override fun afterView() {
        initTitle()
        initListener()
        initViewModel()
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
    }

    private fun initListener(){
        tv_update_phone.setOnClickListener {
            if (tv_update_phone.text=="绑定"){
                RouterManager.startActivityWithParams(BundleUrl.BIND_PHONE_ACTIVITY,mContext as FragmentActivity)
            }else{
                val params= mutableMapOf<String,Any?>()
                params["phoneNum"]=member?.mobile
                RouterManager.startActivityWithParams(BundleUrl.UPDATE_BIND_PHONE_ACTIVITY,mContext as FragmentActivity,params)
            }
        }
        tv_update_we_chat.setOnClickListener {
            if (tv_update_we_chat.text=="绑定"){
                WeChatUtil.getInstance().doLogin(this)
            }else{
                showBaseLoading()
                mViewModel.unBindWeChat(member?.appOpenId?:"")
            }
        }
    }

    private fun initViewModel(){
        mViewModel.unBindResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                toast("解绑成功")
                member?.appOpenId=""
                PreferenceUtil.putString(Constant.USER_INFO,GsonUtil.toJson(member!!))
                buildState()
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.bindWeChatResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                toast("绑定成功")
                member?.appOpenId=it.appOpenId
                member?.avatar=it.avatar
                member?.appOpenId=it.appOpenId
                PreferenceUtil.putString(Constant.USER_INFO,GsonUtil.toJson(member!!))
                buildState()
            }else{
                toast(it.errorMsg)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        member=GsonUtil.fromJson(PreferenceUtil.getString(Constant.USER_INFO)?:"",LoginMember::class.java)
        buildState()
    }

    @SuppressLint("SetTextI18n")
    private fun buildState(){
        if (TextUtils.isEmpty(member?.mobile)){
            tv_phone_value.text="未绑定"
            tv_update_phone.text="绑定"
        }else{
            tv_phone_value.text="已绑定：${member?.mobile}"
            tv_update_phone.text="更换绑定"
        }

        if (TextUtils.isEmpty(member?.appOpenId)){
            tv_we_chat_value.text="未绑定"
            tv_update_we_chat.text="绑定"
        }else{
            tv_we_chat_value.text="已绑定：${member?.nickName}"
            tv_update_we_chat.text="解除绑定"
        }
    }

    override fun onSuccess(code: String) {
        showBaseLoading()
        mViewModel.bindWeChat(code)
    }

    override fun onFail(reason: String) {
       toast(reason)
    }
}
