package com.shunlai.main.ht.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.main.HomeViewModel
import com.shunlai.main.R
import com.shunlai.main.entities.HistoryKey
import com.shunlai.main.entities.HuaTiBean
import com.shunlai.main.ht.adapter.HuaTiSearchAdapter
import com.shunlai.net.util.GsonUtil
import com.shunlai.ui.MediaGridInset
import kotlinx.android.synthetic.main.activity_hua_ti_search_layout.*
import kotlinx.android.synthetic.main.item_search_hua_ti_layout.view.*

/**
 * @author Liu
 * @Date   2021/5/8
 * @mobile 18711832023
 */
class HuaTiSearchActivity : BaseActivity(), HuaTiSearchAdapter.KeywordItemClick {
    override fun getMainContentResId(): Int = R.layout.activity_hua_ti_search_layout

    override fun getToolBarResID(): Int = 0

    private val mViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private val mAdapter by lazy {
        HuaTiSearchAdapter(mContext, mutableListOf(), this)
    }

    private val historyKey by lazy {
        PreferenceUtil.getSetString("historySearchHtBean") ?: mutableSetOf()
    }

    override fun afterView() {
        mViewModel.queryRecommendHt()
        buildHistory()
        initViewModel()
        initListener()
        initRv()
    }

    private fun initRv() {
        rv_hua_ti_result.layoutManager = LinearLayoutManager(mContext)
        rv_hua_ti_result.addItemDecoration(
            MediaGridInset(
                1,
                ScreenUtils.dip2px(mContext, 16f),
                false,
                true
            )
        )
        rv_hua_ti_result.adapter = mAdapter
    }

    private fun initListener() {
        iv_close_search.setOnClickListener {
            finish()
        }
        tv_cancel_search.setOnClickListener {
            et_search_hua_ti.setText("")
            hideInput(et_search_hua_ti)
        }
        et_search_hua_ti.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    tv_cancel_search.visibility = View.GONE
                    ll_hot.visibility = View.VISIBLE
                    ll_recent.visibility = View.VISIBLE
                    rv_hua_ti_result.visibility = View.GONE
                    tv_result_none.visibility = View.GONE
                } else {
                    tv_cancel_search.visibility = View.VISIBLE
                    mViewModel.queryHtByKeyword(s.toString())

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun buildHistory() {
        fv_recent_list.removeAllViews()
        historyKey.forEach { value ->
            val bean = GsonUtil.fromJson(value, HuaTiBean::class.java)
            val view = View.inflate(mContext, R.layout.item_search_hua_ti_layout, null)
            if (bean.activity==true){
                view.tv_label.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.activity_loading_bg,
                    0,
                    0,
                    0
                )
                (view.tv_label.compoundDrawables[0] as AnimationDrawable).start()
                view.tv_label.text = " ${bean.tag}"
            }else{
                view.tv_label.text = "# ${bean.tag}"
            }
            view.setOnClickListener {
                onItemClick(bean)
            }
            fv_recent_list.addView(view)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViewModel() {
        mViewModel.recommendHtResp.observe(this, Observer {
            fv_list.removeAllViews()
            it.forEach { bean ->
                val view = View.inflate(mContext, R.layout.item_search_hua_ti_layout, null)
                if (bean.activity == true) {
                    view.tv_label.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.activity_loading_bg,
                        0,
                        0,
                        0
                    )
                    (view.tv_label.compoundDrawables[0] as AnimationDrawable).start()
                    view.tv_label.text = " ${bean.tag}"
                } else {
                    view.tv_label.text = "# ${bean.tag}"
                }
                view.setOnClickListener {
                    onItemClick(bean)
                }
                fv_list.addView(view)
            }
        })
        mViewModel.keywordHtResp.observe(this, Observer {
            if (TextUtils.isEmpty(et_search_hua_ti.text.toString())) {
                return@Observer
            }
            if (it.isNullOrEmpty()) {
                rv_hua_ti_result.visibility = View.GONE
                ll_recent.visibility = View.GONE
                tv_result_none.visibility = View.VISIBLE
                ll_hot.visibility = View.VISIBLE
            } else {
                rv_hua_ti_result.visibility = View.VISIBLE
                ll_recent.visibility = View.GONE
                tv_result_none.visibility = View.GONE
                ll_hot.visibility = View.GONE
                mAdapter.mData = it
                mAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onItemClick(bean: HuaTiBean) {
        val it=historyKey.iterator()
        while (it.hasNext()){
            val value=it.next()
            if ((GsonUtil.fromJson(value,HuaTiBean::class.java)).tag==bean.tag){
                it.remove()
            }
        }
        historyKey.add(GsonUtil.toJson(bean))
        PreferenceUtil.putSetString("historySearchHtBean", historyKey)
        val intent = Intent()
        intent.putExtra("ht_key_word", GsonUtil.toJson(bean))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}
