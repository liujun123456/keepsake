package com.shunlai.publish.sign

import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.utils.toast
import com.shunlai.publish.PublishViewModel
import com.shunlai.publish.R


/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
class ProductSignPresenter(var mCtx: Context,var mView:ProductSignView) {

    private val mViewModel by lazy {
        ViewModelProvider(mCtx as FragmentActivity).get(PublishViewModel::class.java)
    }

    init {
        initViewModel()
    }

    private fun initViewModel(){
        mViewModel.searchResp.observe(mCtx as FragmentActivity, Observer {
            mView.dismissLoading()
            if (it.isSuccess){
                if (it.data.isNullOrEmpty()){
                    toast(R.string.un_search_goods)
                }else{
                   it.data?.let {list->
                       if (list.size>1){
                           mView.goSearchPage(list)
                       }else{
                           mView.addSignGoods(list[0])
                           mView.trackSignGoods(list[0])
                       }
                   }
                }
            }else{
                toast(R.string.un_search_goods)
            }
        })
    }
    var searchKey:String=""

    fun clipSearch(){
        val cm: ClipboardManager =mCtx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = cm.primaryClip
        if (data!=null){
            val item = data.getItemAt(0)
            if (item!=null){
                if (!TextUtils.isEmpty(item.text)){
                    mView.showLoading("查询商品中!")
                    searchKey=item.text.toString()
                    mViewModel.searchGoods(searchKey,1)
                    return
                }
            }
        }
        toast(R.string.clip_notice_text)
        return
    }
}
