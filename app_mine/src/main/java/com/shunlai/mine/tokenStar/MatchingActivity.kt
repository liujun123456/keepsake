package com.shunlai.mine.tokenStar

import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.os.Message
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.StatusBarUtil
import com.shunlai.common.utils.toast
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_star_match_layout.*
import java.lang.Exception

/**
 * @author Liu
 * @Date   2021/8/30
 * @mobile 18711832023
 */
class MatchingActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_star_match_layout

    override fun getToolBarResID(): Int=0

    private val mViewModel by lazy {
        ViewModelProvider(this).get(TokenStarViewModel::class.java)
    }

    private var mMemberInfo:MemberInfo?=null

    private val mHandler=object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what==2000){
                mViewModel.doStarMatch(mMemberInfo?.id?.toString()?:"")
            }
        }
    }


    override fun afterView() {
        try {
            mMemberInfo= GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)
        }catch (e: Exception){

        }
        StatusBarUtil.showLightStatusBarIcon(this)
        (v_match.background as AnimationDrawable).start()
        mHandler.sendEmptyMessageDelayed(2000,2000)
        initViewModel()
    }

    private fun initViewModel(){
        mViewModel.matchResp.observe(this, Observer {
            if (it.isSuccess){
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_INFO]=GsonUtil.toJson(mMemberInfo!!)
                params[RunIntentKey.MATCH_RESULT]=GsonUtil.toJson(it)
                RouterManager.startActivityWithParams(BundleUrl.OTHER_TOKEN_STAR_CENTER,this,params)
                finish()
            }else{
                toast(it.errorMsg)
            }
        })
    }
}
