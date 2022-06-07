package com.shunlai.common.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

/**
 * @author Liu
 * @Date 2021/6/1
 * @mobile 18711832023
 */
@GlideModule
public class GlideCache extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {

        //设置缓存的大小为500M
        int cacheSize = 512*1024*1024;
        builder.setDiskCache(  new DiskLruCacheFactory(FileUtil.getCacheFileRootPath(context), cacheSize));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

    }
}
