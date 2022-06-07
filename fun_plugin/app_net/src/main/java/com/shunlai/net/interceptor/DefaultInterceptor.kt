package com.shunlai.net.interceptor

import android.content.ComponentName
import android.content.Intent
import android.os.Handler
import com.shunlai.common.BaseApplication
import com.shunlai.common.utils.*
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import org.json.JSONObject
import java.io.EOFException
import java.nio.charset.Charset


/**
 * @author Liu
 * @Date   2021/3/22
 * @mobile 18711832023
 */
class DefaultInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
        req.addHeader("token", PreferenceUtil.getString(Constant.TOKEN)?:"")
        req.addHeader("deviceId", SensorsUtil.getAndroidID(BaseApplication.mInstance)?:"")
        req.addHeader("mac", SensorsUtil.getMacAddress(BaseApplication.mInstance)?:"")
        req.addHeader("imei", SensorsUtil.getIMEI(BaseApplication.mInstance)?:"")
        val resp=chain.proceed(req.build())
        getResponseData(resp.body)
        return resp
    }

    private val handler=Handler()

    private val mCharset: Charset = Charset.forName("UTF-8")

    private fun getResponseData(responseBody: ResponseBody?){
        responseBody?.let {
            try {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE)
                val buffer: Buffer = source.buffer()
                var charset: Charset? = mCharset
                val contentType: MediaType? = responseBody.contentType()
                contentType?.let {
                    charset = contentType.charset(mCharset)
                }
                if (!isPlaintext(buffer)) {
                    return
                }
                if (responseBody.contentLength() !== 0L) {
                    val result=buffer.clone().readString(charset!!)
                    val jsonObject=JSONObject(result)
                    val code=jsonObject.getInt("code")
                    val msg=jsonObject.getString("msg")
                    if (code==400){
                        handler.post {
                            toast("token已过期")
                            val intent= Intent()
                            intent.component = ComponentName(BaseApplication.mInstance.packageName,BundleUrl.LOGIN_ACTIVITY)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            BaseApplication.mInstance.startActivity(intent)
                        }
                    }else if (code==402){
                        handler.post {
                            val intent= Intent()
                            intent.component = ComponentName(BaseApplication.mInstance.packageName,BundleUrl.LOGIN_ACTIVITY)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra("state",code)
                            intent.putExtra("stateMsg",msg)
                            BaseApplication.mInstance.startActivity(intent)
                        }
                    }
                }
            }catch (e:Exception){}
        }

    }

    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64.toLong()
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false
        }
    }
}
