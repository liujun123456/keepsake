package com.shunlai.common.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.IntegerRes
import androidx.annotation.RawRes
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.shunlai.common.R
import java.lang.Exception

/**
 * @author Liu
 * @Date   2020/11/4
 * @mobile 18711832023
 */
object ImageUtil {


    fun showCircleImgWithString(image:ImageView,ctx:Context,url:String?,placeholder:Int=R.mipmap.user_default_icon){
        val roundedCorners= CircleCrop()
        val options= RequestOptions.bitmapTransform(roundedCorners).placeholder(placeholder)
        realShow(image,ctx, Uri.parse(url?:""), options,null)
    }

    fun showCircleImgWithString(image:ImageView,ctx:Context,url:String?,listener:RequestListener<Drawable>){
        val roundedCorners= CircleCrop()
        val options= RequestOptions.bitmapTransform(roundedCorners).placeholder(R.mipmap.default_icon)
        realShow(image,ctx, Uri.parse(url?:""), options,listener)
    }

    fun showRoundImgWithResAndRadius(image:ImageView, ctx:Context,resource:Int, radius:Float, listener:RequestListener<Drawable>?=null){
        val roundedCorners= RoundedCorners(ScreenUtils.dip2px(ctx,radius))
        val builder=Glide.with(ctx)
            .load(resource)
            .transform(MultiTransformation(CenterCrop(),roundedCorners))
            .placeholder(R.mipmap.default_icon)
        listener?.let {
            builder.listener(it)
        }
        builder.into(image)
    }

    fun showRoundImgWithUriAndRadius(image:ImageView,ctx:Context,uri:Uri?,radius:Float,listener:RequestListener<Drawable>?=null){
        val roundedCorners= RoundedCorners(ScreenUtils.dip2px(ctx,radius))
        val builder=Glide.with(ctx)
            .load(uri)
            .transform(MultiTransformation(CenterCrop(),roundedCorners))
            .placeholder(R.mipmap.default_icon)
        listener?.let {
            builder.listener(it)
        }
        builder.into(image)
    }

    fun showRoundImgWithStringAndRadius(image:ImageView,ctx:Context,url:String?,radius:Float,listener:RequestListener<Drawable>?=null){
        val roundedCorners= RoundedCorners(ScreenUtils.dip2px(ctx,radius))
        val builder=Glide.with(ctx)
            .load(Uri.parse(url?:""))
            .transform(MultiTransformation(CenterCrop(),roundedCorners))
            .placeholder(R.mipmap.default_icon)
        listener?.let {
            builder.listener(it)
        }
        builder.into(image)
    }

    fun showRoundImgWithStringAndRadius(image:ImageView,ctx:Context,url:String?,radius:Float,leftTop:Boolean=false,rightTop:Boolean=false,leftBottom:Boolean=false,rightBottom:Boolean=false){
        val roundedCorners =RoundedCornersTransform(ctx, ScreenUtils.dip2px(ctx,radius).toFloat())
        roundedCorners.setNeedCorner(leftTop, rightTop, leftBottom, rightBottom)
        val builder=Glide.with(ctx)
            .load(Uri.parse(url?:""))
            .transform(MultiTransformation(CenterCrop(),roundedCorners))
            .placeholder(R.mipmap.default_icon)
        builder.into(image)
    }


    fun showCropImgWithString(image:ImageView,ctx:Context,url:String?,placeholder:Int=R.mipmap.default_icon){
        val roundedCorners= CenterCrop()
        val options= RequestOptions.bitmapTransform(roundedCorners).placeholder(placeholder)
        realShow(image,ctx, Uri.parse(url?:""), options,null)
    }

    fun showCropImgWithUri(image:ImageView,ctx:Context,uri:Uri?){
        val roundedCorners= CenterCrop()
        val options= RequestOptions.bitmapTransform(roundedCorners).placeholder(R.mipmap.default_icon)
        realShow(image,ctx,uri,options,null)
    }

    fun showRoundImgWithUri(image:ImageView,ctx:Context,uri:Uri?){
        val roundedCorners=  RoundedCorners(ScreenUtils.dip2px(ctx,8f))
        val options= RequestOptions.bitmapTransform(roundedCorners).placeholder(R.mipmap.default_icon)
        realShow(image,ctx,uri,options,null)
    }


    private fun realShow(image:ImageView,ctx:Context,uri:Uri?,options: RequestOptions,listener:RequestListener<Drawable>?){
        try {
            val builder=Glide.with(ctx).load(uri).apply(options)
            listener?.let {
                builder.listener(it)
            }
            builder.into(image)
        }catch (e:Exception){}
    }

   fun showPreView(image: ImageView,ctx:Context,uri:Uri?){
        Glide.with(ctx)
            .load(uri)
            .priority(Priority.HIGH)
            .fitCenter().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(image)
    }

    fun getHighPriorityBitmapFromUrl(context:Context,url:String):Bitmap{
        if (TextUtils.isEmpty(url)){
            return BitmapFactory.decodeResource(context.resources, R.mipmap.default_icon)
        }
        val bitmap=Glide.with(context).asBitmap().load(url)
            .priority(Priority.HIGH).fitCenter().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .submit().get()
        if (bitmap==null){
            return BitmapFactory.decodeResource(context.resources, R.mipmap.default_icon)
        }else{
            return bitmap
        }
    }

    fun showGoodsImg(image: ImageView,ctx:Context,uri:Uri?){
        Glide.with(ctx)
            .load(uri).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(image)
    }

    fun showGoodsGif(image: ImageView,ctx:Context,uri:Uri?){
        Glide.with(ctx).asGif()
            .load(uri).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(image)
    }

    fun showBitmapWithRadius(image: ImageView,ctx:Context,bitmap: Bitmap,radius:Float){
        if (radius>0){
            val roundedCorners= RoundedCorners(ScreenUtils.dip2px(ctx,radius))
            val builder=Glide.with(ctx)
                .load(bitmap)
                .transform(MultiTransformation(CenterCrop(),roundedCorners))
                .placeholder(R.mipmap.default_icon)
            builder.into(image)
        }else{
            val builder=Glide.with(ctx)
                .load(bitmap)
                .transform(CenterCrop())
                .placeholder(R.mipmap.default_icon)
            builder.into(image)
        }
    }


    fun openImage(context: Activity, requestCode:Int){
        val intent: Intent?
        if (VersionUtils.isTargetQ(context)) {
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
        } else {
            if (Build.VERSION.SDK_INT < 19) {
                intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
            } else {
                intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            }
        }
        intent.type = "image/*"
        context.startActivityForResult(intent, requestCode)
    }

    fun getBitmapFromUrl(context:Context,url:String):Bitmap{
        if (TextUtils.isEmpty(url)){
            return BitmapFactory.decodeResource(context.resources, R.mipmap.user_default_icon)
        }
        var bitmap:Bitmap?=null
        try {
            bitmap=Glide.with(context).asBitmap().load(url).transform(CenterCrop()).submit().get()
        }catch (e:Exception){

        }
        if (bitmap==null){
            return BitmapFactory.decodeResource(context.resources, R.mipmap.user_default_icon)
        }else{
            return bitmap
        }
    }

}
