package com.shunlai.mine.account

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.shunlai.common.BaseActivity
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.account.adapter.TokenListAdapter
import com.shunlai.ui.srecyclerview.SRecyclerListener
import com.shunlai.ui.srecyclerview.ScreenUtils
import kotlinx.android.synthetic.main.activity_order_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*
import kotlin.math.abs

/**
 * @author Liu
 * @Date   2021/5/28
 * @mobile 18711832023
 */
class TokenListActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_order_layout

    override fun getToolBarResID(): Int=R.layout.public_title_layout

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    private val mAdapter by lazy {
        TokenListAdapter(mContext, mutableListOf())
    }

    private var currentPage=1

    private var isRefresh=true

    override fun afterView() {
        initTitle()
        initRv()
        initListener()
        initViewModel()
        mViewModel.queryTokenList(1)
    }

    private fun initTitle(){
        tv_title.text="明细"
        ll_back.setOnClickListener {
            finish()
        }
    }

    private fun initListener(){
        ll_back.setOnClickListener { finish() }
        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) > ScreenUtils.dip2px(mContext,40f)){
                tv_title_content.text="明细"
            }else{
                tv_title_content.text=""
            }
        })
    }

    private fun initViewModel(){
        mViewModel.tokenList.observe(this, Observer {
            if (isRefresh){
                if (it.isNullOrEmpty()){
                    rv_order.showEmpty()
                }else{
                    currentPage=1
                    mAdapter.mData.clear()
                    mAdapter.mData.addAll(it)
                    rv_order.notifyDataSetChanged()
                }
            }else{
                if (it.isNullOrEmpty()){
                    rv_order.showNoMore()
                }else{
                    currentPage+=1
                    mAdapter.mData.addAll(it)
                    rv_order.notifyDataSetChanged()
                }
            }
        })
    }

    private fun initRv(){
        rv_order.setAdapter(mAdapter)
        rv_order.setLayoutManager(LinearLayoutManager(mContext))
        rv_order.isCanRefresh=false
        rv_order.setSRecyclerListener(object :SRecyclerListener{
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryTokenList(currentPage+1)
            }

            override fun refresh() {

            }
        })
    }
}
