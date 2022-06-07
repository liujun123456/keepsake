package com.shunlai.publish.search

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import com.getui.gs.sdk.GsManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.common.utils.toast
import com.shunlai.publish.R
import com.shunlai.common.bean.GoodsBean
import com.shunlai.publish.search.adapter.ProductSearchAdapter
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.activity_product_search.*
import org.jetbrains.anko.textColor
import org.json.JSONObject

class ProductSearchActivity : BaseActivity(),ProductSearchAdapter.OnItemGoodsChooseListener,ProductSearchView{
    override fun getMainContentResId(): Int= R.layout.activity_product_search

    override fun getToolBarResID(): Int = 0

    private val mAdapter by lazy {
        ProductSearchAdapter(mContext,mSearchResult,this)
    }

    private val mSearchResult by lazy {
        intent.getParcelableArrayListExtra<GoodsBean>(RunIntentKey.SEARCH_RESULT)?: mutableListOf<GoodsBean>()
    }

    private val mSearchKey by lazy {
        intent.getStringExtra(RunIntentKey.SEARCH_KEY)?:""
    }

    private val mPresenter by lazy {
        ProductSearchPresenter(mContext,this)
    }

    private var defaultTop=0

    override fun afterView() {
        initRv()
        initListener()
    }


    private fun initListener(){
        ll_clear_input.setOnClickListener {
            et_search_input.setText("")
            mSearchResult.clear()
            mAdapter.notifyDataSetChanged()
        }
        tv_search.setOnClickListener {
            if (tv_search.text=="取消"){
                finish()
            }else{
                doSearch()
            }
        }
        et_search_input.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                doSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        et_search_input.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s?.length?:0>0){
                    ll_clear_input.visibility=View.VISIBLE
                    tv_search.text="搜索"
                    tv_search.setBackgroundResource(R.drawable.black_radius_24_bg)
                    tv_search.textColor=Color.parseColor("#ffffff")
                    tv_search.setPadding(ScreenUtils.dip2px(mContext,17f),ScreenUtils.dip2px(mContext,8f),
                        ScreenUtils.dip2px(mContext,17f),ScreenUtils.dip2px(mContext,8f))
                }else{
                    ll_clear_input.visibility=View.GONE
                    tv_search.text="取消"
                    tv_search.setBackgroundResource(0)
                    tv_search.setPadding(0,0,0,0)
                    tv_search.textColor=Color.parseColor("#191919")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })


        if (!TextUtils.isEmpty(mSearchKey)){
            et_search_input.setText(mSearchKey)
            et_search_input.setSelection(mSearchKey.length-1)
        }else{
            et_search_input.post {
                et_search_input.requestFocus()
                showInput(et_search_input)
            }
        }
    }

    private fun doSearch(){
        if (TextUtils.isEmpty(et_search_input.text.toString())){
            toast("请输入您要搜索的内容!")
            return
        }
        mPresenter.searchGoods(et_search_input.text.toString())
    }

    private fun initRv(){
        rv_search_product.setAdapter(mAdapter)
        rv_search_product.setLayoutManager(LinearLayoutManager(mContext))
        rv_search_product.post {
            defaultTop=rv_search_product.top
        }
        rv_search_product.isCanRefresh=false
        rv_search_product.setSRecyclerListener(object :SRecyclerListener{
            override fun loadMore() {
                mPresenter.loadMoreGoods()
            }

            override fun refresh() {

            }
        })

    }

    override fun onItemGoods(bean: GoodsBean) {
        sensorsTrackItemChoose(bean)
        val intent=Intent()
        intent.putExtra(RunIntentKey.CHOOSE_GOODS,bean)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    override fun showLoading(value: String) {
        showBaseLoading()

    }

    override fun dismissLoading() {
        hideBaseLoading()

    }

    override fun searchSuccess(dates: MutableList<GoodsBean>, totalNum:String) {
        mSearchResult.clear()
        mSearchResult.addAll(dates)
        rv_search_product.notifyDataSetChanged()
        if (mSearchResult.isNullOrEmpty()){
            rv_search_product.showEmpty()
        }
        sensorsTrackGoodsList(totalNum)
    }

    override fun loadMoreSuccess(dates: MutableList<GoodsBean>) {
        mSearchResult.addAll(dates)
        rv_search_product.notifyDataSetChanged()
        if (dates.isNullOrEmpty()){
            rv_search_product.showNoMore()
        }
    }

    /**
     * 神策--搜索商品
     */
    private fun sensorsTrackGoodsList(totalNum:String){
        val params= JSONObject()
        params.put("page_name",screenUrl)
        params.put("search_words",et_search_input.text.toString())
        params.put("return_num",totalNum)
        GsManager.getInstance().onEvent("SearchProd", params)
    }

    /**
     * 神策--选择商品
     */
    private fun sensorsTrackItemChoose(bean: GoodsBean){
        val params= JSONObject()
        params.put("page_name",screenUrl)
        params.put("search_words",et_search_input.text.toString())
        params.put("product_name",bean.name)
        params.put("store_name",bean.shopName)
        params.put("product_source",bean.type)
        params.put("product_price",bean.price)
        GsManager.getInstance().onEvent("SelectTa", params)
    }
}
