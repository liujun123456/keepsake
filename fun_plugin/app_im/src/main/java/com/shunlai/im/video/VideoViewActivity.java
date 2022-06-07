package com.shunlai.im.video;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shunlai.common.utils.RunIntentKey;
import com.shunlai.im.R;
import com.shunlai.im.utils.Constant;
import com.shunlai.im.utils.ScreenUtil;
import com.shunlai.im.video.util.ImageUtil;


public class VideoViewActivity extends Activity {

    private UIKitVideoView mVideoView;
    private int videoWidth = 0;
    private int videoHeight = 0;
    private ImageView firstFrame;
    private RelativeLayout rl_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_view);
        mVideoView = findViewById(R.id.video_play_view);
        rl_load=findViewById(R.id.rl_load);
        firstFrame=findViewById(R.id.iv_first_frame);

        String imagePath = getIntent().getStringExtra(RunIntentKey.CAMERA_IMG_PATH);
        if (!TextUtils.isEmpty(imagePath)){
            com.shunlai.common.utils.ImageUtil.INSTANCE.showPreView(firstFrame,this,Uri.parse(imagePath));
        }
        Uri videoUri = getIntent().getParcelableExtra(RunIntentKey.CAMERA_VIDEO_PATH);
        Bitmap firstFrame = ImageUtil.getBitmapFormPath(imagePath);
        if (firstFrame != null) {
            videoWidth = firstFrame.getWidth();
            videoHeight = firstFrame.getHeight();
            updateVideoView();
        }

        mVideoView.setVideoURI(videoUri);
        mVideoView.setOnPreparedListener(mediaPlayer ->{
            rl_load.setVisibility(View.GONE);
            videoWidth = mediaPlayer.getVideoWidth();
            videoHeight = mediaPlayer.getVideoHeight();
            updateVideoView();
            mVideoView.start();
            }
        );
        mVideoView.setOnClickListener(v -> {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
            } else {
                mVideoView.start();
            }
        });

        findViewById(R.id.video_view_back).setOnClickListener(v -> {
            mVideoView.stop();
            finish();
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateVideoView();

    }

    private void updateVideoView() {
        if (videoWidth <= 0 && videoHeight <= 0) {
            return;
        }
        boolean isLandscape = true;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isLandscape = false;
        }

        int deviceWidth;
        int deviceHeight;
        if (isLandscape) {
            deviceWidth = Math.max(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this));
            deviceHeight = Math.min(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this));
        } else {
            deviceWidth = Math.min(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this));
            deviceHeight = Math.max(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this));
        }
        int[] scaledSize = ScreenUtil.scaledSize(deviceWidth, deviceHeight, videoWidth, videoHeight);
        ViewGroup.LayoutParams params = mVideoView.getLayoutParams();
        params.width = scaledSize[0];
        params.height = scaledSize[1];
        mVideoView.setLayoutParams(params);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mVideoView != null) {
            mVideoView.stop();
        }
    }
}
