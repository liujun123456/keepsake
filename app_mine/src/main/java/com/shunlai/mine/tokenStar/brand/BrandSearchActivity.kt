package com.shunlai.mine.tokenStar.brand

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.RunCacheDataUtil
import com.shunlai.common.utils.toast
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.BrandBean
import com.shunlai.mine.tokenStar.TokenStarViewModel
import com.shunlai.mine.tokenStar.adapter.BrandSearchAdapter
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.activity_brand_search_layout.*
import kotlinx.android.synthetic.main.title_brand_search_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/8/27
 * @mobile 18711832023
 */
class BrandSearchActivity:BaseActivity(), BrandSearchAdapter.BrandSearchListener {

    override fun getToolBarResID(): Int=R.layout.title_brand_search_layout

    override fun getMainContentResId(): Int =R.layout.activity_brand_search_layout

    private val mViewModel by lazy {
        ViewModelProvider(this).get(TokenStarViewModel::class.java)
    }

    private val mAdapter by lazy {
        BrandSearchAdapter(mContext, mutableListOf(),this)
    }

    private var currentPage=1

    private var isRefresh=true

    override fun afterView() {
        et_search_brand.post {
            et_search_brand.requestFocus()
            showInput(et_search_brand)
        }
        initListener()
        initViewModel()
        initRv()
    }

    private fun initListener(){
        et_search_brand.setOnEditorActionListener { _, actionId, _ ->
            if (actionId== EditorInfo.IME_ACTION_SEARCH){
                if(TextUtils.isEmpty(et_search_brand.text.toString())){
                    toast("请输入搜索内容")
                    return@setOnEditorActionListener true
                }
                hideInput(et_search_brand)
                isRefresh=true
                mViewModel.queryBrandByKey(et_search_brand.text.toString(),1)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        et_search_brand.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length?:0>0){
                    ll_clear_input.visibility= View.VISIBLE
                }else{
                    ll_clear_input.visibility= View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        ll_clear_input.setOnClickListener {
            et_search_brand.setText("")
        }
        tv_cancel_search.setOnClickListener { finish() }
    }

    private fun initRv(){
        rv_brand_result.setAdapter(mAdapter)
        rv_brand_result.setLayoutManager(LinearLayoutManager(mContext))
        rv_brand_result.setSRecyclerListener(object :SRecyclerListener{
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryBrandByKey(et_search_brand.text.toString(),currentPage+1)
            }

            override fun refresh() {

            }

        })
    }

    private fun initViewModel(){
        mViewModel.brandList.observe(this, Observer {
            if (isRefresh){
                currentPage=1
                mAdapter.mData.clear()
                if (it.isNullOrEmpty()){
                    rv_brand_result.showEmpty()
                }else{
                    mAdapter.mData.addAll(it)
                    rv_brand_result.notifyDataSetChanged()
                }
            }else{
                if (it.isNullOrEmpty()){
                    rv_brand_result.showNoMore()
                }else{
                    currentPage+=1
                    mAdapter.mData.addAll(it)
                    rv_brand_result.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        hideInput(et_search_brand)
    }

    override fun onItemClick(bean: BrandBean, position: Int) {
        bean.brandCode?.let {
            if (RunCacheDataUtil.brandChooseData.contains(it)){
                RunCacheDataUtil.brandChooseData.remove(it)
                EventBus.getDefault().post(bean)
                rv_brand_result.notifyItemChanged(position)
            }else{
                RunCacheDataUtil.brandChooseData.add(it)
                EventBus.getDefault().post(bean)
                finish()
            }
        }

    }
}
