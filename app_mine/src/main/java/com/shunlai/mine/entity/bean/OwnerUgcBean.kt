package com.shunlai.mine.entity.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Liu
 * @Date   2021/7/22
 * @mobile 18711832023
 */
class OwnerUgcBean() : Parcelable {
    var ugcId:String?=""
    var title:String?=""
    var content:String?=""
    var ugcType:String?=""
    var firstImage:String?=""
    var likes:String?=""
    var nickName:String?=""
    var avatar:String?=""

    constructor(parcel: Parcel) : this() {
        ugcId = parcel.readString()
        title = parcel.readString()
        content = parcel.readString()
        ugcType = parcel.readString()
        firstImage = parcel.readString()
        likes = parcel.readString()
        nickName = parcel.readString()
        avatar = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ugcId)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(ugcType)
        parcel.writeString(firstImage)
        parcel.writeString(likes)
        parcel.writeString(nickName)
        parcel.writeString(avatar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OwnerUgcBean> {
        override fun createFromParcel(parcel: Parcel): OwnerUgcBean {
            return OwnerUgcBean(parcel)
        }

        override fun newArray(size: Int): Array<OwnerUgcBean?> {
            return arrayOfNulls(size)
        }
    }
}
