package com.shunlai.mine.photowall

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.*
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.mine.entity.bean.SavePublishBean
import com.shunlai.mine.entity.bean.WallPhotoBean
import com.shunlai.mine.fragment.adapter.ImgWallAdapter
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.ui.DrawableManager
import kotlinx.android.synthetic.main.activity_photo_wall_layout.*

/**
 * @author Liu
 * @Date   2021/5/18
 * @mobile 18711832023
 */
class PhotoWallActivity:BaseActivity() {
    override fun getMainContentResId(): Int= R.layout.activity_photo_wall_layout

    override fun getToolBarResID(): Int=0

    private val mAdapter by lazy {
        ImgWallAdapter(mContext, mutableListOf(),1)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    private var memberInfo:MemberInfo?=null

    private var userType= 0  //0代表自己 1代表别人

    private var wallPhotoList:MutableList<WallPhotoBean>?= mutableListOf()

    override fun afterView() {

        try {
            userType=intent.getIntExtra(RunIntentKey.USER_TYPE,0)?:0

            memberInfo= GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)

            wallPhotoList=Gson().fromJson<MutableList<WallPhotoBean>>( intent.getStringExtra(RunIntentKey.WALL_PHOTO_LIST),object : TypeToken<MutableList<WallPhotoBean>>() {}.type)
        }catch (e:Exception){

        }
        initListener()
        showWall()
    }

    private fun showWall(){
        if (wallPhotoList.isNullOrEmpty()){
            rl_empty_wall.visibility=View.VISIBLE
            rl_wall.visibility=View.GONE
            if (userType==0){   //自己没有图片墙
                ll_mine_no_wall.visibility=View.VISIBLE
                ll_mine_wall.visibility=View.GONE
            }else{   //别人没有图片墙
                ll_mine_no_wall.visibility=View.GONE
                ll_mine_wall.visibility=View.VISIBLE
                ll_other.visibility=View.VISIBLE
                ll_go_publish2.visibility=View.GONE
                ll_edit.visibility=View.GONE
            }
        }else{
            rl_empty_wall.visibility=View.GONE
            rl_wall.visibility=View.VISIBLE
            view_wall_bg.background=DrawableManager.buildLinerDrawable(intArrayOf(Color.parseColor("#cb000000"),
                Color.parseColor("#33000000"),
                Color.parseColor("#cb000000")))
            ll_mine_no_wall.visibility=View.GONE
            ll_mine_wall.visibility=View.VISIBLE
            initRv()
            if (userType==0){   //自己有图片墙
                ll_other.visibility=View.GONE
                ll_go_publish2.visibility=View.VISIBLE
            }else{        //别人有图片墙
                ll_other.visibility=View.VISIBLE
                ll_go_publish2.visibility=View.GONE
                ll_edit.visibility=View.GONE
            }
        }
        showFunc()
    }


    private fun showFunc(){
        ImageUtil.showCircleImgWithString(iv_avatar,mContext,memberInfo?.avatar?:"",R.mipmap.user_default_icon)
        tv_user_name.text=memberInfo?.nickName
        if (userType!=0){
            if (TextUtils.isEmpty(memberInfo?.introduce)){
                tv_user_desc.text="他没有个人简介哦"
            }else{
                tv_user_desc.text=memberInfo?.introduce
            }
            if (memberInfo?.isFollow==1){
                ll_un_attention_layout.visibility=View.GONE
                ll_attention_layout.visibility=View.VISIBLE
            }else{
                ll_un_attention_layout.visibility=View.VISIBLE
                ll_attention_layout.visibility=View.GONE
            }
        }
    }

    private fun initRv(){
        val layoutManager= GridLayoutManager(mContext,6)
        layoutManager.spanSizeLookup=object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position.rem(5)<2){
                    return 3
                }else{
                    return 2
                }
            }
        }
        rv_wall.layoutManager=layoutManager
        mAdapter.mData=wallPhotoList?: mutableListOf()
        rv_wall.adapter=mAdapter
    }

    private fun initListener(){
        ll_go_publish2.setOnClickListener {
            doPublish()
        }
        ll_go_publish.setOnClickListener {
            doPublish()
        }
        ll_back.setOnClickListener {
            finish()
        }
        ll_edit.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.MEMBER_INFO]=GsonUtil.toJson(memberInfo!!)
            params[RunIntentKey.WALL_PHOTO_LIST]=GsonUtil.toJson(wallPhotoList?: mutableListOf<WallPhotoBean>())
            RouterManager.startActivityForResultWithParams(BundleUrl.PHOTO_WALL_EDIT_ACTIVITY,this,params,10086)
        }
        tv_go_chat.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.TO_USER_ID]=memberInfo?.id.toString()
            params[RunIntentKey.TO_USER_NAME]=memberInfo?.nickName
            RouterManager.startActivityWithParams(BundleUrl.CHAT_ACTIVITY,this,params)
        }
        tv_go_chat_2.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.TO_USER_ID]=memberInfo?.id.toString()
            params[RunIntentKey.TO_USER_NAME]=memberInfo?.nickName
            RouterManager.startActivityWithParams(BundleUrl.CHAT_ACTIVITY,this,params)
        }

        tv_do_attention.setOnClickListener {
            mViewModel.doAttention(memberInfo?.id.toString())
            memberInfo?.let {info->
                if (info.isFollow==1){
                    info.isFollow=0
                }else{
                    info.isFollow=1
                }
                showFunc()
            }
        }
    }

    private fun doPublish(){
        RunCacheDataUtil.cleanHtCache()
        RouterManager.startActivityWithParams(BundleUrl.PHOTO_PICKER_ACTIVITY,this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==10086&&resultCode== Activity.RESULT_OK){
            val result=Gson().fromJson<MutableList<WallPhotoBean>>(data?.getStringExtra(RunIntentKey.WALL_PHOTO_LIST),object : TypeToken<MutableList<WallPhotoBean>>() {}.type)
            mAdapter.mData=result?: mutableListOf()
            mAdapter.notifyDataSetChanged()
        }
    }
}
