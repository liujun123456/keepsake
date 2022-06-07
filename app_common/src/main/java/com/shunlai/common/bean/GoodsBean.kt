package com.shunlai.common.bean

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
    var evaType:Int=0

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
        evaType = parcel.readInt()
    }


    fun buildUgcGoods():UgcGoods{
        val ugc=UgcGoods()
        ugc.evaluate=evaluate.toString()
        ugc.productName=name
        ugc.productImg=thumb
        ugc.shopName=shopName
        ugc.type=type
        ugc.price=price
        ugc.productId=productId
        ugc.cateId=cateId
        ugc.subCateId=subCateId
        ugc.isRecommend=if (evaType==1) 1 else 0
        return ugc
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
        parcel.writeInt(evaType)
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
