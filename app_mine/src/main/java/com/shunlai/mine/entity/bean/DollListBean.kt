package com.shunlai.mine.entity.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Liu
 * @Date   2021/7/21
 * @mobile 18711832023
 */
class DollListBean() : Parcelable {
    var modelId:String?=""
    var name:String?=""
    var iconUrl:String?=""
    var tokenPrice:String?=""
    var buyFlag:String?=""
    var selectedFlag:String?=""
    var version:String?=""

    constructor(parcel: Parcel) : this() {
        modelId = parcel.readString()
        name = parcel.readString()
        iconUrl = parcel.readString()
        tokenPrice = parcel.readString()
        buyFlag = parcel.readString()
        selectedFlag = parcel.readString()
        version = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(modelId)
        parcel.writeString(name)
        parcel.writeString(iconUrl)
        parcel.writeString(tokenPrice)
        parcel.writeString(buyFlag)
        parcel.writeString(selectedFlag)
        parcel.writeString(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DollListBean> {
        override fun createFromParcel(parcel: Parcel): DollListBean {
            return DollListBean(parcel)
        }

        override fun newArray(size: Int): Array<DollListBean?> {
            return arrayOfNulls(size)
        }
    }
}
