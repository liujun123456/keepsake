/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shunlai.publish.picker.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.SparseArray;

import androidx.loader.content.CursorLoader;

import com.shunlai.publish.picker.entity.Album;
import com.shunlai.publish.picker.entity.SelectionSpec;


/**
 * Load all albums (grouped by bucket_id) into a single cursor.
 */
public class AlbumLoader extends CursorLoader {
    public static final String COLUMN_COUNT = "count";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            COLUMN_COUNT};
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            "COUNT(*) AS " + COLUMN_COUNT};

    private static final String[] PROJECTION_29 = {
        MediaStore.Files.FileColumns._ID,
        "bucket_id",
        "bucket_display_name",
        MediaStore.MediaColumns.DATA};


    // === params for showSingleMediaType: false ===
    private static final String SELECTION =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";

    private static final String SELECTION_29 =
        "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";


    private static final String[] SELECTION_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };
    // =============================================

    // === params for showSingleMediaType: true ===
    private static final String SELECTION_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";

    private static final String SELECTION_FOR_SINGLE_MEDIA_TYPE_29 =
        MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";


    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }
    // =============================================

    private static final String BUCKET_ORDER_BY = "datetaken DESC";

    private AlbumLoader(Context context, String selection, String[] selectionArgs, String[] projection) {
        super(context, QUERY_URI, projection, selection, selectionArgs, BUCKET_ORDER_BY);
    }

    public static CursorLoader newInstance(Context context) {
        String selection;
        String[] projection;
        String[] selectionArgs;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            projection=PROJECTION_29;
            if (SelectionSpec.showType==1) {
                selection = SELECTION_FOR_SINGLE_MEDIA_TYPE_29;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            } else if (SelectionSpec.showType==2) {
                selection = SELECTION_FOR_SINGLE_MEDIA_TYPE_29;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            } else {
                selection = SELECTION_29;
                selectionArgs = SELECTION_ARGS;
            }
        }else {
            projection=PROJECTION;
            if (SelectionSpec.showType==1) {
                selection = SELECTION_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            } else if (SelectionSpec.showType==2) {
                selection = SELECTION_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            } else {
                selection = SELECTION;
                selectionArgs = SELECTION_ARGS;
            }
        }

        return new AlbumLoader(context, selection, selectionArgs,projection);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);
        int totalCount = 0;
        String allAlbumCoverPath = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (albums == null) {
                return allAlbum;
            }
            SparseArray<Album> albumList = new SparseArray<>();
            while (albums.moveToNext()) {
                int bucketId = albums.getInt(albums.getColumnIndex("bucket_id"));
                String bucketName = albums.getString(albums.getColumnIndex("bucket_display_name"));
                Album album = albumList.get(bucketId);
                if (album == null) {
                    album = new Album(String.valueOf(bucketId), allAlbumCoverPath, bucketName, 0L);
                    albumList.append(bucketId, album);
                }
                album.addCaptureCount();
            }
            for (int i = 0; i < albumList.size(); i++) {
                totalCount += albumList.valueAt(i).getCount();
            }
            if (albums.moveToFirst()) {
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
                allAlbum.addRow(new String[]{Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, allAlbumCoverPath,
                    String.valueOf(totalCount)});
            }
            for (int i = 0; i < albumList.size(); i++) {
                Album album = albumList.valueAt(i);
                totalCount += album.getCount();
                allAlbum.addRow(new String[]{album.getId(), album.getId(), album.getDisplayName(getContext()),
                    album.getCoverPath(), String.valueOf(album.getCount())});
            }
            return allAlbum;
        }else {
            if (albums != null) {
                while (albums.moveToNext()) {
                    totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT));
                }
                if (albums.moveToFirst()) {
                    allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
                }
            }
            allAlbum.addRow(new String[]{Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, allAlbumCoverPath,
                String.valueOf(totalCount)});

            return new MergeCursor(new Cursor[]{allAlbum, albums});
        }
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}
