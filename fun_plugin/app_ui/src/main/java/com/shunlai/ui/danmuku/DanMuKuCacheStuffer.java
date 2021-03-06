package com.shunlai.ui.danmuku;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;

import com.shunlai.ui.R;

import java.util.Map;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.AndroidDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;

/**
 * @author : 贺金龙
 * email : 753355530@qq.com
 * create at 2018/7/13  9:06
 * description :
 */
public class DanMuKuCacheStuffer extends BaseCacheStuffer {

    /**
     * 文字右边间距
     */
    private float RIGHTMARGE;
    /**
     * 文字和头像间距
     */
    private float LEFTMARGE;
    /**
     * 文字和右边线距离
     */
    private int TEXT_RIGHT_PADDING;
    /**
     * 文字大小
     */
    private float TEXT_SIZE;
    /**
     * 头像的大小
     */
    private float IMAGEHEIGHT;

    private float padding;

    private float imagePadding;

    public DanMuKuCacheStuffer(Activity activity) {
        // 初始化固定参数，这些参数可以根据自己需求自行设定
        LEFTMARGE = activity.getResources().getDimension(R.dimen.DIMEN_4dp);
        RIGHTMARGE = activity.getResources().getDimension(R.dimen.DIMEN_8dp);
        IMAGEHEIGHT = activity.getResources().getDimension(R.dimen.DIMEN_24dp);
        TEXT_SIZE = activity.getResources().getDimension(R.dimen.DIMEN_13dp);
        padding=activity.getResources().getDimension(R.dimen.DIMEN_2dp);
        imagePadding=activity.getResources().getDimension(R.dimen.DIMEN_1dp);
    }

    @Override
    public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
        // 初始化数据
        Map<String, Object> map = (Map<String, Object>) danmaku.tag;
        String content = (String) map.get("content");

        // 设置画笔
        paint.setTextSize(TEXT_SIZE);

        // 计算名字和内容的长度，取最大值
        float contentWidth = paint.measureText(content);

        // 设置弹幕区域的宽度
        danmaku.paintWidth = contentWidth + IMAGEHEIGHT + LEFTMARGE + RIGHTMARGE;
        // 设置弹幕区域的高度
        danmaku.paintHeight = IMAGEHEIGHT;
    }

    @Override
    public void clearCaches() {

    }

    @Override
    public void drawDanmaku(BaseDanmaku danmaku, Canvas canvas, float left, float top, boolean fromWorkerThread, AndroidDisplayer.DisplayerConfig displayerConfig) {
        // 初始化数据
        Map<String, Object> map = (Map<String, Object>) danmaku.tag;
        String content = (String) map.get("content");
        Bitmap bitmap = (Bitmap) map.get("bitmap");
        String color = (String) map.get("color");

        // 设置画笔
        Paint paint = new Paint();
        paint.setTextSize(TEXT_SIZE);

        //绘制背景
        int textLength = (int) paint.measureText(content);
        paint.setColor(Color.parseColor(color));
        float rectBgLeft = left;
        float rectBgTop = top;
        float rectBgRight = left + IMAGEHEIGHT + textLength + LEFTMARGE + RIGHTMARGE+padding;
        float rectBgBottom = top + IMAGEHEIGHT+padding*2;
        canvas.drawRoundRect(new RectF(rectBgLeft, rectBgTop, rectBgRight, rectBgBottom), rectBgBottom / 2, rectBgBottom / 2, paint);

        paint.setColor(Color.WHITE);

        // 绘制头像区域
        float avatorRight = left + IMAGEHEIGHT;
        float avatorBottom = top + IMAGEHEIGHT;

        //绘制头像背景
        canvas.drawOval(new RectF(left+padding, top+padding, avatorRight+padding, avatorBottom+padding), paint);

        //绘制具体头像
        canvas.drawBitmap(bitmap, null, new RectF(left+padding+imagePadding, top+padding+imagePadding, avatorRight+imagePadding, avatorBottom+imagePadding), paint);

        float contentLeft = left + IMAGEHEIGHT + LEFTMARGE+padding;
        //计算文字的相应偏移量
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        //为基线到字体上边框的距离,即上图中的top
        float textTop = fontMetrics.top;
        //为基线到字体下边框的距离,即上图中的bottom
        float textBottom = fontMetrics.bottom;
        float contentBottom = (top + IMAGEHEIGHT+padding*2) / 2;
        //基线中间点的y轴计算公式
        int baseLineY = (int) (contentBottom - textTop / 2 - textBottom / 2);
        //绘制文字
        canvas.drawText(content, contentLeft, baseLineY, paint);
    }
}

