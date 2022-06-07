package com.shunlai.common;

import android.app.Application;
import android.util.Log;

import com.getui.gs.ias.core.GsConfig;
import com.getui.gs.sdk.GsManager;
import com.igexin.sdk.IUserLoggerInterface;
import com.igexin.sdk.PushManager;
import com.meituan.android.walle.WalleChannelReader;
import com.shunlai.common.utils.Constant;
import com.shunlai.common.utils.PreferenceUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Liu
 * @Date 2021/4/9
 * @mobile 18711832023
 */
public class BaseApplication extends Application {
    public static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        PreferenceUtil.init(this);

    }

    public void initThirdPlugin(){
        initGtStatistics();
        initGtPush();
        CrashReport.initCrashReport(getApplicationContext(), BuildConfig.BUGLY_ID, BuildConfig.DEBUG);
    }


    private void initGtStatistics(){
        GsManager.getInstance().init(this);
        GsConfig.setDebugEnable(BuildConfig.DEBUG);
        GsConfig.setInstallChannel(readManifestChannel());
        try {
            JSONObject properties = new JSONObject();
            properties.put("userid", PreferenceUtil.getString(Constant.USER_ID));
            properties.put("store", readManifestChannel());
            GsManager.getInstance().setProfile(properties);
        }catch (Exception e){

        }

    }

    private void initGtPush(){
        PushManager.getInstance().initialize(this);
        PushManager.getInstance().setDebugLogger(this, s -> {

        });

    }

    private String readManifestChannel(){
        return WalleChannelReader.getChannel(this,"999");
    }
}
