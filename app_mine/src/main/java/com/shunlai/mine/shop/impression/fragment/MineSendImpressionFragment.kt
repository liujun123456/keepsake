package com.shunlai.mine.shop.impression.fragment

import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.BaseFragment
import com.shunlai.common.utils.toast
import com.shunlai.mine.R
import com.shunlai.mine.shop.ShopViewModel
import com.shunlai.mine.shop.impression.ShopImpressionListener
import com.shunlai.mine.shop.impression.adapter.SendImpressionAdapter
import com.shunlai.mine.utils.SuspensionDecoration
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_send_impression_ayout.*

/**
 * @author Liu
 * @Date   2021/7/7
 * @mobile 18711832023
 */
class MineSendImpressionFragment : BaseFragment(), ShopImpressionListener {

    override fun createView(): Int=R.layout.fragment_send_impression_ayout

    override fun createTitle(): Int=0

    val mAdapter by lazy {
        SendImpressionAdapter(mContext, mutableListOf(),this)
    }

    val mViewModel by lazy {
        ViewModelProvider(this).get(ShopViewModel::class.java)
    }

    var isRefresh=true

    var currentPage=1


    override fun afterView() {
        initRv()
        initViewModel()
        mViewModel.querySendMessage(1)
    }

    private fun initRv(){
        rv_send.setAdapter(mAdapter)
        rv_send.setLayoutManager(LinearLayoutManager(mContext))
        rv_send.getRecyclerView().addItemDecoration(SuspensionDecoration(mContext,mAdapter.mData))
        rv_send.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                isRefresh=false
                mViewModel.querySendMessage(currentPage+1)
            }

            override fun refresh() {
                isRefresh=true
                mViewModel.querySendMessage(1)
            }
        })
    }

    private fun initViewModel(){
        mViewModel.sendMsgResp.observe(this, Observer {
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
        })
        mViewModel.deleteMsgResp.observe(this, Observer {
            if (it.isSuccess){
                mAdapter.mData.removeAt(currentPosition)
                rv_send.notifyDataSetChanged()
            }else{
                toast(it.errorMsg)
            }
        })
    }

    private var currentPosition=-1
    override fun onDelete(position: Int) {
        currentPosition=position
        AlertDialog.Builder(mContext).setTitle("提示").setMessage("确认删除这条留言嘛?")
            .setPositiveButton("确定"
            ) { dialog, _ ->
                mViewModel.deleteLeaveMessage(mAdapter.mData[position].id?:"")
                dialog.dismiss()
            }
            .setNegativeButton("取消"
            ) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    override fun onChoose(position: Int) {

    }
}
