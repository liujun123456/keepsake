package com.shunlai.net.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * @author Liu
 * @Date   2020/8/20
 * @mobile 18711832023
 */
object GsonUtil{

    private var gson: Gson=Gson()

    @JvmStatic
    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(paramJsonElement: JsonElement, paramClass: Class<T>): T {
        return gson.fromJson(paramJsonElement, paramClass) as T
    }

    @JvmStatic
    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(paramJsonElement: JsonElement?, paramType: Type): T? {
        return if (paramJsonElement == null) {
            null
        } else gson.fromJson(paramJsonElement, paramType) as T
    }


    @JvmStatic
    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(paramString: String, paramClass: Class<T>): T {
        return gson.fromJson(paramString, paramClass) as T
    }

    @JvmStatic
    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(paramString: String?, paramType: Type): T? {
        if (paramString==null){
            return null
        }
        return gson.fromJson(paramString, paramType) as T
    }

    @JvmStatic
    fun toJson(paramObject: Any): String {
        return gson.toJson(paramObject)
    }

    @JvmStatic
    fun toJson(paramObject: Any, paramType: Type): String {
        return gson.toJson(paramObject, paramType)
    }

}
