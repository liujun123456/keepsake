package com.shulai.third

import android.app.Application
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import com.alibaba.baichuan.android.trade.AlibcTrade
import com.alibaba.baichuan.android.trade.AlibcTradeSDK
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback
import com.alibaba.baichuan.android.trade.model.AlibcShowParams
import com.alibaba.baichuan.android.trade.model.OpenType
import com.alibaba.baichuan.android.trade.page.AlibcBasePage
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage
import com.alibaba.baichuan.trade.biz.applink.adapter.AlibcFailModeType
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams
import com.kepler.jd.login.KeplerApiManager
import com.kepler.jd.sdk.bean.KeplerAttachParameter
import com.shunlai.common.utils.toast

/**
 * @author Liu
 * @Date   2021/5/27
 * @mobile 18711832023
 */
object ThirdManager {

    fun init(context: Application) {
//        KeplerApiManager.asyncInitSdk(context, "ff1c09dcf821913a20e045ede8551575",
//            "d4713f4799cc4715849cc490ffb31ecd", object : AsyncInitListener {
//                override fun onSuccess() {
//
//                }
//
//                override fun onFailure() {
//
//                }
//            }
//
//        )
        AlibcTradeSDK.asyncInit(context,object : AlibcTradeInitCallback{
            override fun onSuccess() {
            }

            override fun onFailure(code: Int, msg: String?) {
            }

        })
    }

    fun openJdApp(url: String, ctx: Context) {
        KeplerApiManager.getWebViewService().openAppWebViewPage(ctx, url, KeplerAttachParameter()) { status, url ->

        }
    }

    fun openTbApp(url:String, ctx: Context){
        val showParams = AlibcShowParams()
        showParams.openType = OpenType.Auto
        showParams.clientType = "taobao"
        showParams.backUrl = ""
        showParams.nativeOpenFailedMode = AlibcFailModeType.AlibcNativeFailModeJumpDOWNLOAD

        val taokeParams = AlibcTaokeParams("", "", "")
        taokeParams.setPid("mm_1634130158_2375700106_111497600335")


        AlibcTrade.openByUrl(ctx as FragmentActivity,"",url,null, WebViewClient(),
            WebChromeClient(),showParams,taokeParams, mutableMapOf<String,String>(),object : AlibcTradeCallback{
                override fun onFailure(p0: Int, p1: String?) {

                }

                override fun onTradeSuccess(p0: AlibcTradeResult?) {

                }
            }

        )
    }

    fun openTbAppById(productId:String, ctx: Context){
        val showParams = AlibcShowParams()
        showParams.openType = OpenType.Auto
        showParams.clientType = "taobao"
        showParams.backUrl = "taopen32882050"
        showParams.nativeOpenFailedMode = AlibcFailModeType.AlibcNativeFailModeJumpDOWNLOAD


        val taokeParams = AlibcTaokeParams("", "", "")
        taokeParams.setPid("mm_1634130158_2375700106_111497600335")
        taokeParams.adzoneid="111497600335"

        val extraParams= mutableMapOf<String,String>()
        extraParams["taokeyAppkey"]="32882050"
        taokeParams.extraParams=extraParams

        val shopPage: AlibcBasePage = AlibcDetailPage(productId)



        AlibcTrade.openByBizCode(ctx as FragmentActivity,shopPage,null, WebViewClient(),
            WebChromeClient(),"detail",showParams,taokeParams, mutableMapOf(),object : AlibcTradeCallback{
                override fun onFailure(p0: Int, p1: String?) {
                    toast(p1?:"跳转失败")
                }

                override fun onTradeSuccess(p0: AlibcTradeResult?) {

                }
            })
    }

}
