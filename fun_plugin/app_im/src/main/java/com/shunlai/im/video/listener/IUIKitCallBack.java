package com.shunlai.im.video.listener;

/**
 * UIKit回调的通用接口类
 */
public interface IUIKitCallBack {

    void onSuccess(Object data,int type);

    void onError(String module, int errCode, String errMsg);
}
