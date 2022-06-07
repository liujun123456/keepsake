package com.shunlai.mine.shop.edit

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.common.utils.toast
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.OwnerUgcBean
import com.shunlai.mine.shop.ShopViewModel
import com.shunlai.mine.shop.edit.adapter.OwnerUgcAdapter
import com.shunlai.ui.srecyclerview.RefreshGridInset
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.activity_edit_ugc_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/7/16
 * @mobile 18711832023
 */
class EditChooseUgcActivity:BaseActivity(), OwnerUgcAdapter.OwnerUgcListener {
    override fun getMainContentResId(): Int= R.layout.activity_edit_ugc_layout

    override fun getToolBarResID(): Int=0

    private val mViewModel by lazy {
        ViewModelProvider(this).get(ShopViewModel::class.java)
    }

    private val mAdapter by lazy {
        OwnerUgcAdapter(mContext, mutableListOf(),this)
    }

    private var ugcBean:OwnerUgcBean?=null

    var currentPage=1

    var isRefresh=true

    override fun afterView() {
        initViewModel()
        initRv()
        initTitle()
        mViewModel.queryOwnerUgc(currentPage)
    }

    private fun initTitle(){
        iv_close_search.setOnClickListener {
            finish()
        }
        tv_confirm.setOnClickListener {
            if (ugcBean==null){
                toast("请先选择笔记")
            }else{
                EventBus.getDefault().post(ugcBean)
                finish()
            }
        }
    }

    private fun initRv(){
        rv_mine_ugc.setAdapter(mAdapter)
        rv_mine_ugc.setLayoutManager(GridLayoutManager(mContext,2))
        rv_mine_ugc.getRecyclerView().addItemDecoration(RefreshGridInset(2,  ScreenUtils.dip2px(mContext,16f), true,true))
        rv_mine_ugc.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryOwnerUgc(currentPage+1)
            }

            override fun refresh() {

            }
        })
    }

    private fun initViewModel(){
        mViewModel.ownerUgcList.observe(this, Observer {
            if (isRefresh){
                currentPage=1
                mAdapter.mData=it
                rv_mine_ugc.notifyDataSetChanged()
            }else{
                if (it.isEmpty()){
                    rv_mine_ugc.showNoMore()
                }else{
                    currentPage+=1
                    mAdapter.mData.addAll(it)
                    rv_mine_ugc.notifyDataSetChanged()
                }
            }
        })
    }

    private fun updateSignBoard(){
        if (ugcBean!=null){
            iv_ugc_image.visibility= View.VISIBLE
            tv_ugc_title.visibility= View.VISIBLE
            ImageUtil.showRoundImgWithStringAndRadius(iv_ugc_image,mContext,ugcBean!!.firstImage,8f)
            tv_ugc_title.text=ugcBean!!.content
            tv_ugc_title?.post {
                    tv_ugc_title.isSelected=true
                }
        }else{
            iv_ugc_image.visibility= View.INVISIBLE
            tv_ugc_title.visibility= View.INVISIBLE
        }
    }

    override fun onOwnerUgcClick(bean: OwnerUgcBean) {
        ugcBean=bean
        updateSignBoard()
    }

}
