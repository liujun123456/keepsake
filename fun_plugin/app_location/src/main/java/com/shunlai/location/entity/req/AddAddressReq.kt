package com.shunlai.location.entity.req

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
class AddAddressReq() : Parcelable {
    var addressId:String?=""

    var area:String?=""
    var areaId:String?=""
    var cityId:String?=""
    var cityName:String?=""
    var provinceId:String?=""
    var provinceName:String?=""

    var contact:String?=""
    var phone:String?=""
    var street:String?=""
    var isDefault:Int?=0
    var memberId:String?=""

    constructor(parcel: Parcel) : this() {
        addressId = parcel.readString()
        area = parcel.readString()
        areaId = parcel.readString()
        cityId = parcel.readString()
        cityName = parcel.readString()
        provinceId = parcel.readString()
        provinceName = parcel.readString()
        contact = parcel.readString()
        phone = parcel.readString()
        street = parcel.readString()
        isDefault = parcel.readValue(Int::class.java.classLoader) as? Int
        memberId = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addressId)
        parcel.writeString(area)
        parcel.writeString(areaId)
        parcel.writeString(cityId)
        parcel.writeString(cityName)
        parcel.writeString(provinceId)
        parcel.writeString(provinceName)
        parcel.writeString(contact)
        parcel.writeString(phone)
        parcel.writeString(street)
        parcel.writeValue(isDefault)
        parcel.writeString(memberId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddAddressReq> {
        override fun createFromParcel(parcel: Parcel): AddAddressReq {
            return AddAddressReq(parcel)
        }

        override fun newArray(size: Int): Array<AddAddressReq?> {
            return arrayOfNulls(size)
        }
    }
}
