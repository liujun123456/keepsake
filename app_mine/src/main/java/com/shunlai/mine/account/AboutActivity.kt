package com.shunlai.mine.account

import android.annotation.SuppressLint
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.mine.MineApiConfig
import com.shunlai.mine.R
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_about_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*

/**
 * @author Liu
 * @Date   2021/5/28
 * @mobile 18711832023
 */
@SuppressLint("SetTextI18n")
class AboutActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_about_layout

    override fun getToolBarResID(): Int=R.layout.public_title_layout

    override fun afterView() {
        initListener()
        tv_version_name.text="当前版本号：v${packageManager?.getPackageInfo(packageName,0)?.versionName}"
    }

    private fun initListener(){
        ll_back.setOnClickListener {
            finish()
        }
        tv_un_register.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.WEB_URL]="${MineApiConfig.ROOT_URL}/static/app/xwsapp/#/logout"
            RouterManager.startActivityWithParams(BundleUrl.LIGHT_APP_ACTIVITY,this,params)
        }
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
    }
}
