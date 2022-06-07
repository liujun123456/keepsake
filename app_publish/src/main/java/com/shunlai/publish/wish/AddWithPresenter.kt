package com.shunlai.publish.wish

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.utils.FileUtil
import com.shunlai.common.utils.PathUtils
import com.shunlai.common.utils.toast
import com.shunlai.publish.PublishViewModel
import com.shunlai.publish.R
import com.shunlai.publish.entity.req.AddWishReq
import java.io.File

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
class AddWithPresenter(var mCtx:Context,var mView:AddWishView) {

    private val mViewModel by lazy {
        ViewModelProvider(mCtx as FragmentActivity).get(PublishViewModel::class.java)
    }

    private val wishReq= AddWishReq()

    init {
        initViewModel()
    }

    private fun initViewModel(){
        mViewModel.uploadResp.observe(mCtx as FragmentActivity, Observer {
            if (it.isSuccess){
                wishReq.image=it.url
                mViewModel.addWish(wishReq)
            }else{
                mView.dismissLoading()
                toast(it?.errorMsg?:mCtx.resources.getString(R.string.complete_wish_fail))
            }
        })
        mViewModel.addWishResp.observe(mCtx as FragmentActivity, Observer {
            mView.dismissLoading()
            if (it.isSuccess){
                mView.addWishSuccess(it.buildGoods())
            }else{
                toast(it?.errorMsg?:mCtx.resources.getString(R.string.complete_wish_fail))
            }
        })
    }

    fun publishWish(brand:String,goodsName:String,price:String?=null,uri: Uri?){
        if (TextUtils.isEmpty(brand)){
            toast(R.string.check_brandName)
            return
        }
        if (uri==null){
            toast(R.string.check_img)
            return
        }
        wishReq.brandName=brand
        wishReq.name=goodsName
        if (!TextUtils.isEmpty(price)){
            price?.let {
                val realPrice=price.toFloat().times(100).toInt().toFloat()/100
                wishReq.price=realPrice.toString()
            }
        }else{
            wishReq.price=price
        }
        mView.showLoading("提交中")
        val file = File(PathUtils.getPath(mCtx, uri))
        mViewModel.uploadFile(FileUtil.luBanPicture(mCtx, file))
    }
}
