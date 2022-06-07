package com.shunlai.message

import com.shunlai.net.CoreHttpConfig
import com.shunlai.net.CoreHttpManager

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
object MessageHttpManager: CoreHttpManager()  {
    override fun getConfig(): CoreHttpConfig =MessageHttpConfig()

    class MessageHttpConfig:CoreHttpConfig(){
        override fun getBaseUrl(): String =MessageApiConfig.BASE_URL
    }
}
