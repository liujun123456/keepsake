package com.shunlai.keepsake.splash

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.keepsake.R
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.splash_notice_layout.*

class SplashNoticeDialog(var mContext: Context, var themeResId:Int,var mListener:SplashNoticeListener): Dialog(mContext,themeResId)  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.splash_notice_layout)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        initNotice()
        tv_agree.setOnClickListener {
            dismiss()
            mListener.isAgree(true)
        }
        tv_un_agree.setOnClickListener {
            dismiss()
            mListener.isAgree(false)
        }

    }

    private fun initNotice(){
        val sStr= SpannableString("在你使用TOKEN APP前，请认真阅读并了解《用户协议》和《隐私政策》。点击同意即表示你已阅读并同意全部条款。")
        sStr.setSpan(ForegroundColorSpan(Color.parseColor("#0d0d0d")), 23, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sStr.setSpan(StyleSpan(Typeface.BOLD), 23, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sStr.setSpan(object : ClickableSpan(){
            override fun onClick(widget: View) {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.WEB_URL]="https://chengbai-tech.com/static/app/document/agreement.html"
                RouterManager.startActivityWithParams(BundleUrl.LIGHT_APP_ACTIVITY,mContext as FragmentActivity,params)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }

        }, 23, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sStr.setSpan(ForegroundColorSpan(Color.parseColor("#0d0d0d")), 30, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sStr.setSpan(StyleSpan(Typeface.BOLD), 30, 36,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sStr.setSpan(object : ClickableSpan(){
            override fun onClick(widget: View) {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.WEB_URL]="https://chengbai-tech.com/static/app/document/privacy-policy.html"
                RouterManager.startActivityWithParams(BundleUrl.LIGHT_APP_ACTIVITY,mContext as FragmentActivity,params)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }

        }, 30, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_splash_notice.movementMethod = LinkMovementMethod.getInstance()
        tv_splash_notice.text = sStr

    }

    interface SplashNoticeListener{
        fun isAgree(bol:Boolean)
    }
}
