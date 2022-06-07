package com.shunlai.mine.bind

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.KeyEvent
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
import com.shunlai.mine.entity.resp.BindPhoneResp
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_bind_phone_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/4/25
 * @mobile 18711832023
 */
class BindPhoneActivity:BaseActivity(){
    override fun getMainContentResId(): Int =R.layout.activity_bind_phone_layout

    override fun getToolBarResID(): Int = R.layout.public_title_layout

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    private val member: LoginMember by lazy {
        GsonUtil.fromJson(PreferenceUtil.getString(Constant.USER_INFO)?:"", LoginMember::class.java)
    }

    override fun afterView() {
        initTitle()
        initListener()
        initViewModel()
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            RouterManager.startActivityWithParams(BundleUrl.HOME_ACTIVITY,this)
            finish()
        }
    }

    private fun initViewModel(){
        mViewModel.sendSmsResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                downTime()
                toast("验证码发送成功")
            }else{
                tv_get_verify.isEnabled=true
                toast(it.errorMsg)
            }
        })
        mViewModel.bindPhoneResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                if (it.member==null){
                    toast("绑定失败!")
                    return@Observer
                }
                toast("绑定成功")
                reSaveUserInfo(it)
                RouterManager.startActivityWithParams(BundleUrl.HOME_ACTIVITY,this)
                finish()
            }else{
                toast(it.errorMsg)
            }
        })
    }

    private fun initListener(){
        tv_get_verify.setOnClickListener {
            if (TextUtils.isEmpty(et_phone.text.toString())||et_phone.text.toString().length!=11){
                toast("请输入正确的手机号")
                return@setOnClickListener
            }
            mViewModel.sendSms(et_phone.text.toString())
            tv_get_verify.isEnabled=false
            showBaseLoading()
        }

        tv_commit.setOnClickListener {
            if (TextUtils.isEmpty(et_phone.text.toString())||et_phone.text.toString().length!=11){
                toast("请输入正确的手机号!")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(et_verify.text.toString())){
                toast("请输入验证码!")
                return@setOnClickListener
            }
            showBaseLoading()
            mViewModel.bindPhoneNum(et_verify.text.toString(),et_phone.text.toString())
        }
    }

    private fun reSaveUserInfo(resp: BindPhoneResp){
        PreferenceUtil.putString(Constant.TOKEN,resp.accessToken?:"")
        PreferenceUtil.putString(Constant.USER_ID,resp.member?.id?:"")
        member.id=resp.member?.id
        member.mobile=resp.member?.mobile
        PreferenceUtil.putString(Constant.USER_INFO, GsonUtil.toJson(member))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            RouterManager.startActivityWithParams(BundleUrl.HOME_ACTIVITY,this)
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    var job: Job?=null

    @SuppressLint("SetTextI18n")
    private fun downTime(){
        job?.cancel()
        job= GlobalScope.launch(Dispatchers.IO){
            flow{
                for (i in 0 ..60){
                    emit(60-i)
                    delay(1000)
                }
            }.collect {
                GlobalScope.launch(Dispatchers.Main){
                    if (it==0){
                        tv_get_verify.text="发送验证码"
                        tv_get_verify.isEnabled=true
                    }else{
                        tv_get_verify.text="剩余${it}秒"
                    }

                }
            }
        }
        job?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

}
