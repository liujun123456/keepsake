package com.shunlai.mine.login

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import com.getui.gs.sdk.GsManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.*
import com.shunlai.im.ImManager
import com.shunlai.im.inter.ImLoginInterface
import com.shunlai.mine.R
import com.shunlai.mine.dialog.BindPhoneDialog
import com.shunlai.mine.entity.bean.LoginAnimPoint
import com.shunlai.mine.entity.resp.LoginResp
import com.shunlai.mine.utils.AnimEvaluator
import com.shunlai.net.util.GsonUtil
import com.shunlai.net.util.NetWorkUtil
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_login_layout.*
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/4/25
 * @mobile 18711832023
 */
class LoginActivity:BaseActivity(),LoginView {
    override fun getMainContentResId(): Int= R.layout.activity_login_layout

    override fun getToolBarResID(): Int =0

    private val mPresenter by lazy {
        LoginPresenter(mContext,this)
    }

    private val bindDialog by lazy {
        BindPhoneDialog(mContext,R.style.custom_dialog)
    }

    private val state by lazy {
        intent.getIntExtra("state",-1)
    }

    private val stateMsg by lazy {
        intent.getStringExtra("stateMsg")
    }

    var isAgree=false

    override fun afterView() {
        initListener()
        initAnimBg()
        checkParams()
        if (state==402){
            stateMsg?.let {
                toast(stateMsg)
            }
        }
    }

