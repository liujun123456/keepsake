package com.shunlai.mine.entity.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Liu
 * @Date   2021/7/21
 * @mobile 18711832023
 */
class SceneListBean() : Parcelable {
    var sceneId:String?=""
    var name:String?=""
    var iconUrl:String?=""
    var foregroundImageUrl:String?=""
    var backgroundImageUrl:String?=""
    var tokenPrice:String?=""
    var selectedFlag:String?=""
    var buyFlag:String?=""

    constructor(parcel: Parcel) : this() {
        sceneId = parcel.readString()
        name = parcel.readString()
        iconUrl = parcel.readString()
        foregroundImageUrl = parcel.readString()
        backgroundImageUrl = parcel.readString()
        tokenPrice = parcel.readString()
        selectedFlag = parcel.readString()
        buyFlag = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sceneId)
        parcel.writeString(name)
        parcel.writeString(iconUrl)
        parcel.writeString(foregroundImageUrl)
        parcel.writeString(backgroundImageUrl)
        parcel.writeString(tokenPrice)
        parcel.writeString(selectedFlag)
        parcel.writeString(buyFlag)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SceneListBean> {
        override fun createFromParcel(parcel: Parcel): SceneListBean {
            return SceneListBean(parcel)
        }

        override fun newArray(size: Int): Array<SceneListBean?> {
            return arrayOfNulls(size)
        }
    }
}
