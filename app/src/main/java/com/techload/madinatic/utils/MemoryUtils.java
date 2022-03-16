package com.techload.madinatic.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSchemas;
import com.techload.madinatic.data.networking.DataCachingOperation;
import com.techload.madinatic.lists_items.Media;

import java.io.File;
import java.util.ArrayList;

public class MemoryUtils {

    /**/
    public static void deleteCachedImages(Media[] media) {

        if (media != null)
            for (int i = 0; i < media.length; i++) {

                if (media[i].type == Media.TYPE_IMAGE
                        && media[i].path.contains("cache")
                        && media[i].path.contains("images")) {

                    File file = new File(media[i].path);
                    if (file.exists() && !file.isDirectory()) file.delete();
                }
            }
    }

    /**/
    public static void deleteCachedImages(ArrayList<Media> media) {

        for (int i = 0; i < media.size(); i++) {
            Media current = media.get(i);
            if (current.type == Media.TYPE_IMAGE
                    && current.path.contains("cache")
                    && current.path.contains("images")) {

                File file = new File(current.path);
                if (file.exists() && !file.isDirectory()) file.delete();
            }
        }
    }

    public static void deleteCachedImages(SQLiteDatabase writableDB, Context context) {
        File imagesDir = new File(context.getCacheDir().getPath() + File.separator + DataCachingOperation.DataType.IMAGE);
        if (imagesDir != null && imagesDir.exists() && imagesDir.isDirectory()) {

            File[] files = imagesDir.listFiles();
            if (files.length > 0)
                for (File f : files) {

                    if (writableDB.query(InternalDataBaseSchemas.Medias.TABLE_NAME,
                            null,
                            InternalDataBaseSchemas.Medias.COLUMN_CACHED_PATH + " = ? ",
                            new String[]{f.getPath()},
                            null,
                            null,
                            null).getCount() != 1) f.delete();

                }

        }
        // deleting important images (profile picture changed or downloaded multiple times)
        imagesDir = new File(context.getCacheDir().getPath() + File.separator + DataCachingOperation.DataType.IMPORTANT_IMAGE);
        if (imagesDir != null && imagesDir.exists() && imagesDir.isDirectory()) {

            File[] files = imagesDir.listFiles();
            if (files.length > 0)
                for (File f : files) {
                    if (f.getPath().equals(MainActivity.profilePicturePath)) continue;
                    if (writableDB.query(InternalDataBaseSchemas.Posts.TABLE_NAME,
                            null,
                            InternalDataBaseSchemas.Posts.COLUMN_POST_OWNER_PROFILE_PICTURE_CACHED_PATH + " = ? ",
                            new String[]{f.getPath()},
                            null,
                            null,
                            null).getCount() < 1) f.delete();

                }

        }

    }

    public static void removeImagesFromRam(ArrayList<Media> medias) {
        for (int i = 0; i < medias.size(); i++) {
            if (medias.get(i).type == Media.TYPE_IMAGE)
                MainActivity.allAppImages.remove(medias.get(i).path);
        }
    }

}
