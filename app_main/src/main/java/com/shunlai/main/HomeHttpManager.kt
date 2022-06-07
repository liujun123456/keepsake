package com.shunlai.main

import com.shunlai.net.CoreHttpConfig
import com.shunlai.net.CoreHttpManager

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
object HomeHttpManager: CoreHttpManager()  {
    override fun getConfig(): CoreHttpConfig =HomeHttpConfig()

    class HomeHttpConfig:CoreHttpConfig(){
        override fun getBaseUrl(): String =HomeApiConfig.BASE_URL
    }
}
