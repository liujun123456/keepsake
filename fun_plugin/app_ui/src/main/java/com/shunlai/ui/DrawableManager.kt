package com.shunlai.ui

import android.graphics.Color
import android.graphics.RadialGradient
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable

/**
 * @author Liu
 * @Date   2021/5/19
 * @mobile 18711832023
 */
object DrawableManager {
    fun buildStyleDrawable(startColor:Int,endColor:Int):Drawable{
        val drawable= GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.orientation=GradientDrawable.Orientation.TOP_BOTTOM
        drawable.colors= intArrayOf(startColor,endColor)
        return drawable
    }

    fun buildLinerDrawable(colors:IntArray):Drawable{
        val drawable= GradientDrawable()
        drawable.orientation=GradientDrawable.Orientation.TOP_BOTTOM
        drawable.colors= colors
        return drawable
    }

    fun buildLinerDrawableWithCorner(colors:IntArray,radius:Float):Drawable{
        val drawable= GradientDrawable()
        drawable.orientation=GradientDrawable.Orientation.TOP_BOTTOM
        drawable.colors= colors
        drawable.cornerRadii= floatArrayOf(radius,radius,radius,radius,radius,radius,radius,radius)
        return drawable
    }
}
