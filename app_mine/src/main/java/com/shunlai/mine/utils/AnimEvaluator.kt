package com.shunlai.mine.utils

import android.animation.TypeEvaluator
import com.shunlai.mine.entity.bean.LoginAnimPoint

/**
 * @author Liu
 * @Date   2021/6/25
 * @mobile 18711832023
 */
class AnimEvaluator:TypeEvaluator<LoginAnimPoint> {
    override fun evaluate(fraction: Float,startValue: LoginAnimPoint,endValue: LoginAnimPoint): LoginAnimPoint {
        val firstPoint=startValue.firstMargin+fraction.times(endValue.firstMargin-startValue.firstMargin)
        val secondPoint=startValue.secondMargin+fraction.times(endValue.secondMargin-startValue.secondMargin)
        return LoginAnimPoint(firstPoint.toInt(),secondPoint.toInt())
    }
}
