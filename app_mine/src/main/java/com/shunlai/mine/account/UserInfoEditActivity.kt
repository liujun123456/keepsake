package com.shunlai.mine.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.common.utils.toast
import com.shunlai.mine.MineViewModel
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.mine.entity.bean.UserLabel
import com.shunlai.net.util.GsonUtil
import kotlinx.android.synthetic.main.activity_edit_user_info_layout.*
import kotlinx.android.synthetic.main.public_title_layout.*
import java.lang.Exception

/**
 * @author Liu
 * @Date   2021/5/25
 * @mobile 18711832023
 */
@SuppressLint("SetTextI18n")
class UserInfoEditActivity:BaseActivity() {
    override fun getMainContentResId(): Int = R.layout.activity_edit_user_info_layout

    override fun getToolBarResID(): Int=R.layout.public_title_layout

    private val type by lazy {
        intent.getIntExtra(RunIntentKey.TYPE,0)  //0修改昵称 1修改简介 2修改标签
    }

    private var memberInfo:MemberInfo?=null

    private val mViewModel by lazy {
        ViewModelProvider(this).get(MineViewModel::class.java)
    }

    private val chooseLabelIndex= mutableListOf<Int>()

    private var userLabel= mutableListOf<UserLabel>()

    override fun afterView() {
        try {
            memberInfo=GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)
        }catch (e:Exception){}
        if (memberInfo==null){
            AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false).setMessage("用户信息异常")
                .setPositiveButton("确认") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }.show()
            return
        }
        initTitle()
        initListener()
        initData()
        initViewModel()
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
    }

    private fun initData(){
        if (type==0){
            et_content.visibility=View.VISIBLE
            et_desc.visibility=View.GONE
            tv_title.text="昵称"
            tv_input_label.text = "0/15"
            if (!TextUtils.isEmpty(memberInfo?.nickName)){
                et_content.setText(memberInfo?.nickName)
                if (memberInfo?.nickName?.length?:0>15){
                    et_content.setSelection(14)
                }else{
                    et_content.setSelection(memberInfo?.nickName!!.length-1)
                }
            }
            tv_commit.visibility=View.VISIBLE
        }else if (type==1){
            et_content.visibility=View.GONE
            et_desc.visibility=View.VISIBLE
            tv_title.text="简介"
            tv_input_label.text = "0/30"
            if (!TextUtils.isEmpty(memberInfo?.introduce)){
                et_desc.setText(memberInfo?.introduce)
                if (memberInfo?.introduce?.length?:0>30){
                    et_desc.setSelection(29)
                }else{
                    et_desc.setSelection(memberInfo?.introduce!!.length)
                }
            }
            tv_commit.visibility=View.VISIBLE
        }else{
            rl_nick_name.visibility= View.GONE
            tv_title.text="社区徽章"
            mViewModel.queryUserLabel("1")
        }
    }

    private fun initListener(){
        et_content.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                tv_input_label.text = "${s?.length ?: 0}/15"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        et_desc.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                tv_input_label.text = "${s?.length ?: 0}/30"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        tv_commit.setOnClickListener {
            if (type==0){
                updateNickName()
            }else if (type==1){
                updateDesc()
            }else{
                updateLabel()
            }
        }
    }

    private fun updateNickName(){
        if (TextUtils.isEmpty(et_content.text.toString())){
            toast("请输入昵称")
            return
        }
        showBaseLoading()
        mViewModel.updateUserInfo(null,null,et_content.text.toString())
    }

    private fun updateDesc(){
        showBaseLoading()
        mViewModel.updateUserInfo(null,et_desc.text.toString(),null)
    }

    private fun updateLabel(){
        showBaseLoading()
        memberInfo?.labelList?.clear()
        chooseLabelIndex.forEach {
            memberInfo?.labelList?.add(userLabel[it])
        }
        mViewModel.updateUserLabel(memberInfo?.id.toString(),memberInfo?.labelList?: mutableListOf())
    }

    private fun initViewModel(){
        mViewModel.updateUserResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                toast("修改成功")
                dealResult()
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.updateLabelResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess){
                toast("修改成功")
                dealResult()
            }else{
                toast(it.errorMsg)
            }
        })
        mViewModel.labelListResp.observe(this, Observer {
            buildLabel(it)
        })
    }

    private fun buildLabel(data:MutableList<UserLabel>){
        userLabel=data
        if (!userLabel.isNullOrEmpty()){
            label_flex.visibility=View.VISIBLE
            tv_commit.visibility=View.VISIBLE
            data.forEachIndexed { index, label ->
                val view=TextView(mContext)
                view.setPadding(ScreenUtils.dip2px(mContext,16f),ScreenUtils.dip2px(mContext,8f),
                    ScreenUtils.dip2px(mContext,16f),ScreenUtils.dip2px(mContext,8f))
                view.setBackgroundResource(R.drawable.gray_18_radius_bg)
                view.text = label.content
                view.setTextColor(Color.parseColor("#181818"))
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                view.setOnClickListener {
                    if (chooseLabelIndex.contains(index)){
                        chooseLabelIndex.remove(index)
                    }else{
                        if (chooseLabelIndex.size>=2){
                            toast("最多只可以选两个标签")
                            return@setOnClickListener
                        }
                        chooseLabelIndex.add(index)
                    }
                    buildLabelChoose()
                }
                memberInfo?.labelList?.forEach {
                    if (it.labelId==label.labelId){
                        chooseLabelIndex.add(index)
                    }
                }
                label_flex.addView(view)
            }
            buildLabelChoose()
        }else{
            ll_empty_label.visibility=View.VISIBLE
        }
    }

    private fun buildLabelChoose(){
        for (i in 0.until(label_flex.childCount)){
            if (chooseLabelIndex.contains(i)){
                (label_flex.getChildAt(i) as TextView).setTextColor(Color.parseColor("#ffffff"))
                (label_flex.getChildAt(i) as TextView).setBackgroundResource(R.drawable.black_18_radius_bg)
            }else{
                (label_flex.getChildAt(i) as TextView).setTextColor(Color.parseColor("#181818"))
                (label_flex.getChildAt(i) as TextView).setBackgroundResource(R.drawable.gray_18_radius_bg)
            }
        }
    }
    private fun dealResult(){
        val intent=Intent()
        if (type==0){
            memberInfo?.nickName=et_content.text.toString()
        }else if (type==1){
            memberInfo?.introduce=et_desc.text.toString()
        }
        intent.putExtra(RunIntentKey.MEMBER_INFO,GsonUtil.toJson(memberInfo!!))
        setResult(Activity.RESULT_OK,intent)
        finish()
    }
}
