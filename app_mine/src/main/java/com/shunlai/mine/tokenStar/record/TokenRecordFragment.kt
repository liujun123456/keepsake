package com.shunlai.mine.tokenStar.record

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.BaseFragment
import com.shunlai.mine.R
import com.shunlai.mine.shop.ShopViewModel
import com.shunlai.mine.tokenStar.TokenStarViewModel
import com.shunlai.mine.tokenStar.record.adapter.RecordDecoration
import com.shunlai.mine.tokenStar.record.adapter.TestRecordAdapter
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_token_record_layout.*

/**
 * @author Liu
 * @Date   2021/8/26
 * @mobile 18711832023
 */
class TokenRecordFragment(var type:Int): BaseFragment()  {

    override fun createView(): Int=R.layout.fragment_token_record_layout

    override fun createTitle(): Int=0

    val mAdapter by lazy {
        TestRecordAdapter(activity!!, mutableListOf(),type)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(TokenStarViewModel::class.java)
    }

    private var isRefresh=true

    private var currentPage=1

    override fun afterView() {
        initRv()
        initViewModel()
        queryRecord(1)
    }

    private fun initRv(){
        rv_record.setAdapter(mAdapter)
        rv_record.setLayoutManager(LinearLayoutManager(mContext))
        rv_record.getRecyclerView().addItemDecoration(RecordDecoration(mContext,mAdapter.mData))
        rv_record.setSRecyclerListener(object : SRecyclerListener {
                override fun loadMore() {
                    isRefresh=false
                    queryRecord(currentPage+1)
                }

                override fun refresh() {

                }
            })
    }

    private fun initViewModel(){
        mViewModel.testRecordList.observe(this, Observer {
            if (isRefresh){
                currentPage=1
                mAdapter.mData.clear()
                mAdapter.mData.addAll(it)
                rv_record.notifyDataSetChanged()
            }else{
                if (it.isNullOrEmpty()){
                    rv_record.showNoMore()
                }else{
                    currentPage+=1
                    mAdapter.mData.addAll(it)
                    rv_record.notifyDataSetChanged()
                }
            }
        })
    }

    private fun queryRecord(page:Int){
        if (type==0){
            mViewModel.querySendTestRecord(page)
        }else{
            mViewModel.queryReceiverTestRecord(page)
        }
    }
}
