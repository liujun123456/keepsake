package com.shunlai.mine.attention

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.attention.adapter.MineFollowAndFunAdapter
import com.shunlai.mine.entity.bean.AttentionEvent
import com.shunlai.mine.entity.bean.FollowAndFun
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.activity_mine_attention_like_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*
import org.jetbrains.anko.toast
import kotlin.math.abs

/**
 * @author Liu
 * @Date   2021/5/25
 * @mobile 18711832023
 */
class MineAttentionActivity:BaseActivity(), MineFollowAndFunAdapter.AttentionClickListener {
    override fun getMainContentResId(): Int =R.layout.activity_mine_attention_like_layout

    override fun getToolBarResID(): Int= R.layout.public_title_layout

    private val type by lazy {
        intent.getIntExtra(RunIntentKey.TYPE,0)  //0我的关注 1我的粉丝
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    private val mAdapter by lazy {
        MineFollowAndFunAdapter(mContext, mutableListOf(),this,type)
    }

    override fun afterView() {
        initTitle()
        initViewModel()
        initRv()
    }

    private var currentPage:Int=1

    private var isRefresh=true

    private  fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
        if (type==0){
            tv_title.text="我的关注"
        }else{
            tv_title.text="我的粉丝"
        }
    }

    private fun initRv(){
        rv_layout.setAdapter(mAdapter)
        rv_layout.setLayoutManager(LinearLayoutManager(mContext))
        rv_layout.setSRecyclerListener(object : SRecyclerListener{
            override fun loadMore() {
                loadMoreData()
            }

            override fun refresh() {
                refreshData()
            }
        })
        rv_layout.doRefresh()

        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) > ScreenUtils.dip2px(mContext,40f)){
                if (type==0){
                    tv_title_content.text="我的关注"
                }else{
                    tv_title_content.text="我的粉丝"
                }
            }else{
                tv_title_content.text = ""
            }
        })

    }

    private fun initViewModel(){
        mViewModel.mineFollowResp.observe(this, Observer {
            dealResponse(it)
        })
        mViewModel.mineFunResp.observe(this, Observer {
            dealResponse(it)
        })
        mViewModel.attentionResp.observe(this, Observer {
            hideBaseLoading()
            if (it!=null){
                if (type==0){
                    mAdapter.mData[currentPosition].isFollow=it
                    rv_layout.notifyDataSetChanged()
                }else{
                    mAdapter.mData[currentPosition].isEachOther=it
                    rv_layout.notifyDataSetChanged()
                }
            }else{
                toast("操作失败")
            }
        })
    }

    private fun refreshData(){
        if (type==0){
            mViewModel.queryMineFollow(1)
        }else{
            mViewModel.queryMineFun(1)
        }
        isRefresh=true
    }

    private fun loadMoreData(){
        if (type==0){
            mViewModel.queryMineFollow(currentPage+1)
        }else{
            mViewModel.queryMineFun(currentPage+1)
        }
        isRefresh=false
    }

    private fun dealResponse(it:MutableList<FollowAndFun>){
        if (it.isNullOrEmpty()){
            if (isRefresh){
                rv_layout.showEmpty()
            }else{
                rv_layout.showNoMore()
            }
        }else{
            if (isRefresh){
                currentPage=1
                mAdapter.mData=it
            }else{
                currentPage+=1
                mAdapter.mData.addAll(it)
            }
            rv_layout.notifyDataSetChanged()
        }
    }

    var currentPosition=0
    override fun onAttentionClick(event: AttentionEvent) {
        currentPosition=event.position
        showBaseLoading()
        mViewModel.doAttention(event.memberId.toString())
    }
}
