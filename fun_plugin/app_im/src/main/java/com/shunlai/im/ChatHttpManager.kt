package com.shunlai.im

import com.shunlai.net.CoreHttpConfig
import com.shunlai.net.CoreHttpManager

/**
 * @author Liu
 * @Date   2021/7/24
 * @mobile 18711832023
 */
object ChatHttpManager: CoreHttpManager()  {
    override fun getConfig(): CoreHttpConfig =ChatHttpConfig()

    class ChatHttpConfig: CoreHttpConfig(){
        override fun getBaseUrl(): String =ChatApiConfig.BASE_URL
    }
}
