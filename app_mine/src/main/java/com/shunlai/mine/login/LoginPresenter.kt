package com.shunlai.mine.login

import android.content.Context
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.utils.toast
import com.shunlai.mine.entity.req.LoginReq
import com.shunlai.mine.MineViewModel
import com.shunlai.share.WeChatUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * @author Liu
 * @Date   2021/4/26
 * @mobile 18711832023
 */
class LoginPresenter(var mContext:Context,var mView:LoginView):
    com.shunlai.share.LoginResultListener {
    private val mViewModel by lazy {
        ViewModelProvider(mContext as FragmentActivity).get(MineViewModel::class.java)
    }

    init {
        initViewModel()
    }


    private fun initViewModel(){
        mViewModel.sendSmsResp.observe(mContext as FragmentActivity, Observer {
            mView.hideLoading()
            if (!it.isSuccess){
                toast(it.errorMsg)
            }
            mView.sendSmsResult(it.isSuccess)
        })
        mViewModel.loginResp.observe(mContext as FragmentActivity, Observer {
            mView.hideLoading()
            if (!it.isSuccess){
                mView.loginFailed(it.errorMsg)
            }else{
                mView.loginSuccess(it)
            }
        })
    }

    fun getVerifyCode(phone:String){
        if (TextUtils.isEmpty(phone)||phone.length!=11){
            toast("请输入正确的手机号!")
            mView.sendSmsResult(false)
            return
        }
        mView.showLoading("获取验证码")
        mViewModel.sendSms(phone)
    }

    fun doLogin(phone:String,code:String){
        if (TextUtils.isEmpty(phone)||phone.length!=11){
            toast("请输入正确的手机号!")
            return
        }

        if (TextUtils.isEmpty(code)){
            toast("请输入验证码!")
        }
        mView.showLoading("登录中")
        mViewModel.loginWithVerify(LoginReq().apply {
            buildParams(phone,code)
        })
    }

    fun doWeChatLogin(){
        WeChatUtil.getInstance().doLogin(this)
    }

    var job:Job?=null

    fun downTime(){
        job?.cancel()
        job= GlobalScope.launch(Dispatchers.IO){
            flow{
                for (i in 0 ..60){
                    emit(60-i)
                    delay(1000)
                }
            }.collect {
                GlobalScope.launch(Dispatchers.Main){
                    mView.updateDownTime(it,it==0)
                }
            }
        }
        job?.start()
    }

    override fun onSuccess(code:String) {
        mView.showLoading("登录中")
        mViewModel.loginWithWeChat(code)
    }

    override fun onFail(reason: String) {
        toast(reason)
    }
}
