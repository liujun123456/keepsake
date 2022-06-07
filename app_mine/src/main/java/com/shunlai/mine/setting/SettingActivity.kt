package com.shunlai.mine.setting

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.*
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.mine.entity.bean.SkinBean
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_setting_layout.*

/**
 * @author Liu
 * @Date   2021/5/19
 * @mobile 18711832023
 */
class SettingActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_setting_layout

    override fun getToolBarResID(): Int=0

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    private val memberInfo by lazy {
        GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)
    }

    private var skin:SkinBean?=null

    override fun afterView() {
        skin=memberInfo.themeSkin
        initTitle()
        initListener()
        initViewModel()

    }

    @SuppressLint("SetTextI18n")
    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
        tv_clean_data.text="${FileUtil.getCacheSize(mContext)}M"
    }

    @SuppressLint("SetTextI18n")
    private fun initListener(){
        tv_version_name.text=packageManager?.getPackageInfo(packageName,0)?.versionName
        rl_go_account.setOnClickListener {
            RouterManager.startActivityWithParams(BundleUrl.ACCOUNT_ACTIVITY,this)
        }
        rl_version.setOnClickListener {
            RouterManager.startActivityWithParams(BundleUrl.VERSION_ACTIVITY,this)
        }
        rl_clean.setOnClickListener {

            FileUtil.cleanCache(mContext) {
                toast("清除成功!")
                tv_clean_data.text="${FileUtil.getCacheSize(mContext)}M"
            }
        }
        rl_about.setOnClickListener {
            RouterManager.startActivityWithParams(BundleUrl.ABOUT_ACTIVITY,this)
        }
        tv_login_out.setOnClickListener {
            showBaseLoading()
            mViewModel.loginout()
        }
        rl_go_black.setOnClickListener {
            RouterManager.startActivityWithParams(BundleUrl.BLACK_LIST_ACTIVITY,this)
        }
    }

    private fun initViewModel(){
        mViewModel.loginoutResp.observe(this, Observer {
            hideBaseLoading()
            PreferenceUtil.removeValueWithKey(Constant.USER_ID)
            PreferenceUtil.removeValueWithKey(Constant.TOKEN)
            PreferenceUtil.removeValueWithKey(Constant.USER_INFO)
            PreferenceUtil.removeValueWithKey(Constant.USER_SIG)
            finishAll()
            RouterManager.startActivityWithParams(BundleUrl.LOGIN_ACTIVITY,this)
        })
    }

}
