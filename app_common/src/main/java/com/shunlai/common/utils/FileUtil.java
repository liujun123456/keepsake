package com.shunlai.common.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import top.zibin.luban.Luban;


/**
 * Created by jason on 2016/9/19.
 */
public class FileUtil {
    private static final String DIR_IMAGE = "/cache_image";
    private static final String ROOT_DIR = "/shunlai";


    /**
     * 保存图片
     * @param ctx
     * @param b
     * @return
     */
    public static String saveBitmap(Context ctx,Bitmap b){
        String jpegName =getCacheFileRootPath(ctx) + DIR_IMAGE+"/"+System.currentTimeMillis()+".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return jpegName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }


    /**
     * 复制文件
     * @param source
     * @param target
     */
    public static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 压缩图片
     * @param mContext
     * @param file
     * @return
     */
    public static File luBanPicture(Context mContext,File file){
        try {
            List<File> files= Luban.with(mContext).setTargetDir(getDefaultImagePath(mContext).getAbsolutePath()).load(file).get();
            return files.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 获取缓存图片的默认地址
     * @param ctx
     * @return
     */
    public static File getDefaultImagePath(Context ctx) {
        File file = new File(getCacheFileRootPath(ctx) + DIR_IMAGE);
        if (!file.exists()) file.mkdirs();
        return file;
    }
    /**
     * 获取缓存目录路径
     * @return
     */
    public static String getCacheFileRootPath(Context ctx){
        return ctx.getCacheDir() + File.separator + ROOT_DIR ;
    }

    /**
     * 删除单个文件
     * @param url
     * @return
     */
    public static boolean deleteFile(String url) {
        boolean result = false;
        File file = new File(url);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }


    /**
     * 删除某个文件目录，包括目录下的所有文件
     * @param context
     * @param runnable
     */
    public static void cleanCache(Context context,Runnable runnable){
        deleteFolderFile(getCacheFileRootPath(context),true);
        runnable.run();
    }

    public static void deleteFolderFile(String filePath, boolean deleteThisPath){
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取图片缓存大小
     * @param ctx
     * @return
     */
    public static float getCacheSize(Context ctx){
        float size = getFolderSize(new File(getCacheFileRootPath(ctx)));
        float size_show = (float) (Math.round(size / 1024.0f / 1024 * 100)) / 100;
        if (size_show == 0) size_show = size == 0 ? 0 : 0.01f;
        return size_show;
    }

    private static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) size = size + getFolderSize(fileList[i]);
                else size = size + fileList[i].length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    /**
     * @desc 图片转字节数组
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @desc view转bitmap
     * @param view
     * @return
     */
    public static Bitmap createBitmapFromView(View view) {
        Bitmap bitmap = null;
        //开启view缓存bitmap
        view.setDrawingCacheEnabled(true);
        //设置view缓存Bitmap质量
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //获取缓存的bitmap
        Bitmap cache = view.getDrawingCache();
        if (cache != null && !cache.isRecycled()) {
            bitmap = Bitmap.createBitmap(cache);
        }
        //销毁view缓存bitmap
        view.destroyDrawingCache();
        //关闭view缓存bitmap
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static String saveBitmapToPhoto(Context context,Bitmap bitmap)  {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        String filename;//声明文件名
        //以保存时间为文件名
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
        filename =  sdf.format(date);
        File file = new File(extStorageDirectory, filename+".JPEG");//创建文件，第一个参数为路径，第二个参数为文件名
        try {
            outStream = new FileOutputStream(file);//创建输入流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
            //这三行可以实现相册更新
             Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
             Uri uri = Uri.fromFile(file);intent.setData(uri);
            context.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！
            ToastUtilKt.toast("图片已保存到相册!");
            return file.getAbsolutePath();
        } catch(Exception e) {
            ToastUtilKt.toast("保存相册失败!");
        }
        return "";
    }
}
