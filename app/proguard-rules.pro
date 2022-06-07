# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#---------------------------------基本指令区----------------------------------
-dontskipnonpubliclibraryclassmembers
#-printmapping proguardMapping.txt   生成mapping文件到app目录下
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}
#----------------------------------------------------------------------------

-keep class  com.shunlai.im.video.** {*;}
-dontwarn   com.shunlai.im.video.**

-keep class  com.shunlai.im.face.** {*;}
-dontwarn   com.shunlai.im.face.**

-keep class  com.shunlai.im.entity.** {*;}
-dontwarn   com.shunlai.im.entity.**

-keep class com.tencent.imsdk.** { *; }
-dontwarn   com.tencent.imsdk.**

##---------------Begin: proguard configuration for Gson  ----------
-dontwarn sun.misc.**
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
##---------------End: proguard configuration for Gson  ----------


##---------------Begin: proguard configuration for EventBus  ----------
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
##---------------End: proguard configuration for EventBus  ----------


##---------------Begin: proguard configuration for Glide  ----------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
##---------------End: proguard configuration for Glide  ----------

-keep class top.zibin.luban.** {*;}
-dontwarn  top.zibin.luban.**

-keep class com.tencent.mmkv.** {*;}
-dontwarn  com.tencent.mmkv.**

-keep class com.shunlai.common.bean.** {*;}
-dontwarn  com.shunlai.common.bean.**


-keep class com.shunlai.main.entities.** {*;}
-dontwarn  com.shunlai.main.entities.**

-keep class com.shunlai.message.entity.** {*;}
-dontwarn  com.shunlai.message.entity.**

-keep class com.shunlai.message.complaint.entity.** {*;}
-dontwarn  com.shunlai.message.complaint.entity.**

-keep class  com.shunlai.mine.entity.** {*;}
-dontwarn   com.shunlai.mine.entity.**

-keep class  com.shunlai.publish.entity.** {*;}
-dontwarn   com.shunlai.publish.entity.**

-keep class  com.shunlai.publish.picker.entity.** {*;}
-dontwarn   com.shunlai.publish.picker.entity.**

-keep class  com.shunlai.im.entity.** {*;}
-dontwarn   com.shunlai.im.entity.**

-keep class  com.shunlai.ugc.entity.** {*;}
-dontwarn   com.shunlai.ugc.entity.**

-keep class com.shunlai.location.entity.** {*;}
-dontwarn   com.shunlai.location.entity.**

-keep class com.bigkoo.pickerview.** {*;}
-dontwarn   com.bigkoo.pickerview.**

-keep class  okhttp3.** {*;}
-dontwarn   okhttp3.**

-keep class  okio.** {*;}
-dontwarn   okio.**

-keep class  retrofit2.** {*;}
-dontwarn   retrofit2.**

-keep class  com.squareup.** {*;}
-dontwarn   com.squareup.**

-keep class  io.reactivex.** {*;}
-dontwarn   io.reactivex.**

-keep class  com.shunlai.net.bean.** {*;}
-dontwarn   com.shunlai.net.bean.**

-keep class com.kepler.**{*;}
-dontwarn com.kepler.**
-keep class com.jingdong.jdma.**{*;}
-dontwarn com.jingdong.jdma.**
-keep class com.jingdong.crash.**{*;}
-dontwarn com.jingdong.crash.**

-keep class com.shunlai.ui.**{*;}
-dontwarn com.shunlai.ui.**

-keep class com.github.ybq.android.spinkit.**{*;}
-dontwarn  com.github.ybq.android.spinkit.**


-keep class master.flame.danmaku.**{*;}
-dontwarn  master.flame.danmaku.**

-keep class tv.cjump.jni.**{*;}
-dontwarn  tv.cjump.jni.**



-dontwarn com.sensorsdata.analytics.android.sdk.**
-keep class com.sensorsdata.analytics.android.sdk.** {
*;
}

-dontwarn com.getui.**
-keep class com.getui.**{*;}

-dontwarn com.igexin.**
-keep class com.igexin.**{*;}

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-dontwarn com.sina.weibo.sdk.**
-keep public class com.sina.weibo.sdk.**{*;}

##-------------------淘宝联盟------------------------
-keep class javax.ws.rs.** { *; }
-keep class com.alibaba.fastjson.** { *; }
-dontwarn com.alibaba.fastjson.**
-keep class sun.misc.Unsafe { *; }
-dontwarn sun.misc.**
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-keep class com.ut.** {*;}
-dontwarn com.ut.**
-keep class com.ta.** {*;}
-dontwarn com.ta.**
-keep class org.json.** {*;}
-keep class com.ali.auth.**  {*;}
-dontwarn com.ali.auth.**
-keep class com.taobao.securityjni.** {*;}
-keep class com.taobao.wireless.security.** {*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}
-keep interface mtopsdk.mtop.global.init.IMtopInitTask {*;}
-keep class * implements mtopsdk.mtop.global.init.IMtopInitTask {*;}

