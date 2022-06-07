package com.shunlai.mine.account

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.mine.MineApiConfig
import com.shunlai.mine.MineHttpManager
import com.shunlai.mine.R
import com.shunlai.mine.fragment.MineFragment
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable

/**
 * @author Liu
 * @Date   2021/5/19
 * @mobile 18711832023
 */
class UserActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_user_layout

    override fun getToolBarResID(): Int=0

    private val memberId by lazy {
        intent.getStringExtra(RunIntentKey.MEMBER_ID)
    }

    override fun afterView() {
        val mineFragment= MineFragment()
        val bundle=Bundle()
        bundle.putInt(RunIntentKey.USER_TYPE,if (memberId==PreferenceUtil.getString(Constant.USER_ID)) 0 else 1)
        bundle.putString(RunIntentKey.MEMBER_ID,memberId)
        bundle.putBoolean("isFromActivity",true)
        mineFragment.arguments=bundle
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,mineFragment).commitAllowingStateLoss()
        checkUserState()
    }

    private fun checkUserState(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId?:""
        MineHttpManager.getByParams(this,MineApiConfig.CHECK_BLOCK_USER,params).subscribe(object :CoreHttpSubscriber<String>{
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t: String?) {
                t?.let {
                    if (t!="1"){
                        AlertDialog.Builder(mContext).setTitle("提示").setMessage(if (it=="3")"您已被对方拉黑" else "对方已被您拉黑")
                            .setPositiveButton("确认") { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }.setCancelable(false).show()
                    }
                }
            }
        })
    }
}
