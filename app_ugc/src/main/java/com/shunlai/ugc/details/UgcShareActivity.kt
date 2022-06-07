package com.shunlai.ugc.details

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BlurUtil
import com.shunlai.common.utils.EncodingHandler
import com.shunlai.common.utils.FileUtil
import com.shunlai.common.utils.ImageUtil
import com.shunlai.share.WeChatUtil
import com.shunlai.net.util.GsonUtil
import com.shunlai.ugc.R
import com.shunlai.ugc.UgcApiConfig
import com.shunlai.ugc.entity.UgcBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_ugc_share_layout.*
import java.lang.Exception

/**
 * @author Liu
 * @Date   2021/8/10
 * @mobile 18711832023
 */
@SuppressLint("CheckResult")
class UgcShareActivity:BaseActivity() {
    override fun getMainContentResId(): Int= R.layout.activity_ugc_share_layout

    override fun getToolBarResID(): Int=0

    private var ugcBean:UgcBean?=null

    override fun afterView() {
        try {
            ugcBean= GsonUtil.fromJson(intent.getStringExtra("ugcBean"),UgcBean::class.java)
        }catch (e:Exception){

        }
        if (ugcBean==null){
            AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false)
                .setMessage("笔记信息异常")
                .setPositiveButton("确认") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }.show()
            return
        }
        initData()
        initListener()
    }

    private fun initData(){
        tv_user_name.text=ugcBean?.nickName
        val spanString=SpannableString("“${ugcBean?.content}")
        spanString.setSpan(ForegroundColorSpan(Color.parseColor("#4D000000")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanString.setSpan(StyleSpan(Typeface.BOLD), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_ugc_content.text=spanString
        ImageUtil.showCircleImgWithString(iv_user_avatar,mContext,ugcBean?.avatar)
        if (ugcBean?.ugcType=="1"){
            iv_video_icon.visibility= View.INVISIBLE
        }
        showShareImg()

    }

    private fun showShareImg(){
        Observable.just(ugcBean?.imageList!![0]).map {
            return@map ImageUtil.getBitmapFromUrl(mContext,it)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe{
                ImageUtil.showBitmapWithRadius(iv_share_img,mContext,it,8f)
                ImageUtil.showBitmapWithRadius(iv_img_bg,mContext,BlurUtil.fastblur(mContext,it,20),0.0f)
            }
        val bitmap=EncodingHandler.createQRCode("${UgcApiConfig.ROOT_URL}/static/app/xwsapp/#/ugc?id=${ugcBean?.id}",iv_qr_code.width)
        iv_qr_code.setImageBitmap(bitmap)
    }

    private fun initListener(){
        tv_share_we_chat.setOnClickListener {
            val bitmap=FileUtil.createBitmapFromView(rl_content)
            WeChatUtil.getInstance().shareWeChatWithImg(bitmap)
        }
        tv_share_circle.setOnClickListener {
            val bitmap=FileUtil.createBitmapFromView(rl_content)
            WeChatUtil.getInstance().shareCircleWithImg(bitmap)

        }
        tv_save_local.setOnClickListener {
            val bitmap=FileUtil.createBitmapFromView(rl_content)
            FileUtil.saveBitmapToPhoto(mContext,bitmap)

        }
        tv_cancel_share.setOnClickListener {
            finish()
        }
    }
}
