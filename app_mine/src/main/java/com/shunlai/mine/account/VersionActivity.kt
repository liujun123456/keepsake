package com.shunlai.mine.account

import android.annotation.SuppressLint
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.MarketUtils
import com.shunlai.mine.R
import kotlinx.android.synthetic.main.activity_version_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*

/**
 * @author Liu
 * @Date   2021/5/28
 * @mobile 18711832023
 */
@SuppressLint("SetTextI18n")
class VersionActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_version_layout

    override fun getToolBarResID(): Int=R.layout.public_title_layout

    override fun afterView() {
        tv_version_name.text= "当前版本号：v${packageManager?.getPackageInfo(packageName,0)?.versionName}"
        initListener()
    }

    private fun initListener(){
        ll_back.setOnClickListener { finish() }
        do_app_update.setOnClickListener {
            MarketUtils.launchAppDetail(mContext,mContext.packageName,null)
        }
    }
}
