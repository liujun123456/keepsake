package com.shunlai.ui.danmuku;

import master.flame.danmaku.danmaku.model.android.DanmakuContext;

/**
 * @author Liu
 * @Date 2021/7/12
 * @mobile 18711832023
 */
public class DanMuKuContext extends DanmakuContext {
    public static DanMuKuContext create(){
        return new DanMuKuContext();
    }

    private int mUpdateRate = 16;

    public int getFrameUpdateRate(){
        return mUpdateRate;
    }

    public void setFrameUpateRate(int rate){
        mUpdateRate = rate;
    }
}
