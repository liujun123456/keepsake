package com.shunlai.ugc.search.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.ugc.R
import com.shunlai.ugc.UgcViewModel
import com.shunlai.ugc.search.UgcSearchAdapter
import com.shunlai.ui.srecyclerview.RefreshGridInset
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_search_layout.*

/**
 * @author Liu
 * @Date   2021/8/24
 * @mobile 18711832023
 */
class UgcSearchFragment():SearchFragment() {

    private var currentPage=1

    private var isRefresh=true

    var keyWord: String=""

    private val mViewModel by lazy {
        ViewModelProvider(this).get(UgcViewModel::class.java)
    }

    private val mAdapter by lazy {
        UgcSearchAdapter(activity!!, mutableListOf())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(activity,R.layout.fragment_search_layout,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        initViewModel()
        if (!TextUtils.isEmpty(keyWord)){
            isRefresh=true
            mViewModel.queryUgcList(keyWord,1)
        }

    }

    override fun setSearchKey(key:String) {
        keyWord=key
        activity?.let {
            isRefresh=true
            mViewModel.queryUgcList(keyWord,1)
        }
    }

    private fun initRv(){
        rv_search.setAdapter(mAdapter)
        rv_search.setLayoutManager(GridLayoutManager(activity,2))
        rv_search.getRecyclerView().addItemDecoration(RefreshGridInset(2,  ScreenUtils.dip2px(activity,16f), true,true))
        rv_search.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryUgcList(keyWord,currentPage+1)
            }

            override fun refresh() {
                isRefresh=true
                mViewModel.queryUgcList(keyWord,1)
            }
        })
    }

    private fun initViewModel(){
        mViewModel.ugcSearchResp.observe(activity as FragmentActivity, Observer {
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
    }

}
