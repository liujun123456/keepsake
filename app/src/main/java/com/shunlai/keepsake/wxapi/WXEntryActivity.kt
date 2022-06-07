package com.shunlai.keepsake.wxapi

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.shunlai.common.BaseApplication
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.toast
import com.shunlai.share.WeChatUtil
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.json.JSONObject


/**
 * @author Liu
 * @Date   2019-09-06
 * @mobile 18711832023
 * @desc 使用时请把该文件和所在目录放在对于项目包名下
 */
class WXEntryActivity:Activity(), IWXAPIEventHandler {

    private var mIWXAPI: IWXAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIWXAPI = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_APPID, true)
        mIWXAPI?.registerApp(Constant.WEIXIN_APPID)
        mIWXAPI?.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResp(resp: BaseResp) {
        if (resp.type== ConstantsAPI.COMMAND_SENDAUTH){
            resp as SendAuth.Resp
            if (resp.errCode==0){
                WeChatUtil.getInstance().notifyLoginResult(true,resp.code,null)
            }else if (resp.errCode==-2){
                WeChatUtil.getInstance().notifyLoginResult(false,null,"用户取消登录")
            }else{
                WeChatUtil.getInstance().notifyLoginResult(false,null,"用户拒绝授权")
            }
        }
        finish()

    }

    override fun onReq(req: BaseReq?) {
        if (req?.type == ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX&&req is ShowMessageFromWX.Req){
            val extInfo =req.message?.messageExt
            if (!TextUtils.isEmpty(extInfo)){
                val intent = Intent()
                intent.component = ComponentName(
                    BaseApplication.mInstance.packageName,
                    BundleUrl.SPLASH_ACTIVITY
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(buildUriData(extInfo!!))
                BaseApplication.mInstance.startActivity(intent)

            }else{
                toast("跳转路径不存在!")
            }
        }
        finish()
    }

    private fun buildUriData(data: String): String? {
        var uriData = "android://$packageName/open?"
        try {
            val jsonObject = JSONObject(data)
            val path = jsonObject.optString("path")
            uriData = uriData + "path=" + path+ "&"
            val params = jsonObject.optJSONObject("params").toString()
            uriData = uriData + "params=" + params
        } catch (e: Exception) {
        }
        return uriData
    }
}
