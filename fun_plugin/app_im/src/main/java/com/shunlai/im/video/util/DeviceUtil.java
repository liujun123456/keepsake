package com.shunlai.im.video.util;

import android.os.Build;

import com.shunlai.common.BaseApplication;
import com.shunlai.im.R;


public class DeviceUtil {

    private static String[] huaweiRongyao = {
            "hwH60",    //荣耀6
            "hwPE",     //荣耀6 plus
            "hwH30",    //3c
            "hwHol",    //3c畅玩版
            "hwG750",   //3x
            "hw7D",      //x1
            "hwChe2",      //x1
    };

    public static String getDeviceInfo() {
        String handSetInfo =
                BaseApplication.mInstance.getString(R.string.device_info) + Build.DEVICE +
                    BaseApplication.mInstance.getString(R.string.system_version) + Build.VERSION.RELEASE +
                    BaseApplication.mInstance.getString(R.string.sdk_version) + Build.VERSION.SDK_INT;
        return handSetInfo;
    }

    public static String getDeviceModel() {
        return Build.DEVICE;
    }

    public static boolean isHuaWeiRongyao() {
        int length = huaweiRongyao.length;
        for (int i = 0; i < length; i++) {
            if (huaweiRongyao[i].equals(getDeviceModel())) {
                return true;
            }
        }
        return false;
    }
}
