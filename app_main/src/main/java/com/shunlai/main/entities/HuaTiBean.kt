package com.shunlai.main.entities

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Liu
 * @Date   2021/5/10
 * @mobile 18711832023
 */
class HuaTiBean() : Parcelable {
    var id:String?=""
    var tag:String?=""
    var homeImgUrl:String?=""
    var listImgUrl:String?=""
    var bannerImgUrl:String?=""
    var status:String?=""
    var isRecommend:String?=""
    var recommendOrder:String?=""
    var pnum:String?=null
    var activity:Boolean?=false

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        tag = parcel.readString()
        homeImgUrl = parcel.readString()
        listImgUrl = parcel.readString()
        bannerImgUrl = parcel.readString()
        status = parcel.readString()
        isRecommend = parcel.readString()
        recommendOrder = parcel.readString()
        pnum = parcel.readString()
        activity = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(tag)
        parcel.writeString(homeImgUrl)
        parcel.writeString(listImgUrl)
        parcel.writeString(bannerImgUrl)
        parcel.writeString(status)
        parcel.writeString(isRecommend)
        parcel.writeString(recommendOrder)
        parcel.writeString(pnum)
        parcel.writeValue(activity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HuaTiBean> {
        override fun createFromParcel(parcel: Parcel): HuaTiBean {
            return HuaTiBean(parcel)
        }

        override fun newArray(size: Int): Array<HuaTiBean?> {
            return arrayOfNulls(size)
        }
    }
}
