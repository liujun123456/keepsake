package com.shunlai.message.system

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.message.MessageViewModel
import com.shunlai.message.R
import com.shunlai.message.system.adapter.SystemMessageAdapter
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.activity_sys_message_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*
import kotlin.math.abs

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class SystemMessageActivity  : BaseActivity() {
    override fun getMainContentResId(): Int= R.layout.activity_sys_message_layout

    override fun getToolBarResID(): Int= R.layout.public_title_layout

    override fun setTitleColor(): Int=R.color.message_white

    private val mAdapter by lazy {
        SystemMessageAdapter(mContext, mutableListOf())
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MessageViewModel::class.java)
    }

    private var currentPage=1

    private var isRefresh=true

    override fun afterView() {
        initRv()
        initTitle()
        initViewModel()
        mViewModel.querySysMsg(currentPage)
    }

    private fun initViewModel(){
        mViewModel.sysPushMsgResp.observe(this, Observer {
            if (it.isNullOrEmpty()){
                if (isRefresh){
                    mAdapter.mData.clear()
                    rv_sys_msg.showEmpty()
                }else{
                    rv_sys_msg.showNoMore()
                }
            }else{
                if (isRefresh){
                    currentPage=1
                    mAdapter.mData=it
                }else{
                    currentPage+=1
                    mAdapter.mData.addAll(it)
                }
                rv_sys_msg.notifyDataSetChanged()
            }
        })
    }

    private fun initRv(){
        rv_sys_msg.setAdapter(mAdapter)
        rv_sys_msg.setLayoutManager(LinearLayoutManager(mContext))
        rv_sys_msg.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryPushMsg(currentPage+1)
            }

            override fun refresh() {

            }

        })
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) > ScreenUtils.dip2px(mContext,40f)){
                tv_title_content.text = "系统通知"
            }else{
                tv_title_content.text = ""
            }
        })
    }
}
