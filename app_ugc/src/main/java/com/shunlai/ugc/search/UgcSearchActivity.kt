package com.shunlai.ugc.search

import android.app.ActivityOptions
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.router.RouterManager
import com.shunlai.ugc.R
import com.shunlai.ugc.UgcViewModel
import com.shunlai.ugc.entity.HotSearchBean
import kotlinx.android.synthetic.main.activity_ugc_search_layout.*
import kotlinx.android.synthetic.main.item_search_layout.view.*
import kotlinx.android.synthetic.main.title_ugc_search_layout.*

/**
 * @author Liu
 * @Date   2021/4/25
 * @mobile 18711832023
 */
class UgcSearchActivity:BaseActivity() {
    override fun getMainContentResId(): Int =R.layout.activity_ugc_search_layout

    override fun getToolBarResID(): Int=R.layout.title_ugc_search_layout

    private val mViewModel by lazy {
        ViewModelProvider(this).get(UgcViewModel::class.java)
    }

    override fun afterView() {
        initTitle()
        initViewModel()
        mViewModel.queryHotSearch()
    }


    private fun initTitle(){
        ll_back.visibility=View.GONE
        tv_cancel_search.visibility=View.VISIBLE
        et_search_input.isFocusable=false
        tv_cancel_search.setOnClickListener {
            finish()
        }
        et_search_input.setOnClickListener {
            jumpToResult(mutableMapOf())
        }
    }

    override fun onResume() {
        super.onResume()
        buildHistorySearch()
    }

    private fun initViewModel(){
        mViewModel.hotSearchResp.observe(this, Observer {
            buildHotSearch(it)
        })
    }


    fun clearHistory(view:View){
        PreferenceUtil.removeValueWithKey("historySearch")
        buildHistorySearch()
    }

    private fun buildHotSearch(dates:MutableList<HotSearchBean>){
        fv_hot_search.removeAllViews()
        dates.forEach {
            val view:View=View.inflate(mContext,R.layout.item_search_layout,null)
            view.tv_search.text=it.name
            view.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.UGC_SEARCH_KEY]=view.tv_search.text.toString()
                jumpToResult(params)
            }
            fv_hot_search.addView(view)
        }
    }

    private fun buildHistorySearch(){
        fv_history_search.removeAllViews()
        val history=PreferenceUtil.getSetString("historySearch")
        history?.forEach {
            val view:View=View.inflate(mContext,R.layout.item_search_layout,null)
            view.tv_search.text=it
            view.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.UGC_SEARCH_KEY]=view.tv_search.text.toString()
                jumpToResult(params)
            }
            fv_history_search.addView(view)
        }
    }

    private fun jumpToResult(params:MutableMap<String,Any?>){
        val bundle= ActivityOptions.makeSceneTransitionAnimation(this,et_search_input,"search_transition").toBundle()
        RouterManager.startTransActivityWithParams(BundleUrl.UGC_SEARCH_RESULT_ACTIVITY,this,bundle,params)
    }
}
