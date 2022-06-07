package com.shunlai.mine.photowall

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.common.utils.toast
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.mine.entity.bean.WallPhotoBean
import com.shunlai.mine.photowall.adapter.EditPhotoWallAdapter
import com.shunlai.net.util.GsonUtil
import com.shunlai.ui.MediaGridInset
import kotlinx.android.synthetic.main.activity_photo_wall_edit_layout.*

/**
 * @author Liu
 * @Date   2021/5/18
 * @mobile 18711832023
 */
class EditPhotoWallActivity:BaseActivity() {
    override fun getMainContentResId(): Int= R.layout.activity_photo_wall_edit_layout

    override fun getToolBarResID(): Int=0

    private var wallPhotoList:MutableList<WallPhotoBean>?= mutableListOf()

    private var memberInfo: MemberInfo?=null

    private val mAdapter by lazy {
        EditPhotoWallAdapter(mContext, mutableListOf(),rv_wall_edit,wallPhotoList?: mutableListOf())
    }

    private val mVieModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    override fun afterView() {
        memberInfo= GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)
        mVieModel.queryAllWallPage(memberInfo?.id.toString())
        initViewModel()
        initListener()
        initRv()
    }

    private fun initRv(){
        rv_wall_edit.layoutManager= GridLayoutManager(mContext,3)
        rv_wall_edit.addItemDecoration(MediaGridInset(3, ScreenUtils.dip2px(mContext,4f), false,true))
        rv_wall_edit.adapter = mAdapter
    }

    private fun initListener(){
        ll_back.setOnClickListener {
            finish()
        }
        ll_edit.setOnClickListener {
            if (mAdapter.chooseData.size<5){
                toast("最少要选中5张图片哦")
                return@setOnClickListener
            }
            showBaseLoading()
            mVieModel.editWallPage(mAdapter.chooseData)
        }
    }

    private fun initViewModel(){
        mVieModel.allPhotoWallList.observe(this, Observer {
            if (it.isNullOrEmpty()){
                iv_empty.visibility= View.VISIBLE
                tv_empty.visibility= View.VISIBLE
            }else{
                mAdapter.mData=it
                mAdapter.notifyDataSetChanged()
            }
        })
        mVieModel.editPhotoWallResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                val intent=Intent()
                intent.putExtra(RunIntentKey.WALL_PHOTO_LIST,GsonUtil.toJson(mAdapter.chooseData))
                setResult(Activity.RESULT_OK,intent)
                finish()
            }else{
                toast(it.errorMsg)
            }
        })
    }

}
