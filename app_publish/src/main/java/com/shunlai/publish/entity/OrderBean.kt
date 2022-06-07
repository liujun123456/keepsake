package com.shunlai.publish.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Liu
 * @Date   2021/5/27
 * @mobile 18711832023
 */
class OrderBean() : Parcelable {
    var memberId:String?=null
    var orderNo:String?=null
    var orderAmount:String?=null
    var orderTime:String?=null
    var skuId:String?=null
    var skuName:String?=null
    var skuImage:String?=null
    var skuNum:String?=null
    var price:String?=null
    var evaluate:Int=0

    constructor(parcel: Parcel) : this() {
        memberId = parcel.readString()
        orderNo = parcel.readString()
        orderAmount = parcel.readString()
        orderTime = parcel.readString()
        skuId = parcel.readString()
        skuName = parcel.readString()
        skuImage = parcel.readString()
        skuNum = parcel.readString()
        price = parcel.readString()
        evaluate = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(memberId)
        parcel.writeString(orderNo)
        parcel.writeString(orderAmount)
        parcel.writeString(orderTime)
        parcel.writeString(skuId)
        parcel.writeString(skuName)
        parcel.writeString(skuImage)
        parcel.writeString(skuNum)
        parcel.writeString(price)
        parcel.writeInt(evaluate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderBean> {
        override fun createFromParcel(parcel: Parcel): OrderBean {
            return OrderBean(parcel)
        }

        override fun newArray(size: Int): Array<OrderBean?> {
            return arrayOfNulls(size)
        }
    }
}
