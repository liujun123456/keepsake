package com.shunlai.mine.shop.feed

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.BaseFragment
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.FeedRecord
import com.shunlai.mine.entity.bean.FeedRecordEvent
import com.shunlai.mine.shop.ShopViewModel
import com.shunlai.mine.shop.feed.adapter.FeedRecordAdapter
import com.shunlai.mine.utils.FeedDecoration
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_send_impression_ayout.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/7/22
 * @mobile 18711832023
 */
class FeedRecordFragment(var type:Int):BaseFragment() {

    override fun createView(): Int=R.layout.fragment_send_impression_ayout

    override fun createTitle(): Int=0

    private val mViewModel by lazy {
        ViewModelProvider(this).get(ShopViewModel::class.java)
    }

    private val mAdapter by lazy {
        FeedRecordAdapter(type,mContext, mutableListOf())
    }

    private var isRefresh=true

    private var currentPage=1

    override fun afterView() {
        initRv()
        initViewModel()
        queryRecord(1)
    }

    private fun initRv(){
        rv_send.setAdapter(mAdapter)
        rv_send.setLayoutManager(LinearLayoutManager(mContext))
        rv_send.getRecyclerView().addItemDecoration(FeedDecoration(mContext,mAdapter.mData))
        rv_send.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                isRefresh=false
                queryRecord(currentPage+1)
            }

            override fun refresh() {

            }
        })
    }

    private fun initViewModel(){
        mViewModel.feedSend.observe(this, Observer {
            dealData(it)
        })
        mViewModel.feedReceiver.observe(this, Observer {
            EventBus.getDefault().post(FeedRecordEvent((it.total_records?:0L).toString()))
            dealData(it?.data?: mutableListOf())
        })
    }

    private fun dealData(it:MutableList<FeedRecord>){
        if (isRefresh){
            currentPage=1
            mAdapter.mData.clear()
            mAdapter.mData.addAll(it)
            rv_send.notifyDataSetChanged()
        }else{
            if (it.isNullOrEmpty()){
                rv_send.showNoMore()
            }else{
                currentPage+=1
                mAdapter.mData.addAll(it)
                rv_send.notifyDataSetChanged()
            }
        }
    }

    private fun queryRecord(page:Int){
        if (type==0){
            mViewModel.queryReceiverFeed(page)
        }else{
            mViewModel.querySendFeed(page)
        }
    }

}
