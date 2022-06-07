package com.shunlai.im

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.PathItem
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.toast
import com.shunlai.im.adapter.ChatAdapter
import com.shunlai.im.face.Emoji
import com.shunlai.im.face.FaceFragment
import com.shunlai.im.face.FaceManager
import com.shunlai.im.utils.ChatActionWindow
import com.shunlai.im.utils.Constant
import com.shunlai.im.video.CameraActivity
import com.shunlai.im.video.listener.IUIKitCallBack
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable
import com.shunlai.router.RouterManager
import com.shunlai.ui.UgcActionWindow
import kotlinx.android.synthetic.main.activity_chat_layout.*
import kotlinx.android.synthetic.main.im_public_title_layout.*
import java.io.File

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
class ChatActivity:BaseActivity(), FaceFragment.OnEmojiClickListener,ChatView,ChatActionWindow.ActionListener  {
    override fun getMainContentResId(): Int =R.layout.activity_chat_layout

    override fun getToolBarResID(): Int=R.layout.im_public_title_layout

    override fun setTitleColor(): Int=R.color.chat_white

    private val mPresenter by lazy {
        ChatPresenter(this,this)
    }

    private val mAdapter by lazy {
        ChatAdapter(mContext,mPresenter.msgList)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }

    private val chatWindow by lazy {
        ChatActionWindow(mContext,this)
    }

    private val minHeight = 50
    private var oldBottom = 0
    private var newBottom = 0
    private val rect = Rect()
    private val handler= @SuppressLint("HandlerLeak")
    object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    override fun afterView() {
        initListener()
        initFace()
        initTitle()
        initRv()
        mPresenter.loadMsg()
    }

    private fun initTitle(){
        findViewById<LinearLayout>(R.id.ll_back)
        ll_back.setOnClickListener {
            finish()
        }
        tv_title_content.text=mPresenter.toUserName

        iv_more.setOnClickListener {
            chatWindow.showWindow()
        }
    }

    private fun initFace(){
        val fragment=FaceFragment()
        fragment.setListener(this)
        supportFragmentManager.beginTransaction().replace(R.id.emoji_layout,fragment).commitAllowingStateLoss()
    }

    private fun initRv(){
        rv_message.layoutManager=LinearLayoutManager(mContext)
        rv_message.adapter=mAdapter
        if (mPresenter.chatGoods==null||mPresenter.chatGoods?.img==null){
            ll_send_goods.visibility=View.GONE
        }else{
            ll_send_goods.visibility=View.VISIBLE
            ImageUtil.showRoundImgWithStringAndRadius(iv_goods,mContext,mPresenter.chatGoods?.img?:"",10f)
            ll_send_goods.setOnClickListener {
                mPresenter.sendGoods()
                ll_send_goods.visibility=View.GONE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener(){
        main_layout.viewTreeObserver.addOnGlobalLayoutListener {
            updateWindowHeight()
        }
        iv_show_emoji.setOnClickListener {
            hideInput(et_msg_input)
            handler.postDelayed({
                updateEmoji()
            },100)

        }
        iv_show_fun.setOnClickListener {
            hideInput(et_msg_input)
            handler.postDelayed({
                updateFunLayout()
            },100)

        }
        tv_send.setOnClickListener {
            mPresenter.sendTextMsg(et_msg_input.text.toString())
            et_msg_input.setText("")
        }

        iv_open_camera.setOnClickListener {
            checkPermission()
        }

        iv_choose_image.setOnClickListener {
            choosePhoto()
        }

        chat_swipe_layout.setOnRefreshListener {
            mPresenter.loadHistory()
        }

        initInputListener()
    }


    private fun choosePhoto(){
        val params= mutableMapOf<String,Any?>()
        params[RunIntentKey.IS_ONLY_PICKER]=true
        params[RunIntentKey.LIMIT_SIZE]=9
        params[RunIntentKey.IS_NEED_VIDEO]=true
        RouterManager.startActivityForResultWithParams(BundleUrl.PHOTO_PICKER_ACTIVITY,this,params,
            Constant.CHOOSE_PHOTO)
    }


    val PERMISSION_REQUEST_CODE = 10086

    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
            }else{
                openCamera()
            }
        }
    }

