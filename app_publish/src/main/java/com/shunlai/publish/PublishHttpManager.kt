package com.shunlai.publish

import com.shunlai.net.CoreHttpConfig
import com.shunlai.net.CoreHttpManager

/**
 * @author Liu
 * @Date   2021/4/13
 * @mobile 18711832023
 */
object PublishHttpManager:CoreHttpManager() {
    override fun getConfig(): CoreHttpConfig =PublishHttpConfig()


    class PublishHttpConfig:CoreHttpConfig(){
        override fun getBaseUrl(): String =PublishApiConfig.BASE_URL
    }
}
