package com.shunlai.router

import android.app.Activity
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * @author Liu
 * @Date   2019-06-28
 * @mobile 18711832023
 * @email  liujunsh01@aikucun.com
 */
object RouterManager {

    fun startActivityWithParams(url:String,ctx:Activity,params:MutableMap<String,Any?> = mutableMapOf()){
        startActivityForResultWithParams(url,ctx,params,null)
    }

    fun startActivityForResultWithParams(url:String, ctx:Activity, params:MutableMap<String,Any?> = mutableMapOf(),requestCode:Int?){
        val intent=Intent()
        intent.component = ComponentName(ctx.packageName,url)
        buildParams(intent,params)
        if (requestCode==null){
            ctx.startActivity(intent)
        }else{
            ctx.startActivityForResult(intent,requestCode)
        }
    }

    fun startTransActivityWithParams(url:String, ctx:Activity, transValue:Bundle,params:MutableMap<String,Any?> = mutableMapOf()){
        startTransActivityForResultWithParams(url,ctx,transValue,params,null)
    }

    fun startTransActivityForResultWithParams(url:String, ctx:Activity, transValue:Bundle, params:MutableMap<String,Any?> = mutableMapOf(),requestCode:Int?){
        val intent=Intent()
        intent.component = ComponentName(ctx.packageName,url)
        buildParams(intent,params)
        if (requestCode==null){
            ctx.startActivity(intent,transValue)
        }else{
            ctx.startActivityForResult(intent,requestCode,transValue)
        }
    }


    fun navigationFragment(url:String):Any?{
        try {
            val clazz=Class.forName(url)
            return clazz.newInstance()
        }catch (e:Exception){

        }
        return null
    }

    private fun buildParams(intent:Intent,params:MutableMap<String,Any?>){
        params.forEach {
            when(val value=it.value){
                null->{}
                is Int ->intent.putExtra(it.key,value)
                is Long ->intent.putExtra(it.key,value)
                is CharSequence ->intent.putExtra(it.key,value)
                is String ->intent.putExtra(it.key,value)
                is Float ->intent.putExtra(it.key,value)
                is Double ->intent.putExtra(it.key,value)
                is Char ->intent.putExtra(it.key,value)
                is Short ->intent.putExtra(it.key,value)
                is Boolean ->intent.putExtra(it.key,value)
                is Serializable ->intent.putExtra(it.key,value)
                is Bundle ->intent.putExtra(it.key,value)
                is Parcelable ->intent.putExtra(it.key,value)
                is ArrayList<*> -> when (value[0]){
                    is String ->intent.putStringArrayListExtra(it.key,value as ArrayList<String>)
                    is Parcelable ->intent.putParcelableArrayListExtra(it.key,value as ArrayList<out Parcelable>)
                    is Int ->intent.putIntegerArrayListExtra(it.key,value as ArrayList<Int>)
                    is CharSequence ->intent.putCharSequenceArrayListExtra(it.key,value as ArrayList<CharSequence>)
                }

            }
        }
    }
}
