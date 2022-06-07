package com.shunlai.message.attention

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.common.utils.toast
import com.shunlai.message.MessageViewModel
import com.shunlai.message.R
import com.shunlai.message.attention.adapter.AttentionAdapter
import com.shunlai.message.entity.event.AttentionEvent
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.activity_attention.*
import kotlinx.android.synthetic.main.public_title_layout.*
import kotlin.math.abs

class AttentionActivity : BaseActivity(),AttentionAdapter.AttentionClickListener {
    override fun getMainContentResId(): Int=R.layout.activity_attention

    override fun getToolBarResID(): Int=R.layout.public_title_layout

    override fun setTitleColor(): Int=R.color.message_white

    private val mAdapter by lazy {
        AttentionAdapter(mContext, mutableListOf(),this)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MessageViewModel::class.java)
    }

    private var currentPage=1

    private var isRefresh=true

    private var currentPosition=0

    override fun afterView() {
        initTitle()
        initRv()
        initListener()
        initViewModel()
        mViewModel.queryAttention(currentPage)
    }

    private fun initViewModel(){
        mViewModel.attentionResp.observe(this, Observer {
            if (it.isNullOrEmpty()){
                if (isRefresh){
                    mAdapter.mDates.clear()
                    rv_attention.showEmpty()
                }else{
                    rv_attention.showNoMore()
                }
            }else{
                if (isRefresh){
                    currentPage=1
                    mAdapter.mDates=it
                }else{
                    currentPage+=1
                    mAdapter.mDates.addAll(it)
                }
                rv_attention.notifyDataSetChanged()
            }
        })
        mViewModel.doAttentionResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                mAdapter.mDates[currentPosition].isEachOther=it.isAttention
                rv_attention.notifyItemChanged(currentPosition)
            }else{
                toast(it.errorMsg)
            }
        })
    }

    private fun initListener(){
        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) >ScreenUtils.dip2px(mContext,40f)){
                tv_title_content.setText(R.string.attention_title)
            }else{
                tv_title_content.text = ""
            }
        })
    }

    private fun initRv(){
        rv_attention.setAdapter(mAdapter)
        rv_attention.setLayoutManager(LinearLayoutManager(mContext))
        rv_attention.setSRecyclerListener(object :SRecyclerListener{
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryAttention(currentPage+1)
            }

            override fun refresh() {

            }

        })
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
    }

    override fun onAttentionClick(event: AttentionEvent) {
        showBaseLoading()
        currentPosition=event.position
        mViewModel.doAttention(event.memberId)
    }

}
