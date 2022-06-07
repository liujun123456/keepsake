package com.shunlai.message.collect

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.message.MessageViewModel
import com.shunlai.message.R
import com.shunlai.message.collect.adapter.CollectAdapter
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.activity_collect_layout.*
import kotlinx.android.synthetic.main.activity_collect_layout.app_bar_layout
import kotlinx.android.synthetic.main.public_title_layout.*
import kotlin.math.abs

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class CollectActivity : BaseActivity() {
    override fun getMainContentResId(): Int= R.layout.activity_collect_layout

    override fun getToolBarResID(): Int= R.layout.public_title_layout

    override fun setTitleColor(): Int=R.color.message_white

    private val mAdapter by lazy {
        CollectAdapter(mContext, mutableListOf())
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MessageViewModel::class.java)
    }

    private var currentPage=1

    private var isRefresh=true

    override fun afterView() {
        initTitle()
        initRv()
        initViewModel()
        initListener()
        mViewModel.queryCollect(currentPage)
    }

    private fun initRv(){
        rv_collect.setAdapter(mAdapter)
        rv_collect.setLayoutManager(LinearLayoutManager(mContext))
        rv_collect.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryCollect(currentPage+1)
            }

            override fun refresh() {

            }

        })
    }

    private fun initListener(){
        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset)> ScreenUtils.dip2px(mContext,40f)){
                tv_title_content.setText(R.string.collect_title)
            }else{
                tv_title_content.text = ""
            }
        })
    }

    private fun initViewModel(){
        mViewModel.collectResp.observe(this, Observer {
            if (it.isNullOrEmpty()){
                if (isRefresh){
                    mAdapter.mDates.clear()
                    rv_collect.showEmpty()
                }else{
                    rv_collect.showNoMore()
                }
            }else{
                if (isRefresh){
                    currentPage=1
                    mAdapter.mDates=it
                }else{
                    currentPage+=1
                    mAdapter.mDates.addAll(it)
                }
                rv_collect.notifyDataSetChanged()
            }
        })
    }


    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
    }
}
