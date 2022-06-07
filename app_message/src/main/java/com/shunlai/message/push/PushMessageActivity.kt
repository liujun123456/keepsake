package com.shunlai.message.push

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.message.MessageViewModel
import com.shunlai.message.R
import com.shunlai.message.push.adapter.PushMessageAdapter
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.activity_comment_layout.*
import kotlinx.android.synthetic.main.activity_push_message_layout.*
import kotlinx.android.synthetic.main.activity_push_message_layout.app_bar_layout
import kotlinx.android.synthetic.main.public_title_layout.*
import kotlinx.android.synthetic.main.public_title_layout.ll_back
import kotlinx.android.synthetic.main.public_title_layout.tv_title_content
import kotlin.math.abs

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class PushMessageActivity : BaseActivity() {
    override fun getMainContentResId(): Int= R.layout.activity_push_message_layout

    override fun getToolBarResID(): Int= R.layout.public_title_layout

    override fun setTitleColor(): Int=R.color.message_white

    private val mAdapter by lazy {
        PushMessageAdapter(mContext, mutableListOf())
    }

    private var currentPage=1

    private var isRefresh=true

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MessageViewModel::class.java)
    }

    override fun afterView() {
        initTitle()
        initRv()
        initViewModel()
        mViewModel.queryPushMsg(currentPage)
    }

    private fun initRv(){
        rv_push_msg.setAdapter(mAdapter)
        rv_push_msg.setLayoutManager(LinearLayoutManager(mContext))
        rv_push_msg.setSRecyclerListener(object :SRecyclerListener{
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryPushMsg(currentPage+1)
            }

            override fun refresh() {

            }

        })
    }

    private fun initViewModel(){
        mViewModel.sysPushMsgResp.observe(this, Observer {
            if (it.isNullOrEmpty()){
                if (isRefresh){
                    mAdapter.mData.clear()
                    rv_push_msg.showEmpty()
                }else{
                    rv_push_msg.showNoMore()
                }
            }else{
                if (isRefresh){
                    currentPage=1
                    mAdapter.mData=it
                }else{
                    currentPage+=1
                    mAdapter.mData.addAll(it)
                }
                rv_push_msg.notifyDataSetChanged()
            }
        })
    }


    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) > ScreenUtils.dip2px(mContext,40f)){
                tv_title_content.text ="推送消息"
            }else{
                tv_title_content.text = ""
            }
        })
    }
}
