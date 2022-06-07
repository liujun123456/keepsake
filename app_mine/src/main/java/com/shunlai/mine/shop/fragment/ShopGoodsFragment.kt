package com.shunlai.mine.shop.fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shunlai.common.BaseFragment
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.MineGoodsEvent
import com.shunlai.mine.fragment.adapter.MineGoodsAdapter
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_shop_goods_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/7/9
 * @mobile 18711832023
 * @desc type 1推荐 0避雷
 */
class ShopGoodsFragment(var memberId:String,var type:Int):BaseFragment() {

    constructor():this("",0)

    override fun createView(): Int=R.layout.fragment_shop_goods_layout

    override fun createTitle(): Int=0

    private var currentPage=1

    private var isRefresh=true

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    private val mAdapter by lazy {
        MineGoodsAdapter(mContext, mutableListOf(),memberId== PreferenceUtil.getString(
            Constant.USER_ID))
    }


    override fun afterView() {
        initRv()
        initViewModel()
    }

    private fun initRv(){
        rv_shop_goods.setAdapter(mAdapter)
        rv_shop_goods.setLayoutManager(StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL))
        rv_shop_goods.setSRecyclerListener(object :SRecyclerListener{
            override fun loadMore() {
                isRefresh=false
                mViewModel.queryUserGoods(currentPage+1,type,memberId)
            }

            override fun refresh() {

            }
        })
    }

    private fun initViewModel(){
        mViewModel.queryUserGoods(1,type,memberId)
        mViewModel.userGoods.observe(this, Observer {
            EventBus.getDefault().post(MineGoodsEvent((it.total_records?:0L).toString(),type))
            if (isRefresh){
                mAdapter.mData=it.data
                if (it.data.isEmpty()){
                    rv_shop_goods.showEmpty()
                }else{
                    rv_shop_goods.notifyDataSetChanged()
                }
                currentPage=1
            }else{
                mAdapter.mData.addAll(it.data)
                if (it.data.isEmpty()){
                    rv_shop_goods.showNoMore()
                }else{
                    rv_shop_goods.notifyDataSetChanged()
                    currentPage+=1
                }
            }
        })
    }

}
