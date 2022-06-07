package com.shunlai.ugc

import com.shunlai.net.CoreHttpConfig
import com.shunlai.net.CoreHttpManager

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
object UgcHttpManager: CoreHttpManager()  {
    override fun getConfig(): CoreHttpConfig =UgcHttpConfig()

    class UgcHttpConfig:CoreHttpConfig(){
        override fun getBaseUrl(): String =UgcApiConfig.BASE_URL
    }
}
