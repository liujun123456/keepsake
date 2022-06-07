package com.shunlai.mine.light

import android.os.Build
import android.webkit.*
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.FileUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.mine.R
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.activity_light_layout.*
import kotlinx.android.synthetic.main.title_order_sign_layout.*

/**
 * @author Liu
 * @Date   2021/5/24
 * @mobile 18711832023
 */
class LightAppActivity:BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_light_layout

    override fun getToolBarResID(): Int= R.layout.title_order_sign_layout

    private val title by lazy {
        intent.getStringExtra(RunIntentKey.WEB_TITLE)
    }
    private val url by lazy {
        intent.getStringExtra(RunIntentKey.WEB_URL)
    }

    override fun afterView() {
        initTitle()
        initWeb()
        web_view.loadUrl(url)
    }

    private fun initWeb(){
        web_view.settings?.let {setting->
            setting.javaScriptEnabled=true
            setting.builtInZoomControls = true
            setting.useWideViewPort = true
            setting.loadWithOverviewMode = true
            setting.setSupportZoom(false)
            setting.domStorageEnabled = true
            setting.databaseEnabled = true
            setting.setAppCachePath(FileUtil.getCacheFileRootPath(mContext)+"/webCache")
            setting.setAppCacheEnabled(true)
            setting.allowContentAccess = true
        }
        web_view.webChromeClient=object :WebChromeClient(){
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                tv_title_content.text=title
            }
        }
        web_view.webViewClient=object :WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url:String
                if (Build.VERSION.SDK_INT >= 21){
                    url = request?.url?.toString() ?: ""
                }else{
                    url = request.toString()
                }
                if (url.startsWith("shunfeng://usercenter")){
                    val data=url.split("?")
                    if (data.size==2){
                        val params= mutableMapOf<String,Any?>()
                        params[RunIntentKey.MEMBER_ID]=data[1]
                        RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
                    }
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
    }

    private fun initTitle(){
        tv_title_content.text=title
        if (title=="Token分"){
            tv_right_content.text="分数记录"
            tv_right_content.setOnClickListener {
                RouterManager.startActivityWithParams(BundleUrl.TOKEN_LIST_ACTIVITY,this)
            }
        }
        ll_back.setOnClickListener {
            finish()
        }
    }
}
