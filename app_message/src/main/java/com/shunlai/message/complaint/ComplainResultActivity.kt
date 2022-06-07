package com.shunlai.message.complaint

import com.shunlai.common.BaseActivity
import com.shunlai.message.R
import kotlinx.android.synthetic.main.activity_complain_result_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class ComplainResultActivity: BaseActivity() {
    override fun getMainContentResId(): Int= R.layout.activity_complain_result_layout

    override fun getToolBarResID(): Int = R.layout.public_title_layout

    override fun setTitleColor(): Int=R.color.message_white

    override fun afterView() {
        initTitle()
        initListener()
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
    }

    private fun initListener(){
        tv_complete.setOnClickListener { finish() }
    }
}