    private fun openCamera(){
        RouterManager.startActivityWithParams(BundleUrl.CAMERA_ACTIVITY,this)
        CameraActivity.mCallBack=object : IUIKitCallBack{

            override fun onSuccess(data: Any?, type: Int) {
                data?.let {
                    if (type==1){
                        mPresenter.sendImgMsg(Uri.fromFile(File(data.toString())))
                    }else{
                        val videoData = data as Intent
                        val imgPath =videoData.getStringExtra(RunIntentKey.CAMERA_IMG_PATH)
                        val videoPath =videoData.getStringExtra(RunIntentKey.CAMERA_VIDEO_PATH)
                        val duration =videoData.getLongExtra(Constant.VIDEO_TIME, 0)
                        mPresenter.sendVideoMsgWithPath(videoPath,duration,imgPath)
                    }
                }
            }

            override fun onError(module: String?, errCode: Int, errMsg: String?) {

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initInputListener(){
        et_msg_input.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateSendState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        et_msg_input.setOnTouchListener { _, _ ->
            if (fuc_layout.visibility==View.VISIBLE){
                fuc_layout.visibility=View.GONE
            }
            if (emoji_layout.visibility== View.VISIBLE){
                emoji_layout.visibility=View.GONE
                iv_show_emoji.setImageResource(R.mipmap.emoji_icon)
            }
            return@setOnTouchListener false
        }
    }

    private fun updateEmoji(){
        if (emoji_layout.visibility== View.VISIBLE){
            emoji_layout.visibility=View.GONE
            iv_show_emoji.setImageResource(R.mipmap.emoji_icon)
            et_msg_input.requestFocus()
            showInput(et_msg_input)
        }else{
            fuc_layout.visibility=View.GONE
            emoji_layout.visibility=View.VISIBLE
            iv_show_emoji.setImageResource(R.mipmap.keyborad_icon)
            mPresenter.scrollBottom()
        }
    }

    private fun updateFunLayout(){
        if (fuc_layout.visibility==View.VISIBLE){
            fuc_layout.visibility=View.GONE
            et_msg_input.requestFocus()
            showInput(et_msg_input)
        }else{
            emoji_layout.visibility=View.GONE
            iv_show_emoji.setImageResource(R.mipmap.emoji_icon)
            fuc_layout.visibility=View.VISIBLE
            mPresenter.scrollBottom()
        }
    }


    private fun updateSendState(){
        if (TextUtils.isEmpty(et_msg_input.text.toString())){
            tv_send.visibility=View.GONE
            iv_show_fun.visibility=View.VISIBLE
        }else{
            tv_send.visibility=View.VISIBLE
            iv_show_fun.visibility=View.GONE
        }
    }

    private var defaultMargin: Int = 0
    private fun updateWindowHeight() {
        oldBottom = rect.bottom
        main_layout.getWindowVisibleDisplayFrame(rect)
        newBottom = rect.bottom
        if (oldBottom - newBottom > minHeight) {
            val params = bottom_menu.layoutParams as RelativeLayout.LayoutParams
            defaultMargin += oldBottom - newBottom
            params.setMargins(0, 0, 0, defaultMargin)
            bottom_menu.layoutParams = params
            if (et_msg_input.isFocused) {
                mPresenter.scrollBottom()
            }
        } else if (newBottom - oldBottom > minHeight) {
            val params = bottom_menu.layoutParams as RelativeLayout.LayoutParams
            if (defaultMargin == newBottom - oldBottom) {
                params.setMargins(0, 0, 0, 0)
                bottom_menu.layoutParams = params
                defaultMargin = 0
            } else {
                if (defaultMargin > 0) {
                    defaultMargin -= newBottom - oldBottom
                    params.setMargins(0, 0, 0, defaultMargin)
                    bottom_menu.layoutParams = params
                }
            }
            if (et_msg_input.isFocused) {
                mPresenter.scrollBottom()
            }

        }
    }

    override fun onEmojiClick(emoji: Emoji) {
        val index: Int = et_msg_input.selectionStart
        val editable: Editable = et_msg_input.text
        editable.insert(index, emoji.filter)
        FaceManager.handlerEmojiText(et_msg_input, editable.toString(), true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }



    override fun scrollToPosition(position: Int) {
        if (!mAdapter.mData.isNullOrEmpty()) {
            if (position >= 0) {
                rv_message.scrollToPosition(position)
            }
        }
    }

    override fun showDates() {
        chat_swipe_layout.isRefreshing=false
        mAdapter.notifyDataSetChanged()
    }

    override fun notifyRange(start:Int,count:Int) {
        chat_swipe_layout.isRefreshing=false
        mAdapter.notifyItemRangeInserted(0,count)
    }

    override fun pushMessage(type: Int, message: String) {
        if (!TextUtils.isEmpty(mPresenter.toUserId)){
            mViewModel.pushMessage(mPresenter.toUserId,type,message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==Constant.CHOOSE_PHOTO&&resultCode== Activity.RESULT_OK){
            val dates=data?.getStringExtra("choose_img_item_resource")
            dates?.let {
                val result= Gson().fromJson<List<PathItem>>(it,object : TypeToken<List<PathItem>>() {}.type)
                result.forEach {data->
                    if (data.type==2){
                        mPresenter.sendVideoMsgWithUri(Uri.parse(data.path))
                    }else{
                        mPresenter.sendImgMsg(Uri.parse(data.path))
                    }

                }
            }
        }
    }

    override fun onComplaintAction() {
        val params= mutableMapOf<String,Any?>()
        params[RunIntentKey.UGC_ID]="default"    //todo  仅仅为了过审的权益之计
        RouterManager.startActivityWithParams(BundleUrl.COMPLAIN_ACTIVITY,this,params)
    }

    override fun onBlockAction() {
        AlertDialog.Builder(mContext).setTitle("提示").setMessage("确定要拉黑对方吗？").setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }.setPositiveButton("确认") { dialog, _ ->
            dialog.dismiss()
            mViewModel.blockUser(mPresenter.toUserId)
            toast("拉黑成功!")
        }.show()

    }

}
