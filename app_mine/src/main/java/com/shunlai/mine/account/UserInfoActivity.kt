package com.shunlai.mine.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.PathItem
import com.shunlai.common.utils.*
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_user_info_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*
import java.io.File
import java.lang.Exception

/**
 * @author Liu
 * @Date   2021/5/25
 * @mobile 18711832023
 */
class UserInfoActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_user_info_layout

    override fun getToolBarResID(): Int = R.layout.public_title_layout

    private var memberInfo: MemberInfo?=null

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }


    override fun afterView() {
        try {
            memberInfo= GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)
        }catch (e:Exception){

        }
        initData()
        initListener()
        initViewModel()
    }

    private fun initData(){
        memberInfo?.let {
            ImageUtil.showCircleImgWithString(iv_avatar,mContext,it.avatar?:"")
            tv_user_name.text=it.nickName
            if (TextUtils.isEmpty(it.introduce)){
                tv_desc.text="暂无简介"
            }else{
                tv_desc.text=it.introduce
            }
        }
    }

    private fun initListener(){
        ll_back.setOnClickListener { finish() }
        rl_nick_name.setOnClickListener {
            if (memberInfo==null){
                toast("用户信息异常!")
                return@setOnClickListener
            }
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.TYPE]=0
            params[RunIntentKey.MEMBER_INFO]=GsonUtil.toJson(memberInfo!!)
            RouterManager.startActivityForResultWithParams(BundleUrl.MINE_USER_INFO_EDIT_ACTIVITY,this,params,10086)
        }
        rl_desc.setOnClickListener {
            if (memberInfo==null){
                toast("用户信息异常!")
                return@setOnClickListener
            }
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.TYPE]=1
            params[RunIntentKey.MEMBER_INFO]=GsonUtil.toJson(memberInfo!!)
            RouterManager.startActivityForResultWithParams(BundleUrl.MINE_USER_INFO_EDIT_ACTIVITY,this,params,10086)
        }
        rl_label.setOnClickListener {
            if (memberInfo==null){
                toast("用户信息异常!")
                return@setOnClickListener
            }
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.TYPE]=2
            params[RunIntentKey.MEMBER_INFO]=GsonUtil.toJson(memberInfo!!)
            RouterManager.startActivityForResultWithParams(BundleUrl.MINE_USER_INFO_EDIT_ACTIVITY,this,params,10086)
        }
        iv_avatar.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.IS_ONLY_PICKER]=true
            params[RunIntentKey.LIMIT_SIZE]=1
            RouterManager.startActivityForResultWithParams(BundleUrl.PHOTO_PICKER_ACTIVITY,this,params,10087)
        }
    }

    private fun initViewModel(){
        mViewModel.uploadResp.observe(this, Observer {
            if (TextUtils.isEmpty(it)){
                hideBaseLoading()
                toast("头像上传失败!")
            }else{
                updateInfo(it)
            }
        })
        mViewModel.updateUserResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                initData()
            }else{
                toast(it.errorMsg)
            }
        })
    }


    private fun updateInfo(path:String){
        memberInfo?.avatar=path
        mViewModel.updateUserInfo(path,null,null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==10086&&resultCode== Activity.RESULT_OK){
            data?.let {
                memberInfo= GsonUtil.fromJson(it.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)
                initData()
            }
        }else if (requestCode==10087&&resultCode== Activity.RESULT_OK){
            val dates=data?.getStringExtra("choose_img_item_resource")
            dates?.let{
                val result= Gson().fromJson<List<PathItem>>(it,object : TypeToken<List<PathItem>>() {}.type)
                if (!result.isNullOrEmpty()){
                    val file = File(PathUtils.getPath(mContext, Uri.parse(result[0].path)))
                    mViewModel.uploadFile(FileUtil.luBanPicture(mContext, file))
                    showBaseLoading()
                }
            }
        }
    }
}
