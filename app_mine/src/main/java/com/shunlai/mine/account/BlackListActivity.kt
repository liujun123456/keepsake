package com.shunlai.mine.account

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.account.adapter.BlackListAdapter
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.activity_black_list_layout.*
import kotlin.math.abs

/**
 * @author Liu
 * @Date   2021/8/25
 * @mobile 18711832023
 */
class BlackListActivity:BaseActivity(), BlackListAdapter.BlackListener {

    private val mAdapter by lazy {
        BlackListAdapter(mContext, mutableListOf(),this)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    private var currentPage=1

    private var isRefresh=true

    override fun getMainContentResId(): Int= R.layout.activity_black_list_layout

    override fun getToolBarResID(): Int=0

    override fun afterView() {
        initTitle()
        initRv()
        initViewModel()
        mViewModel.queryBlackList(currentPage)
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }

        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) > ScreenUtils.dip2px(mContext,40f)){
                tv_title_content.text = "黑名单"
            }else{
                tv_title_content.text = ""
            }
        })
    }

    private fun initRv(){
        rv_black_list.setLayoutManager(LinearLayoutManager(mContext))
        rv_black_list.setAdapter(mAdapter)
        rv_black_list.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryBlackList(currentPage+1)
            }

            override fun refresh() {

            }

        })
    }

    private fun initViewModel(){
        mViewModel.blackListResp.observe(this, Observer {
            if (it.isNullOrEmpty()){
                if (isRefresh){
                    mAdapter.mData.clear()
                    rv_black_list.showEmpty()
                }else{
                    rv_black_list.showNoMore()
                }
            }else{
                if (isRefresh){
                    currentPage=1
                    mAdapter.mData=it
                }else{
                    currentPage+=1
                    mAdapter.mData.addAll(it)
                }
                rv_black_list.notifyDataSetChanged()
            }
        })
    }


    override fun cancelBlack(memberId: String, position: Int) {
        mViewModel.cancelBlack(memberId)
        mAdapter.mData.removeAt(position)
        if (mAdapter.mData.isEmpty()){
            rv_black_list.showEmpty()
        }else{
            rv_black_list.notifyDataSetChanged()
        }
    }
}
