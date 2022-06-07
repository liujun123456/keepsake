package com.shunlai.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebSettings;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Liu
 * @Date 2021/8/31
 * @mobile 18711832023
 */
public class SensorsUtil {

    private static final String marshmallowMacAddress = "02:00:00:00:00:00";

    private static final String SHARED_PREF_EDITS_FILE = "sensorsdata";
    private static final String SHARED_PREF_USER_AGENT_KEY = "sensorsdata.user.agent";
    private static final String SHARED_PREF_APP_VERSION = "sensorsdata.app.version";
    private static final String SHARED_PREF_DEVICE_ID_KEY = "sensorsdata.device.id";

    public static final String COMMAND_HARMONYOS_VERSION = "getprop hw_sc.build.platform.version";

    private static final Set<String> mPermissionGrantedSet = new HashSet<>();
    private static final Map<String, String> deviceUniqueIdentifiersMap = new HashMap<>();
    private static final Map<String, String> sCarrierMap = new HashMap<String, String>() {
        {
            //中国移动
            put("46000", "中国移动");
            put("46002", "中国移动");
            put("46007", "中国移动");
            put("46008", "中国移动");

            //中国联通
            put("46001", "中国联通");
            put("46006", "中国联通");
            put("46009", "中国联通");

            //中国电信
            put("46003", "中国电信");
            put("46005", "中国电信");
            put("46011", "中国电信");

            //中国卫通
            put("46004", "中国卫通");

            //中国铁通
            put("46020", "中国铁通");

        }
    };

    private static final List<String> mInvalidAndroidId = new ArrayList<String>() {
        {
            add("9774d56d682e549c");
            add("0123456789abcdef");
        }
    };
    private static final String TAG = "SA.SensorsDataUtils";

