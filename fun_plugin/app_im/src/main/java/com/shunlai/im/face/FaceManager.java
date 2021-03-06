package com.shunlai.im.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.widget.EditText;
import android.widget.TextView;


import com.shunlai.common.BaseApplication;
import com.shunlai.im.R;
import com.shunlai.im.utils.ScreenUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FaceManager {

    private static final int drawableWidth = ScreenUtil.getPxByDp(BaseApplication.mInstance,32);
    private static ArrayList<Emoji> emojiList = new ArrayList<>();
    private static LruCache<String, Bitmap> drawableCache = new LruCache(1024);
    private static Context context = BaseApplication.mInstance;
    private static String[] emojiFilters = context.getResources().getStringArray(R.array.emoji_filter_key);
    private static String[] emojiFilters_values = context.getResources().getStringArray(R.array.emoji_filter_value);

    public static ArrayList<Emoji> getEmojiList() {
        return emojiList;
    }


    public static String[] getEmojiFiltersValues(){
        return emojiFilters_values;
    }

    public static String[] getEmojiFilters(){
        return emojiFilters;
    }

    public static void loadFaceFiles() {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < emojiFilters.length; i++) {
                    loadAssetBitmap(emojiFilters[i], "emoji/" + emojiFilters[i] + "@2x.png", true);
                }
            }
        }.start();


    }


    private static Emoji loadAssetBitmap(String filter, String assetPath, boolean isEmoji) {
        InputStream is = null;
        try {
            Emoji emoji = new Emoji();
            Resources resources = context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = DisplayMetrics.DENSITY_XXHIGH;
            options.inScreenDensity = resources.getDisplayMetrics().densityDpi;
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
            context.getAssets().list("");
            is = context.getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(0, 0, drawableWidth, drawableWidth), options);
            if (bitmap != null) {
                drawableCache.put(filter, bitmap);

                emoji.setIcon(bitmap);
                emoji.setFilter(filter);
                if (isEmoji) {
                    emojiList.add(emoji);
                }

            }
            return emoji;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // ???????????????????????????
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // ?????????????????????????????????????????????
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // ???????????????????????????????????????inSampleSize???????????????????????????????????????????????????
            // ?????????????????????????????????????????????
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // ??????????????????inJustDecodeBounds?????????true????????????????????????
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // ?????????????????????????????????inSampleSize???
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // ??????????????????inSampleSize?????????????????????
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static boolean isFaceChar(String faceChar) {
        return drawableCache.get(faceChar) != null;
    }


    public static void handlerEmojiText(TextView comment, String content, boolean typing) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "\\[(\\S+?)\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        boolean imageFound = false;
        while (m.find()) {
            String emojiName = m.group();
            Bitmap bitmap = drawableCache.get(emojiName);
            if (bitmap != null) {
                imageFound = true;
                sb.setSpan(new ImageSpan(context, bitmap),
                        m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        // ????????????????????????????????????????????????????????????????????????????????????
        if (!imageFound && typing) {
            return;
        }
        int selection = comment.getSelectionStart();
        comment.setText(sb);
        if (comment instanceof EditText) {
            ((EditText) comment).setSelection(selection);
        }
    }

    public static Bitmap getEmoji(String name) {
        return drawableCache.get(name);
    }
}
