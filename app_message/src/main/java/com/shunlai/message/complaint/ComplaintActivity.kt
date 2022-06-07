package com.shunlai.message.complaint

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.PathItem
import com.shunlai.common.utils.*
import com.shunlai.message.Constant
import com.shunlai.message.MessageViewModel
import com.shunlai.message.R
import com.shunlai.message.complaint.adapter.ImageAdapter
import com.shunlai.message.entity.req.ComplainReq
import com.shunlai.router.RouterManager
import com.shunlai.ui.ListenerScrollView
import com.shunlai.ui.MediaGridInset
import com.shunlai.ui.moveRv.MoveCallBack
import kotlinx.android.synthetic.main.activity_complain_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*
import java.io.File

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class ComplaintActivity:BaseActivity(), ImageAdapter.OnHandleViewListener  {
    override fun getMainContentResId(): Int= R.layout.activity_complain_layout

    override fun getToolBarResID(): Int =R.layout.public_title_layout

    override fun setTitleColor(): Int=R.color.message_white

    private val mAdapter by lazy {
        ImageAdapter(mContext, mutableListOf(),rv_complain,this)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MessageViewModel::class.java)
    }

    private val ugcId by lazy {
        intent.getStringExtra(RunIntentKey.UGC_ID)?:""
    }

    override fun afterView() {
        initTitle()
        initListener()
        initRv()
        initViewModel()
        updateBtState()
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
    }

    private fun initListener(){
        tv_submit.setOnClickListener {
            doComplain()
        }
        et_complain_content.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateBtState()
                tv_count_size.text="${200-(s?.length?:0)}"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        et_complain_content.setOnEditorActionListener { _, actionId, _ ->
            if (actionId== EditorInfo.IME_ACTION_SEND){
                doComplain()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        sl_view.setTopScrollListener(object : ListenerScrollView.TopScrollListener {
            override fun onScroll(top: Int) {
                if (top > ScreenUtils.dip2px(mContext, 40f)) {
                    tv_title_content.text = "投诉"
                } else {
                    tv_title_content.text = ""
                }
            }
        })
    }

    private fun initRv(){
        rv_complain.layoutManager= GridLayoutManager(mContext,3)
        rv_complain.addItemDecoration(MediaGridInset(3,  ScreenUtils.dip2px(mContext,16f), false,true))
        rv_complain.adapter=mAdapter
        val helper= ItemTouchHelper(MoveCallBack(mAdapter))
        helper.attachToRecyclerView(rv_complain)
    }


    private var sb=StringBuffer()
    private fun initViewModel(){
        mViewModel.uploadResp.observe(this, Observer {
            if (it.isSuccess){
                if (sb.isEmpty()){
                    sb.append(it.url)
                }else{
                    sb.append(",${it.url}")
                }
            }
            currentUpload++
            if (currentUpload>=mAdapter.mData.size){
                val req=ComplainReq()
                req.content=et_complain_content.text.toString()
                req.image=sb.toString()
                req.ugcId=ugcId
                mViewModel.doComplain(req)
                sb=StringBuffer()
            }else{
                uploadFile(mAdapter.mData[currentUpload].path)
            }
        })
        mViewModel.doComplainResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                RouterManager.startActivityWithParams(BundleUrl.COMPLAIN_RESULT_ACTIVITY,this)
                finish()
            }else{
                toast(it.errorMsg)
            }
        })
    }

    private var currentUpload=0

    private fun doComplain(){
        if (TextUtils.isEmpty(et_complain_content.text.toString())){
            toast("投诉内容不能为空!")
            return
        }
        if (ugcId=="default"){
            RouterManager.startActivityWithParams(BundleUrl.COMPLAIN_RESULT_ACTIVITY,this)
            finish()
            return
        }

        currentUpload=0
        showBaseLoading()
        if (mAdapter.mData.size>0){
            uploadFile(mAdapter.mData[currentUpload].path)
        }else{
            val req=ComplainReq()
            req.ugcId=ugcId
            req.content=et_complain_content.text.toString()
            mViewModel.doComplain(req)
        }
    }

    private fun uploadFile(path: String){
        val file = File(PathUtils.getPath(mContext, Uri.parse(path)))
        mViewModel.uploadFile(FileUtil.luBanPicture(mContext, file))
    }

    private fun updateBtState(){
        if (TextUtils.isEmpty(et_complain_content.text.toString())){
            tv_submit.setBackgroundResource(R.drawable.gray_radius_24_bg)
            tv_submit.setTextColor(Color.parseColor("#888888"))
        }else{
            tv_submit.setBackgroundResource(R.drawable.black_radius_24_bg)
            tv_submit.setTextColor(ContextCompat.getColor(mContext,R.color.message_white))
        }
    }

    override fun removeItem(item: PathItem, position: Int) {
        mAdapter.mData.removeAt(position)
        mAdapter.notifyDataSetChanged()
        tv_complain_size.text="上传证据（${mAdapter.mData.size}）"
    }

    override fun onTakePhoto() {
        val params= mutableMapOf<String,Any?>()
        params[RunIntentKey.IS_ONLY_PICKER]=true
        params[RunIntentKey.LIMIT_SIZE]=9- mAdapter.mData.size
        RouterManager.startActivityForResultWithParams(BundleUrl.PHOTO_PICKER_ACTIVITY,this,params,
            Constant.OPEN_PICKER_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==Constant.OPEN_PICKER_REQUEST_CODE&&resultCode== Activity.RESULT_OK){
            val dates=data?.getStringExtra("choose_img_item_resource")
            dates?.let {
                val result=Gson().fromJson<List<PathItem>>(it,object : TypeToken<List<PathItem>>() {}.type)
                mAdapter.mData.addAll(result)
                mAdapter.notifyDataSetChanged()
                tv_complain_size.text="上传证据（${mAdapter.mData.size}）"
            }
        }
    }
}