    private static String getJsonFromAssets(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bf = null;
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            bf = new BufferedReader(new InputStreamReader(
                assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {

        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {

                }
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 根据 operator，获取本地化运营商信息
     *
     * @param context context
     * @param operator sim operator
     * @param alternativeName 备选名称
     * @return local carrier name
     */
    private static String operatorToCarrier(Context context, String operator, String alternativeName) {
        try {
            if (TextUtils.isEmpty(operator)) {
                return alternativeName;
            }
            if (sCarrierMap.containsKey(operator)) {
                return sCarrierMap.get(operator);
            }
            String carrierJson = getJsonFromAssets("sa_mcc_mnc_mini.json", context);
            if (TextUtils.isEmpty(carrierJson)) {
                sCarrierMap.put(operator, alternativeName);
                return alternativeName;
            }
            JSONObject jsonObject = new JSONObject(carrierJson);
            String carrier = getCarrierFromJsonObject(jsonObject, operator);
            if (!TextUtils.isEmpty(carrier)) {
                sCarrierMap.put(operator, carrier);
                return carrier;
            }
        } catch (Exception e) {

        }
        return alternativeName;
    }

    private static String getCarrierFromJsonObject(JSONObject jsonObject, String mccMnc) {
        if (jsonObject == null || TextUtils.isEmpty(mccMnc)) {
            return null;
        }
        return jsonObject.optString(mccMnc);

    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF_EDITS_FILE, Context.MODE_PRIVATE);
    }

    @TargetApi(11)
    static String getToolbarTitle(Activity activity) {
        try {
            if ("com.tencent.connect.common.AssistActivity".equals(activity.getClass().getCanonicalName())) {
                if (!TextUtils.isEmpty(activity.getTitle())) {
                    return activity.getTitle().toString();
                }
                return null;
            }
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                if (!TextUtils.isEmpty(actionBar.getTitle())) {
                    return actionBar.getTitle().toString();
                }
            } else {
                try {
                    Class<?> appCompatActivityClass = compatActivity();
                    if (appCompatActivityClass != null && appCompatActivityClass.isInstance(activity)) {
                        Method method = activity.getClass().getMethod("getSupportActionBar");
                        Object supportActionBar = method.invoke(activity);
                        if (supportActionBar != null) {
                            method = supportActionBar.getClass().getMethod("getTitle");
                            CharSequence charSequence = (CharSequence) method.invoke(supportActionBar);
                            if (charSequence != null) {
                                return charSequence.toString();
                            }
                        }
                    }
                } catch (Exception e) {
                    //ignored
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    private static Class<?> compatActivity() {
        Class<?> appCompatActivityClass = null;
        try {
            appCompatActivityClass = Class.forName("android.support.v7.app.AppCompatActivity");
        } catch (Exception e) {
            //ignored
        }
        if (appCompatActivityClass == null) {
            try {
                appCompatActivityClass = Class.forName("androidx.appcompat.app.AppCompatActivity");
            } catch (Exception e) {
                //ignored
            }
        }
        return appCompatActivityClass;
    }



    /**
     * 获取 UA 值
     *
     * @param context Context
     * @return 当前 UA 值
     */
    @Deprecated
    public static String getUserAgent(Context context) {
        try {
            final SharedPreferences preferences = getSharedPreferences(context);
            String userAgent = preferences.getString(SHARED_PREF_USER_AGENT_KEY, null);
            if (TextUtils.isEmpty(userAgent)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    try {
                        Class webSettingsClass = Class.forName("android.webkit.WebSettings");
                        Method getDefaultUserAgentMethod = webSettingsClass.getMethod("getDefaultUserAgent", Context.class);
                        if (getDefaultUserAgentMethod != null) {
                            userAgent = WebSettings.getDefaultUserAgent(context);
                        }
                    } catch (Exception e) {

                    }
                }

                if (TextUtils.isEmpty(userAgent)) {
                    userAgent = System.getProperty("http.agent");
                }

                if (!TextUtils.isEmpty(userAgent)) {
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(SHARED_PREF_USER_AGENT_KEY, userAgent);
                    editor.apply();
                }
            }

            return userAgent;
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * 检测权限
     *
     * @param context Context
     * @param permission 权限名称
     * @return true:已允许该权限; false:没有允许该权限
     */
    public static boolean checkHasPermission(Context context, String permission) {
        try {
            if (mPermissionGrantedSet.contains(permission)) {
                return true;
            }
            Class<?> contextCompat = null;
            try {
                contextCompat = Class.forName("android.support.v4.content.ContextCompat");
            } catch (Exception e) {
                //ignored
            }

            if (contextCompat == null) {
                try {
                    contextCompat = Class.forName("androidx.core.content.ContextCompat");
                } catch (Exception e) {
                    //ignored
                }
            }

            if (contextCompat == null) {
                mPermissionGrantedSet.add(permission);
                return true;
            }

            Method checkSelfPermissionMethod = contextCompat.getMethod("checkSelfPermission", Context.class, String.class);
            int result = (int) checkSelfPermissionMethod.invoke(null, new Object[]{context, permission});
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            mPermissionGrantedSet.add(permission);
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 此方法谨慎修改
     * 插件配置 disableIMEI 会修改此方法
     * 获取IMEI
     *
     * @param context Context
     * @return IMEI
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMEI(Context context) {
        String imei = "";
        try {
            if (deviceUniqueIdentifiersMap.containsKey("IMEI")) {
                imei = deviceUniqueIdentifiersMap.get("IMEI");
            }
            if (TextUtils.isEmpty(imei) && hasReadPhoneStatePermission(context)) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        if (tm.hasCarrierPrivileges()) {
                            imei = tm.getImei();
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        imei = tm.getImei();
                    } else {
                        imei = tm.getDeviceId();
                    }
                    deviceUniqueIdentifiersMap.put("IMEI", imei);
                }
            }
        } catch (Exception e) {
        }
        return imei;
    }

    /**
     * 获取设备标识
     *
     * @param context Context
     * @return 设备标识
     */
    public static String getIMEIOld(Context context) {
        return getDeviceID(context, -1);
    }

    /**
     * 获取设备标识
     *
     * @param context Context
     * @param number 卡槽位置
     * @return 设备标识
     */
    public static String getSlot(Context context, int number) {
        return getDeviceID(context, number);
    }

    /**
     * 获取设备标识
     *
     * @param context Context
     * @return 设备标识
     */
    public static String getMEID(Context context) {
        return getDeviceID(context, -2);
    }

    /**
     * 获取设备唯一标识
     *
     * @param context Context
     * @param number 卡槽
     * @return 设备唯一标识
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    private static String getDeviceID(Context context, int number) {
        String deviceId = "";
        try {
            String deviceIDKey = "deviceID" + number;
            if (deviceUniqueIdentifiersMap.containsKey(deviceIDKey)) {
                deviceId = deviceUniqueIdentifiersMap.get(deviceIDKey);
            }
            if (TextUtils.isEmpty(deviceId) && hasReadPhoneStatePermission(context)) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (number == -1) {
                        deviceId = tm.getDeviceId();
                    } else if (number == -2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        deviceId = tm.getMeid();
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        deviceId = tm.getDeviceId(number);
                    }
                    deviceUniqueIdentifiersMap.put(deviceIDKey, deviceId);
                }
            }
        } catch (Exception e) {

        }
        return deviceId;
    }

    private static boolean hasReadPhoneStatePermission(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return false;
        } else if (!checkHasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            return false;
        }
        return true;
    }

    /**
     * 此方法谨慎修改
     * 插件配置 disableAndroidID 会修改此方法
     * 获取 Android ID
     *
     * @param context Context
     * @return androidID
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID(Context context) {
        String androidID = "";
        try {
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
        }
        return androidID;
    }

    private static String getMacAddressByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if ("wlan0".equalsIgnoreCase(nif.getName())) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            //ignore
        }
        return null;
    }

    /**
     * 此方法谨慎修改
     * 插件配置 disableMacAddress 会修改此方法
     * 获取手机的 Mac 地址
     *
     * @param context Context
     * @return String 当前手机的 Mac 地址
     */
    @SuppressLint("HardwareIds")
    public static String getMacAddress(Context context) {
        String macAddress = "";
        try {
            if (deviceUniqueIdentifiersMap.containsKey("macAddress")) {
                macAddress = deviceUniqueIdentifiersMap.get("macAddress");
            }
            if (TextUtils.isEmpty(macAddress) && checkHasPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                WifiManager wifiMan = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiMan != null) {
                    WifiInfo wifiInfo = wifiMan.getConnectionInfo();
                    if (wifiInfo != null && wifiInfo.getMacAddress() != null) {
                        macAddress = wifiInfo.getMacAddress();
                        if (marshmallowMacAddress.equals(macAddress)) {
                            macAddress = getMacAddressByInterface();
                            if (macAddress == null) {
                                macAddress = marshmallowMacAddress;
                            }
                        }
                        deviceUniqueIdentifiersMap.put("macAddress", macAddress);
                    }
                }
            }
        } catch (Exception e) {
        }
        return macAddress;
    }

    public static boolean isValidAndroidId(String androidId) {
        if (TextUtils.isEmpty(androidId)) {
            return false;
        }

        return !mInvalidAndroidId.contains(androidId.toLowerCase(Locale.getDefault()));
    }

    /**
     * 检查版本是否经过升级
     *
     * @param context context
     * @param currVersion 当前 SDK 版本
     * @return true，老版本升级到新版本。false，当前已是最新版本
     */
    public static boolean checkVersionIsNew(Context context, String currVersion) {
        try {
            SharedPreferences appVersionPref = getSharedPreferences(context);
            String localVersion = appVersionPref.getString(SHARED_PREF_APP_VERSION, "");

            if (!TextUtils.isEmpty(currVersion) && !currVersion.equals(localVersion)) {
                appVersionPref.edit().putString(SHARED_PREF_APP_VERSION, currVersion).apply();
                return true;
            }
        } catch (Exception ex) {
            return true;
        }
        return false;
    }
}
