package com.shunlai.main.ht

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.main.HomeViewModel
import com.shunlai.main.R
import com.shunlai.main.ht.adapter.HuaTiAdapter
import com.shunlai.main.ht.adapter.HuaTiListAdapter
import com.shunlai.ui.MediaGridInset
import kotlinx.android.synthetic.main.activity_hua_ti_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*

/**
 * @author Liu
 * @Date   2021/5/7
 * @mobile 18711832023
 */
class HuaTiActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_hua_ti_layout

    override fun getToolBarResID(): Int= R.layout.public_title_layout

    private val mAdapter by lazy {
        HuaTiListAdapter(mContext,  mutableListOf(),mutableListOf())
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private fun initTitle(){
        ll_back.setOnClickListener { finish() }
        tv_title_content.text="话题"
        mViewModel.queryRecommendHt()
        mViewModel.queryAllHt()
    }

    override fun afterView() {
        initTitle()
        initRv()
        initViewModel()
    }

    private fun initRv(){
        rv_list_ht.layoutManager=LinearLayoutManager(mContext)
        rv_list_ht.addItemDecoration(MediaGridInset(1,ScreenUtils.dip2px(mContext,16f),false,true))
        rv_list_ht.adapter=mAdapter
    }

    private fun initViewModel(){
        mViewModel.recommendHtResp.observe(this, Observer {
            mAdapter.setHotAdapter(it?: mutableListOf())
        })
        mViewModel.allHtResp.observe(this, Observer {
            mAdapter.setAllAdapter(it?: mutableListOf())
        })
    }
}