    private fun initListener(){
        et_phone.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                checkParams()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        et_verify.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                checkParams()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        tv_user_agreement.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.WEB_URL]="https://chengbai-tech.com/static/app/document/agreement.html"
            RouterManager.startActivityWithParams(BundleUrl.LIGHT_APP_ACTIVITY,this,params)
        }
        tv_privacy_agreement.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.WEB_URL]="https://chengbai-tech.com/static/app/document/privacy-policy.html"
            RouterManager.startActivityWithParams(BundleUrl.LIGHT_APP_ACTIVITY,this,params)
        }
        tv_do_agree.setOnClickListener {
            if (isAgree){
                isAgree=false
                tv_do_agree.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.login_un_agree,0,0,0)
            }else{
                isAgree=true
                tv_do_agree.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.login_agree_icon,0,0,0)
            }
        }
    }

    private fun checkParams(){
        if (TextUtils.isEmpty(et_phone.text.toString())||TextUtils.isEmpty(et_verify.text.toString())){
            tv_login.setBackgroundResource(R.drawable.gray_24_radius_bg)
        }else{
            tv_login.setBackgroundResource(R.drawable.black_radius_24)
        }

    }

    fun getVerifyCode(view:View){
        if (!NetWorkUtil.isNetWorkActive(mContext)){
            toast("无网络，需要授权网络权限哦")
            return
        }
        tv_get_verify.isEnabled=false
        mPresenter.getVerifyCode(et_phone.text.toString())
    }

    fun doLogin(view:View){
        if (!NetWorkUtil.isNetWorkActive(mContext)){
            toast("无网络，需要授权网络权限哦")
            return
        }
        if (!isAgree){
            toast("请先勾选同意《用户协议》和《隐私权政策》")
            return
        }
        mPresenter.doLogin(et_phone.text.toString(),et_verify.text.toString())
    }

    fun doWeChatLogin(view:View){
        if (!NetWorkUtil.isNetWorkActive(mContext)){
            toast("无网络，需要授权网络权限哦")
            return
        }
        if (!isAgree){
            toast("请先勾选同意《用户协议》和《隐私权政策》")
            return
        }
        mPresenter.doWeChatLogin()

        val params= JSONObject()
        params.put("is_direct_success",true)
        params.put("page_name",screenUrl)
        GsManager.getInstance().onEvent("LoginByWechat", params)
    }

    override fun showLoading(value: String) {
        showBaseLoading()
    }

    override fun hideLoading() {
        hideBaseLoading()
    }

    override fun sendSmsResult(boolean: Boolean) {
        if (!boolean){
            tv_get_verify.isEnabled=true
        }else{
            mPresenter.downTime()
        }
        sensorsTrackSendCodeResult(boolean)
    }

    @SuppressLint("SetTextI18n")
    override fun updateDownTime(second: Int, isEnd: Boolean) {
        if (!isEnd){
            tv_get_verify.text="剩余${second}秒"
        }else{
            tv_get_verify.text="发送验证码"
            tv_get_verify.isEnabled=true
        }
    }

    override fun loginSuccess(resp: LoginResp) {
        if (resp.member==null){
            toast("获取用户信息失败!")
            return
        }
        PreferenceUtil.putString(Constant.TOKEN,resp.accessToken?:"")
        PreferenceUtil.putString(Constant.USER_ID,resp.member?.id?:"")
        PreferenceUtil.putString(Constant.USER_SIG,resp.userSig?:"")
        PreferenceUtil.putString(Constant.USER_INFO,GsonUtil.toJson(resp.member!!))
        ImManager.login(resp.member?.id?:"",resp.userSig?:"",object : ImLoginInterface {
            override fun imLoginSuccess() {

            }

            override fun imLoginFailed(error: String) {

            }
        })
        mPresenter.job?.cancel()
        if (resp.hasMobile==1){
            RouterManager.startActivityWithParams(BundleUrl.HOME_ACTIVITY,this)
            finish()
        }else{
            bindDialog.show()
        }
        sensorsTrackLoginResult("登陆成功",true)
    }

    override fun loginFailed(msg: String) {
        toast(msg)
        sensorsTrackLoginResult(msg,false)
    }

    private fun initAnimBg(){
        val shoesFirstPoint=LoginAnimPoint(ScreenUtils.dip2px(mContext,-225f),ScreenUtils.dip2px(mContext,208f))
        val shoesSecondPoint=LoginAnimPoint(ScreenUtils.dip2px(mContext,-56f),ScreenUtils.dip2px(mContext,97f))
        updateShoes(shoesFirstPoint)

        val packageFirstPoint=LoginAnimPoint(ScreenUtils.dip2px(mContext,231f),ScreenUtils.dip2px(mContext,91f))
        val packageSecondPoint=LoginAnimPoint(ScreenUtils.dip2px(mContext,61f),ScreenUtils.dip2px(mContext,0f))
        updatePackage(packageFirstPoint)

        beginAnim(shoesFirstPoint,shoesSecondPoint,packageFirstPoint,packageSecondPoint)
    }

    private var shoesAnim:ValueAnimator?=null
    private var packageAnim:ValueAnimator?=null

    private fun beginAnim(shoesFirstPoint:LoginAnimPoint,shoesSecondPoint:LoginAnimPoint,
                          packageFirstPoint:LoginAnimPoint,packageSecondPoint:LoginAnimPoint){
        shoesAnim=ValueAnimator.ofObject(AnimEvaluator(),shoesFirstPoint,shoesSecondPoint)
        shoesAnim?.duration = 5000
        shoesAnim?.addUpdateListener {
            if (it.animatedValue is LoginAnimPoint){
                updateShoes(it.animatedValue as LoginAnimPoint)
            }
        }
        shoesAnim?.start()
        packageAnim=ValueAnimator.ofObject(AnimEvaluator(),packageFirstPoint,packageSecondPoint)
        packageAnim?.duration = 5000
        packageAnim?.addUpdateListener {
            if (it.animatedValue is LoginAnimPoint){
                updatePackage(it.animatedValue as LoginAnimPoint)
            }
        }
        packageAnim?.start()
    }

    private fun updatePackage(point:LoginAnimPoint){
        iv_package.translationX=point.firstMargin.toFloat()
        iv_package.translationY=point.secondMargin.toFloat()
    }
    private fun updateShoes(point:LoginAnimPoint){
        iv_shoes.translationX=point.firstMargin.toFloat()
        iv_shoes.translationY=point.secondMargin.toFloat()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            finishAll()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 神策--登陆结果
     */
    private fun sensorsTrackLoginResult(msg:String,bol:Boolean){
        val params= JSONObject()
        params.put("is_success_nextpage",bol)
        params.put("page_name",screenUrl)
        params.put("is_success_msg",msg)
        GsManager.getInstance().onEvent("SignInClick", params)
    }

    /**
     * 神策--验证码结果
     */
    private fun sensorsTrackSendCodeResult(bol: Boolean){
        val params= JSONObject()
        params.put("is_getcode",bol)
        params.put("page_name",screenUrl)
        GsManager.getInstance().onEvent("GetCodeClick", params)
    }
}
