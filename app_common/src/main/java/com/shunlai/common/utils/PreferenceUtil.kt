package com.shunlai.common.utils

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 * @author Liu
 * @Date   2021/4/15
 * @mobile 18711832023
 */
object PreferenceUtil {
    @JvmStatic
    fun init(mContext:Application){
        MMKV.initialize(mContext)
    }

    @JvmStatic
    private fun getMmKV():MMKV?{
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE,null)
    }

    //string
    @JvmStatic
    fun putString(key:String,value:String){
        getMmKV()?.putString(key,value)?.commit()
    }
    @JvmStatic
    fun getString(key:String):String?{
        return getMmKV()?.getString(key,"")
    }

    //boolean
    @JvmStatic
    fun putBoolean(key:String,value:Boolean){
        getMmKV()?.putBoolean(key,value)?.commit()
    }
    @JvmStatic
    fun getBoolean(key:String):Boolean?{
        return getMmKV()?.getBoolean(key,false)
    }

    //int
    @JvmStatic
    fun putInt(key:String,value:Int){
        getMmKV()?.putInt(key,value)?.commit()
    }
    @JvmStatic
    fun getInt(key:String):Int?{
        return getMmKV()?.getInt(key,-1)
    }

    //long
    @JvmStatic
    fun putLong(key:String,value:Long){
        getMmKV()?.putLong(key,value)?.commit()
    }
    @JvmStatic
    fun getLong(key:String):Long?{
        return getMmKV()?.getLong(key,-1)
    }

    //float
    @JvmStatic
    fun putFloat(key:String,value:Float){
        getMmKV()?.putFloat(key,value)?.commit()
    }
    @JvmStatic
    fun getFloat(key:String):Float?{
        return getMmKV()?.getFloat(key,-1f)
    }

    //bytes
    @JvmStatic
    fun putBytes(key:String,value:ByteArray){
        getMmKV()?.putBytes(key,value)?.commit()
    }
    @JvmStatic
    fun getBytes(key:String):ByteArray?{
        return getMmKV()?.getBytes(key, ByteArray(0))
    }

    //set<String>
    @JvmStatic
    fun putSetString(key:String,value:MutableSet<String>){
        getMmKV()?.putStringSet(key,value)?.commit()
    }
    @JvmStatic
    fun getSetString(key:String):MutableSet<String>?{
        return getMmKV()?.getStringSet(key, mutableSetOf())
    }

    //remove
    @JvmStatic
    fun removeValueWithKey(key:String){
        getMmKV()?.removeValueForKey(key)
    }


    //all keys
    @JvmStatic
    fun getAllKeys():Array<String>?{
        return getMmKV()?.allKeys()
    }

}
