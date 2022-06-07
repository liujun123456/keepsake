package com.shunlai.mine.login

import com.shunlai.mine.entity.resp.LoginResp

/**
 * @author Liu
 * @Date   2021/4/26
 * @mobile 18711832023
 */
interface LoginView {
    fun showLoading(value:String)
    fun hideLoading()
    fun sendSmsResult(boolean: Boolean)
    fun updateDownTime(int:Int,isEnd:Boolean)
    fun loginSuccess(resp: LoginResp)
    fun loginFailed(msg:String)
}
