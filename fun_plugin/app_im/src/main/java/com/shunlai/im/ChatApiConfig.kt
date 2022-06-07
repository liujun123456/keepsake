package com.shunlai.im

import com.shunlai.common.BaseApiConfig

/**
 * @author Liu
 * @Date   2021/7/24
 * @mobile 18711832023
 */
object ChatApiConfig: BaseApiConfig()  {
    const val BLOCK_USER="blacklist/add"  //拉黑用户

    const val CHECK_BLOCK_USER="blacklist/getStatus"  //查询用户拉黑状态

    const val PUSH_MESSAGE="member/im/pushMessage"  //查询用户拉黑状态
}
