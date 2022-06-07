package com.shunlai.mine.shop.impression.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.Switch
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.BaseFragment
import com.shunlai.common.utils.toast
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.ImpressionBean
import com.shunlai.mine.shop.ShopViewModel
import com.shunlai.mine.shop.impression.ShopImpressionListener
import com.shunlai.mine.shop.impression.adapter.ReceiverImpressionAdapter
import com.shunlai.mine.utils.SuspensionDecoration
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_receiver_impression_ayout.*

/**
 * @author Liu
 * @Date   2021/7/7
 * @mobile 18711832023
 */
class MineReceiverImpressionFragment:BaseFragment(), ShopImpressionListener {

    override fun createView(): Int= R.layout.fragment_receiver_impression_ayout

    override fun createTitle(): Int=0

    val mAdapter by lazy {
        ReceiverImpressionAdapter(mContext, mutableListOf(),this)
    }

    val mViewModel by lazy {
        ViewModelProvider(this).get(ShopViewModel::class.java)
    }

    var isRefresh=true

    var currentPage=1

    override fun afterView() {
        initRv()
        initListener()
        initViewModel()
        mViewModel.queryReceiveMessage(1)
        mViewModel.checkDanMu()
    }

    private fun initRv(){
        rv_receiver.setAdapter(mAdapter)
        rv_receiver.setLayoutManager(LinearLayoutManager(mContext))
        rv_receiver.getRecyclerView().addItemDecoration(SuspensionDecoration(mContext,mAdapter.mData))
        rv_receiver.setSRecyclerListener(object :SRecyclerListener{
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryReceiveMessage(currentPage+1)
            }

            override fun refresh() {
                isRefresh=true
                mViewModel.queryReceiveMessage(1)
            }
        })
    }

    private fun initListener(){
        sb_default.setOnCheckedChangeListener { view, isChecked ->
            switchState(isChecked)
            mViewModel.openDanMu(if (isChecked) 1 else 0)
        }
    }

    private fun switchState(isChecked:Boolean){
        if (isChecked){
            ll_content.visibility= View.VISIBLE
        }else{
            ll_content.visibility= View.GONE
        }
    }

    private fun initViewModel(){
        mViewModel.receiveMsgResp.observe(this, Observer {
            if (isRefresh){
                currentPage=1
                mAdapter.mData.clear()
                mAdapter.mData.addAll(it)
                rv_receiver.notifyDataSetChanged()
            }else{
                if (it.isNullOrEmpty()){
                    rv_receiver.showNoMore()
                }else{
                    currentPage+=1
                    mAdapter.mData.addAll(it)
                    rv_receiver.notifyDataSetChanged()
                }
            }
        })
        mViewModel.blockedMsgResp.observe(this, Observer {
            if (it.isSuccess){
                mAdapter.mData.removeAt(currentPosition)
                rv_receiver.notifyDataSetChanged()
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.checkDanMuResp.observe(this, Observer {
            sb_default.isChecked = it==1
        })
    }

    private var currentPosition=-1
    override fun onDelete(position:Int) {
        currentPosition=position
        AlertDialog.Builder(mContext).setTitle("提示").setMessage("确认删除这条留言嘛?")
            .setPositiveButton("取消"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("确定"
            ) { dialog, _ ->
                mViewModel.blockedLeaveMessage(mAdapter.mData[position].id?:"")
                dialog.dismiss()
            }.create().show()

    }

    override fun onChoose(position:Int) {
        currentPosition=position
        val value=mAdapter.mData[position].chosedFlag
        mAdapter.mData[currentPosition].chosedFlag=if (value==0) 1 else 0
        rv_receiver.notifyDataSetChanged()
        mViewModel.chooseLeaveMessage(mAdapter.mData[position].id!!,value?:0)
    }
}
