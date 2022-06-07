package com.shunlai.ugc.search.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.utils.toast
import com.shunlai.ugc.R
import com.shunlai.ugc.UgcViewModel
import com.shunlai.ugc.search.AttentionConfirmDialog
import com.shunlai.ugc.search.UserMemberAdapter
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_search_layout.*

/**
 * @author Liu
 * @Date   2021/8/24
 * @mobile 18711832023
 */
class UserSearchFragment:SearchFragment(), UserMemberAdapter.AttentionListener,
    AttentionConfirmDialog.AttentionConfirmListener {

    private var currentPage=1

    private var isRefresh=true

    var keyWord: String=""

    private val mViewModel by lazy {
        ViewModelProvider(this).get(UgcViewModel::class.java)
    }

    private val mAdapter by lazy {
        UserMemberAdapter(mutableListOf(),activity!!,this)
    }

    private val mDialog by lazy {
        AttentionConfirmDialog(activity!!,R.style.custom_dialog,this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(activity!!, R.layout.fragment_search_layout,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        initViewModel()
        if (!TextUtils.isEmpty(keyWord)){
            isRefresh=true
            mViewModel.queryUserList(keyWord,1)
        }
    }

    override fun setSearchKey(key:String) {
        keyWord=key
        activity?.let {
            isRefresh=true
            mViewModel.queryUserList(keyWord,1)
        }
    }

    private fun initRv(){
        rv_search.setAdapter(mAdapter)
        rv_search.setLayoutManager(LinearLayoutManager(activity))
        rv_search.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryUserList(keyWord,currentPage+1)
            }

            override fun refresh() {
                isRefresh=true
                mViewModel.queryUserList(keyWord,1)
            }
        })
    }

    private fun initViewModel(){
        mViewModel.userSearchResp.observe(activity as FragmentActivity, Observer {
            if (isRefresh){
                mAdapter.mData=it
                currentPage=1
                if (mAdapter.mData.isEmpty()){
                    rv_search.showEmpty()
                }else{
                    rv_search.notifyDataSetChanged()
                }
            }else{
                if (it.isNullOrEmpty()){
                    rv_search.showNoMore()
                }else{
                    mAdapter.mData.addAll(it)
                    currentPage+=1
                    rv_search.notifyDataSetChanged()
                }

            }
        })

        mViewModel.attentionResp.observe(activity as FragmentActivity, Observer {
            if (TextUtils.isEmpty(it.msg)){
                mAdapter.mData[actionPosition].isFollow=it.code
                rv_search.notifyItemChanged(actionPosition)
            }else{
                toast(it.msg)
            }
        })
    }

    var actionPosition=-1

    var id:String=""

    override fun doAttention(position: Int, memberId: String,isFollow:Int) {
        actionPosition=position
        id=memberId
        if (isFollow==1){
            mDialog.show()
        }else{
            mViewModel.doAttention(memberId)
        }

    }

    override fun onConfirm() {
        mViewModel.doAttention(id)
    }
}
