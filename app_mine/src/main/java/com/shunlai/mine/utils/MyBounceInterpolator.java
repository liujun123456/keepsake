package com.shunlai.mine.utils;

import android.view.animation.Interpolator;

/**
 * @author Liu
 * @Date 2021/8/4
 * @mobile 18711832023
 */
public class MyBounceInterpolator implements Interpolator {
    double defaultAmplitude = 0.3f;
    double defaultFrequency = 6;

    public MyBounceInterpolator() {
    }

    public MyBounceInterpolator(double defaultAmplitude, double defaultFrequency) {
        this.defaultAmplitude = defaultAmplitude;
        this.defaultFrequency = defaultFrequency;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / defaultAmplitude) * Math.cos(defaultFrequency * time) + 1);
    }
}
