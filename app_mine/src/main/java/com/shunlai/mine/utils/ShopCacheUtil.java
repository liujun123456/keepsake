package com.shunlai.mine.utils;

import android.content.Context;

import com.google.gson.JsonSyntaxException;
import com.shunlai.common.utils.PreferenceUtil;
import com.shunlai.mine.entity.bean.ShopBgBean;
import com.shunlai.mine.entity.bean.ShopDollBean;
import com.shunlai.net.util.GsonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Liu
 * @Date 2021/7/19
 * @mobile 18711832023
 */
public class ShopCacheUtil {

    private static final String SHOP_IMG="/shop_image";
    private static final String SHOP_BG_CACHE="SHOP_BG_FILE_CACHE";
    private static final String SHOP_DOLL_CACHE="SHOP_DOLL_FILE_CACHE";


    public static List<ShopBgBean> sceneDown=new ArrayList<>();
    public static List<ShopDollBean> dollDown=new ArrayList<>();
    public static void setNeedDownLoad(List<ShopBgBean> scene,List<ShopDollBean> doll){
        sceneDown=scene;
        dollDown=doll;
    }
    /**
     * 获取背景本地缓存配置
     * @return
     */
    public static List<ShopBgBean> getLocalShopBg(){
        List<ShopBgBean> shopBgBeanList=new ArrayList<>();
        Set<String> bg=PreferenceUtil.getSetString(SHOP_BG_CACHE);
        if (bg!=null){
            Iterator<String> iterator= bg.iterator();
            while (iterator.hasNext()){
                String value=iterator.next();
                try {
                    shopBgBeanList.add(GsonUtil.fromJson(value,ShopBgBean.class));
                }catch (JsonSyntaxException exception){

                }
            }
        }
        return shopBgBeanList;
    }

    /**
     * 保存背景本地缓存
     * @param beans
     */
    public static void saveLocalShopBg(List<ShopBgBean> beans){
        Set<String> data=new HashSet<>();
        for (int i=0;i<beans.size();i++){
            data.add(GsonUtil.toJson(beans.get(i)));
        }
        PreferenceUtil.putSetString(SHOP_BG_CACHE,data);
    }

    public static ShopBgBean getShopBgById(String id){
        List<ShopBgBean> shopBgBeanList=getLocalShopBg();
        for (int i=0;i<shopBgBeanList.size();i++){
            if (shopBgBeanList.get(i).getId().equals(id)){
                return shopBgBeanList.get(i);
            }
        }
        return null;
    }

    /**
     * 获取主理人形象本地缓存配置
     * @return
     */
    public static List<ShopDollBean>  getLocalShopDoll(){
        List<ShopDollBean> shopDollBeanList=new ArrayList<>();
        Set<String> bg=PreferenceUtil.getSetString(SHOP_DOLL_CACHE);
        if (bg!=null){
            Iterator<String> iterator= bg.iterator();
            while (iterator.hasNext()){
                String value=iterator.next();
                try {
                    shopDollBeanList.add(GsonUtil.fromJson(value,ShopDollBean.class));
                }catch (JsonSyntaxException exception){

                }
            }
        }
        return shopDollBeanList;
    }

    /**
     * 保存主理人形象配置
     * @param beans
     */
    public static void saveLocalShopDoll(List<ShopDollBean> beans){
        Set<String> data=new HashSet<>();
        for (int i=0;i<beans.size();i++){
            data.add(GsonUtil.toJson(beans.get(i)));
        }
        PreferenceUtil.putSetString(SHOP_DOLL_CACHE,data);
    }


    public static ShopDollBean getShopDollById(String id){
        List<ShopDollBean> shopDollBeanList=getLocalShopDoll();
        for (int i=0;i<shopDollBeanList.size();i++){
            if (shopDollBeanList.get(i).getId().equals(id)){
                return shopDollBeanList.get(i);
            }
        }
        return null;
    }

    /**
     * 获取店铺形象缓存根目录
     * @param ctx
     * @return
     */
    public static String getShopCacheRootPath(Context ctx){
        File file=new File(ctx.getFilesDir() + File.separator + SHOP_IMG);
        if (!file.exists()) file.mkdirs();
        return file.getAbsolutePath();
    }


    /**
     * 解压文件
     * @param zipFileString
     * @param outPathString
     * @throws Exception
     */
    public static void unZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()){
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }
}
