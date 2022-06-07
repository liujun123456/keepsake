package com.shunlai.ugc.comment

import android.graphics.Color
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.ugc.R
import com.shunlai.ugc.UgcViewModel
import com.shunlai.ugc.comment.adapter.UgcCommentAdapter
import com.shunlai.ui.MediaGridInset
import kotlinx.android.synthetic.main.activity_ugc_comment_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*

/**
 * @author Liu
 * @Date   2021/4/25
 * @mobile 18711832023
 */
class UgcCommentActivity:BaseActivity() {
    override fun getMainContentResId(): Int =R.layout.activity_ugc_comment_layout

    override fun getToolBarResID(): Int=R.layout.public_title_layout

    private val mViewModel by lazy {
        ViewModelProvider(this).get(UgcViewModel::class.java)
    }

    private val mAdapter by lazy {
        UgcCommentAdapter(mContext, mutableListOf())
    }

    private val productId by lazy {
        intent.getStringExtra(RunIntentKey.PRODUCT_ID)
    }

    private var currentPage:Int=1
    private var currentChoose:Int=1

    override fun afterView() {
        intiTitle()
        initViewModel()
        initRv()
        initListener()
        mViewModel.queryGoodsComment(productId,1,currentPage)
    }

    private fun initViewModel(){
        mViewModel.ugcCommentResp.observe(this, Observer {
            if (it.isSuccess){
                mAdapter.mData=it.data?: mutableListOf()
                mAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun intiTitle(){
        ll_back.setOnClickListener {
            finish()
        }
        tv_title_content.text="商品评价"
    }

    private fun initListener(){
        tv_hot.setOnClickListener {
            currentChoose=1
            currentPage=1
            mViewModel.queryGoodsComment(productId,currentChoose,currentPage)
            updateLabel()
        }
        tv_new.setOnClickListener {
            currentChoose=2
            currentPage=1
            mViewModel.queryGoodsComment(productId,currentChoose,currentPage)
            updateLabel()
        }
        tv_star_more.setOnClickListener {
            currentChoose=3
            currentPage=1
            mViewModel.queryGoodsComment(productId,currentChoose,currentPage)
            updateLabel()
        }
        tv_star_less.setOnClickListener {
            currentChoose=4
            currentPage=1
            mViewModel.queryGoodsComment(productId,currentChoose,currentPage)
            updateLabel()
        }
    }

    private fun updateLabel(){
        tv_hot.setBackgroundResource(R.drawable.gray_24_radius_bg)
        tv_hot.setTextColor(Color.parseColor("#0D0D0D"))
        tv_new.setBackgroundResource(R.drawable.gray_24_radius_bg)
        tv_new.setTextColor(Color.parseColor("#0D0D0D"))
        tv_star_more.setBackgroundResource(R.drawable.gray_24_radius_bg)
        tv_star_more.setTextColor(Color.parseColor("#0D0D0D"))
        tv_star_less.setBackgroundResource(R.drawable.gray_24_radius_bg)
        tv_star_less.setTextColor(Color.parseColor("#0D0D0D"))

        if (currentChoose==1){
            tv_hot.setBackgroundResource(R.drawable.black_radius_24)
            tv_hot.setTextColor(Color.parseColor("#ffffff"))
        }else if (currentChoose==2){
            tv_new.setBackgroundResource(R.drawable.black_radius_24)
            tv_new.setTextColor(Color.parseColor("#ffffff"))
        }else if (currentChoose==3){
            tv_star_more.setBackgroundResource(R.drawable.black_radius_24)
            tv_star_more.setTextColor(Color.parseColor("#ffffff"))
        }else if (currentChoose==4){
            tv_star_less.setBackgroundResource(R.drawable.black_radius_24)
            tv_star_less.setTextColor(Color.parseColor("#ffffff"))
        }
    }

    private fun initRv(){
        rv_ugc_comment.layoutManager=GridLayoutManager(mContext,2)
        rv_ugc_comment.addItemDecoration(MediaGridInset(2, ScreenUtils.dip2px(mContext,16f), true,true))
        rv_ugc_comment.adapter=mAdapter
    }
}
