package com.shunlai.location

import com.shunlai.net.CoreHttpConfig
import com.shunlai.net.CoreHttpManager

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
object LocationHttpManager: CoreHttpManager() {
    override fun getConfig(): CoreHttpConfig =LocationHttpConfig()

    class LocationHttpConfig:CoreHttpConfig(){
        override fun getBaseUrl(): String =LocationApiConfig.BASE_URL
    }
}
