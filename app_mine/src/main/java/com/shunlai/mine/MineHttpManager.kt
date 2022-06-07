package com.shunlai.mine

import com.shunlai.net.CoreHttpConfig
import com.shunlai.net.CoreHttpManager

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
object MineHttpManager: CoreHttpManager()  {
    override fun getConfig(): CoreHttpConfig =LoginHttpConfig()

    class LoginHttpConfig:CoreHttpConfig(){
        override fun getBaseUrl(): String =MineApiConfig.BASE_URL
    }
}
