package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.BaseResp
import com.shunlai.mine.entity.bean.ShopBgBean
import com.shunlai.mine.entity.bean.ShopDollBean

/**
 * @author Liu
 * @Date   2021/7/20
 * @mobile 18711832023
 */
class ResourceResp:BaseResp() {
    var principalModelDTOList:MutableList<ShopDollBean> = mutableListOf()
    var principalSceneDTOList:MutableList<ShopBgBean> = mutableListOf()
}
