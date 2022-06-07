package com.shunlai.im.video;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.shunlai.common.utils.FileUtil;
import com.shunlai.common.utils.RunIntentKey;
import com.shunlai.common.utils.ToastUtilKt;
import com.shunlai.im.R;
import com.shunlai.im.utils.Constant;
import com.shunlai.im.video.listener.ClickListener;
import com.shunlai.im.video.listener.ErrorListener;
import com.shunlai.im.video.listener.IUIKitCallBack;
import com.shunlai.im.video.listener.JCameraListener;


public class CameraActivity extends Activity {

    public static IUIKitCallBack mCallBack;
    private JCameraView jCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        jCameraView = findViewById(R.id.jcameraview);
        //设置视频保存路径
        //jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");

        int state = getIntent().getIntExtra(Constant.CAMERA_TYPE, JCameraView.BUTTON_STATE_BOTH);
        jCameraView.setFeatures(state);
        if (state == JCameraView.BUTTON_STATE_ONLY_CAPTURE) {
            jCameraView.setTip(getString(R.string.tap_capture));
        } else if (state == JCameraView.BUTTON_STATE_ONLY_RECORDER) {
            jCameraView.setTip(getString(R.string.tap_video));
        }

        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                ToastUtilKt.toast(getString(R.string.audio_permission_error));
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                String path = FileUtil.saveBitmap(CameraActivity.this,bitmap);
                if (mCallBack != null) {
                    mCallBack.onSuccess(path,1);
                }
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame, long duration) {
                //获取视频路径
                String path = FileUtil.saveBitmap(CameraActivity.this, firstFrame);
                Intent intent = new Intent();
                intent.putExtra(Constant.IMAGE_WIDTH, firstFrame.getWidth());
                intent.putExtra(Constant.IMAGE_HEIGHT, firstFrame.getHeight());
                intent.putExtra(Constant.VIDEO_TIME, duration);
                intent.putExtra(RunIntentKey.CAMERA_IMG_PATH, path);
                intent.putExtra(RunIntentKey.CAMERA_VIDEO_PATH, url);
                firstFrame.getWidth();
                //setResult(-1, intent);
                if (mCallBack != null) {
                    mCallBack.onSuccess(intent,2);
                }
                finish();
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                CameraActivity.this.finish();
            }
        });
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                ToastUtilKt.toast("Right");
            }
        });
        //jCameraView.setVisibility(View.GONE);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCallBack = null;
    }

}
