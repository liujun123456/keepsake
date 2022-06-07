package com.shunlai.keepsake.splash

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.view.View
import com.shulai.third.ThirdManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.BaseApplication
import com.shunlai.common.bean.JumpEvent
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.im.ImManager
import com.shunlai.im.inter.ImLoginInterface
import com.shunlai.keepsake.R
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_splash.*
import org.greenrobot.eventbus.EventBus


class SplashActivity : BaseActivity(), SplashNoticeDialog.SplashNoticeListener {
    override fun getMainContentResId(): Int=R.layout.activity_splash

    override fun getToolBarResID(): Int=0

    private val mDialog by lazy {
        SplashNoticeDialog(mContext, R.style.custom_dialog,this)
    }

    override fun afterView() {
        dealWebAction()
        if (!isTaskRoot){
            finish()
            return
        }

        if (PreferenceUtil.getBoolean("isReadSplashNotice")==true){
            initListener()
            initThird()
        }else{
            mDialog.show()
        }
    }


    private fun initListener(){
        val uri = "android.resource://" + packageName + "/" + R.raw.launch_screen_token_video
        video_view.setVideoURI(Uri.parse(uri))
        video_view.setOnPreparedListener {
            iv_splash.visibility= View.GONE
            video_view.start()
        }
        video_view.setOnCompletionListener {
            jumpToNext()

        }
        video_view.setOnErrorListener { _, _, _ ->
            jumpToNext()
            return@setOnErrorListener true
        }
    }

    private fun jumpToNext(){
        val userId= PreferenceUtil.getString(Constant.USER_ID)
        val userSig= PreferenceUtil.getString(Constant.USER_SIG)
        if (TextUtils.isEmpty(PreferenceUtil.getString(Constant.USER_INFO))){
            RouterManager.startActivityWithParams(BundleUrl.LOGIN_ACTIVITY, this@SplashActivity)
            overridePendingTransition(R.anim.splash_close_enter,R.anim.splash_close_exit)
        }else{
            ImManager.login(userId?:"",userSig?:"",object : ImLoginInterface {
                override fun imLoginSuccess() {

                }

                override fun imLoginFailed(error: String) {

                }
            })
            RouterManager.startActivityWithParams(BundleUrl.HOME_ACTIVITY, this@SplashActivity)
            overridePendingTransition(R.anim.splash_close_enter,R.anim.splash_close_exit)
        }
        finish()
    }

    private fun dealWebAction(){
        val intent = intent
        val action = intent.action
        if (Intent.ACTION_VIEW==action){
            val uri = intent.data
            uri?.let {
                val eventBean=JumpEvent()
                eventBean.path= it.getQueryParameter("path")
                eventBean.params= it.getQueryParameter("params")
                EventBus.getDefault().postSticky(eventBean)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        video_view.stop()
    }

    override fun isAgree(bol: Boolean) {
       if (bol){
           PreferenceUtil.putBoolean("isReadSplashNotice",true)
           initListener()
           initThird()
       }else{
           finish()
       }
    }

    private val handler:Handler=Handler()

    private fun initThird(){
        handler.postDelayed({
            application?.let {
                ImManager.init(it)
                ThirdManager.init(it)
                BaseApplication.mInstance.initThirdPlugin()
            }
        },500)   //防止application还没有实例化
    }
}
