package com.shunlai.ugc.search

import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.viewpager.widget.ViewPager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.toast
import com.shunlai.ugc.R
import com.shunlai.ugc.search.fragment.UgcSearchFragment
import com.shunlai.ugc.search.fragment.UserSearchFragment
import kotlinx.android.synthetic.main.activity_ugc_search_result_layout.*
import kotlinx.android.synthetic.main.title_ugc_search_layout.*

/**
 * @author Liu
 * @Date   2021/4/25
 * @mobile 18711832023
 */
class UgcSearchResultActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_ugc_search_result_layout

    override fun getToolBarResID(): Int=R.layout.title_ugc_search_layout

    override fun setTitleColor(): Int=R.color.search_title_color

    private val searchKey by lazy {
        intent.getStringExtra(RunIntentKey.UGC_SEARCH_KEY)
    }

    private val mFragments= mutableListOf(UgcSearchFragment(), UserSearchFragment())

    override fun afterView() {
        initTitle()
        initPager()
        if (!TextUtils.isEmpty(searchKey)){
            et_search_input.setText(searchKey)
            et_search_input.setSelection(searchKey.length)
            mFragments.forEach {
                it.setSearchKey(searchKey)
            }
            saveSearchKey()
        }else{
            et_search_input.post {
                et_search_input.requestFocus()
                showInput(et_search_input)
            }
        }

        tv_search_ugc.setOnClickListener {
            search_pager.currentItem=0
        }
        tv_search_user.setOnClickListener {
            search_pager.currentItem=1
        }
    }

    private fun initPager(){
        search_pager.adapter=SearchPagerAdapter(supportFragmentManager,mFragments)
        search_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
               if (position==0){
                   tv_search_ugc.setTextColor(Color.parseColor("#0d0d0d"))
                   tv_search_ugc.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.search_choose_tab_index_bg)
                   tv_search_ugc.typeface = Typeface.DEFAULT_BOLD
                   tv_search_user.setTextColor(Color.parseColor("#999999"))
                   tv_search_user.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.search_un_choose_tab_index_bg)
                   tv_search_user.typeface = Typeface.DEFAULT
               }else{
                   tv_search_ugc.setTextColor(Color.parseColor("#999999"))
                   tv_search_ugc.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.search_un_choose_tab_index_bg)
                   tv_search_ugc.typeface = Typeface.DEFAULT
                   tv_search_user.setTextColor(Color.parseColor("#0d0d0d"))
                   tv_search_user.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.search_choose_tab_index_bg)
                   tv_search_user.typeface = Typeface.DEFAULT_BOLD
               }
            }

        })
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
        et_search_input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId== EditorInfo.IME_ACTION_SEARCH){
                if(TextUtils.isEmpty(et_search_input.text.toString())){
                    toast("请输入搜索内容")
                    return@setOnEditorActionListener true
                }
                hideInput(et_search_input)
                mFragments.forEach {
                    it.setSearchKey(et_search_input.text.toString())
                }
                saveSearchKey()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        et_search_input.addTextChangedListener(object :TextWatcher{
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
            et_search_input.setText("")
        }
    }



    private fun saveSearchKey(){
        var historyList= PreferenceUtil.getSetString("historySearch")
        if (historyList==null){
            historyList= mutableSetOf()
            historyList.add(et_search_input.text.toString())
        }else{
            if (!historyList.contains(et_search_input.text.toString())){
                historyList.add(et_search_input.text.toString())
            }
        }
        PreferenceUtil.putSetString("historySearch",historyList)
    }
}
