package com.shunlai.mine.entity.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Liu
 * @Date   2021/4/14
 * @mobile 18711832023
 */
class GoodsBean() : Parcelable {
    var name:String?=null
    var subtitle:String?=null
    var thumb:String?=null
    var cateId:String?=null
    var subCateId:String?=null
    var price:String?=null
    var totalSales:String?=null
    var type:String?=null
    var productId:String?=null
    var commission:String?=null
    var evaluate:Int?=null
    var shopName:String?=null
    var brandName:String?=null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        subtitle = parcel.readString()
        thumb = parcel.readString()
        cateId = parcel.readString()
        subCateId = parcel.readString()
        price = parcel.readString()
        totalSales = parcel.readString()
        type = parcel.readString()
        productId = parcel.readString()
        commission = parcel.readString()
        evaluate = parcel.readValue(Int::class.java.classLoader) as? Int
        shopName = parcel.readString()
        brandName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(subtitle)
        parcel.writeString(thumb)
        parcel.writeString(cateId)
        parcel.writeString(subCateId)
        parcel.writeString(price)
        parcel.writeString(totalSales)
        parcel.writeString(type)
        parcel.writeString(productId)
        parcel.writeString(commission)
        parcel.writeValue(evaluate)
        parcel.writeString(shopName)
        parcel.writeString(brandName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GoodsBean> {
        override fun createFromParcel(parcel: Parcel): GoodsBean {
            return GoodsBean(parcel)
        }

        override fun newArray(size: Int): Array<GoodsBean?> {
            return arrayOfNulls(size)
        }
    }
}
