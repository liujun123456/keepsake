package com.shunlai.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.getui.gs.sdk.GsManager;
import com.shunlai.common.utils.BaseActivityManager;
import com.shunlai.common.utils.BundleUrl;
import com.shunlai.common.utils.Constant;
import com.shunlai.common.utils.PreferenceUtil;
import com.shunlai.common.utils.RunCacheDataUtil;
import com.shunlai.common.utils.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author AsureLiu on 2018/8/9.
 */

public abstract class BaseActivity extends AppCompatActivity{
    public Toolbar base_toolbar;
    private RelativeLayout main_view;
    public Context mContext = this;
    private LoadingDialog loadingDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keepFullScreen();
        StatusBarUtil.showDrakStatusBarIcon(this);
        BaseActivityManager.getInstance().addActivity(this);
        setRootView();
        sensorsTrackActivityIn();
    }

    private void initView() {
        base_toolbar = findViewById(R.id.base_toolbar);
        main_view=findViewById(R.id.main_view);
    }

    private void setRootView() {
        setContentView(R.layout.activity_base);
        initView();
        View mainView = View.inflate(this, getMainContentResId(), null);
        mainView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        main_view.addView(mainView, 0);
        setSupportActionBar(base_toolbar);
        if (getToolBarResID() == 0) {
            base_toolbar.setVisibility(View.GONE);
        } else {
            View titleView = View.inflate(this, getToolBarResID(), null);
            titleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            base_toolbar.addView(titleView);
            if (setTitleColor() != 0)
                base_toolbar.setBackgroundColor(ContextCompat.getColor(mContext, setTitleColor()));
        }
        afterView();
    }

    private void keepFullScreen() {
        if (isKeepFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseActivityManager.getInstance().removeActivity(this);
    }


    public void finishAll() {
        BaseActivityManager.getInstance().finishAllActivity();
    }

    public void finishSingleActivity(Class clazz) {
        BaseActivityManager.getInstance().finishActivity(clazz);
    }

    public void showInput(View view) {
        InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm!=null){
            imm.showSoftInput(view, 0);
        }
    }

    public void hideInput(View view) {
        InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm!=null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showBaseLoading(){
        if (loadingDialog==null){
            loadingDialog=new LoadingDialog(mContext,R.style.no_trans_dialog);
        }
        loadingDialog.show();
    }

    public void hideBaseLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }


    public PermissionListener listener;
    public int REQUEST_CODE = 0;

    public Boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }else {
            return true;
        }
    }


    public void requestPermission(String permission, int requestCode, PermissionListener listener) {
        this.listener = listener;
        REQUEST_CODE = requestCode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission}, requestCode);
            } else {
                listener.onPermissionSuccess(requestCode);
            }
        } else {
            listener.onPermissionSuccess(requestCode);
        }
    }

    public interface PermissionListener {
        void onPermissionSuccess(int requestCode);
        void onPermissionFailed(int requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length == 0) {
                listener.onPermissionSuccess(requestCode);
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listener.onPermissionSuccess(requestCode);
            } else {
                listener.onPermissionFailed(requestCode);
            }
        }
    }

    private Long currentTime;
    @Override
    protected void onResume() {
        super.onResume();
        currentTime=System.currentTimeMillis();

    }

    public String getScreenUrl() {
        String sensorsName=BundleUrl.sensorsMaps.get(getClass().getName());
        if (TextUtils.isEmpty(sensorsName)){
            sensorsName=getClass().getName();
        }else {
            sensorsName=sensorsName.split("\\|")[0];
        }
        return sensorsName;
    }


    /**
     * 神策---pageView
     */
    private void sensorsTrackActivityIn(){
        String sensorsName=BundleUrl.sensorsMaps.get(getClass().getName());
        if (!TextUtils.isEmpty(sensorsName)){
            try {
                JSONObject params=new JSONObject();
                params.put("from_page", RunCacheDataUtil.INSTANCE.getLastPage());
                params.put("is_login",!TextUtils.isEmpty(PreferenceUtil.getString(Constant.USER_ID)));
                GsManager.getInstance().onEvent(sensorsName.split("\\|")[1],params);
                RunCacheDataUtil.INSTANCE.setLastPage(sensorsName.split("\\|")[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            RunCacheDataUtil.INSTANCE.setLastPage(getClass().getName());
        }

    }

    /**
     * 神策---pageStay
     */
    private void sensorTrackActivityStay(Long duration){
        String sensorsName=BundleUrl.sensorsMaps.get(getClass().getName());
        if (!TextUtils.isEmpty(sensorsName)){
            try {
                JSONObject params=new JSONObject();
                params.put("event_duration",duration);
                params.put("page_name",sensorsName.split("\\|")[0]);
                params.put("from_page",RunCacheDataUtil.INSTANCE.getLastPage());
                params.put("is_login",!TextUtils.isEmpty(PreferenceUtil.getString(Constant.USER_ID)));
                GsManager.getInstance().onEvent(sensorsName.split("\\|")[2],params);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorTrackActivityStay(System.currentTimeMillis()-currentTime);
    }

    public Boolean isKeepFullScreen(){
        return false;
    }

    public abstract int getToolBarResID();

    public abstract int getMainContentResId();

    public int setTitleColor(){
        return R.color.defaultColor;
    }

    public abstract void afterView();
}
