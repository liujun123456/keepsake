package com.shunlai.ugc.entity

import com.stx.xhb.androidx.entity.BaseBannerInfo

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class BannerInfo(var imageUrl:String):BaseBannerInfo {


    override fun getXBannerUrl(): String=imageUrl

    override fun getXBannerTitle(): String?=null
}
