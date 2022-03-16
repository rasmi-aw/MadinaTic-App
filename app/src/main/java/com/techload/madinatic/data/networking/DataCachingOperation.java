package com.techload.madinatic.data.networking;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.techload.madinatic.R;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/* The point of this class is to take some data and put it into cache directory in a
 structured way and return the path to your or vice versa (better put in an other thread than ui thread)*/
public class DataCachingOperation {
    private Context context;
    private String path;

    public DataCachingOperation(Context context) {
        this.context = context;
    }

    /**/
    public interface DataType {
        String TEXT = "texts";
        String IMAGE = "images";
        String IMPORTANT_IMAGE = "important_images";
        String VIDEO = "videos";
        String AUDIO = "audios";
        String OTHER = "others";/*for every other type of data*/
    }


    /**
     * returns null: in case of a starting error (no element is put in the cache)
     * files paths: in case of a successful caching operation
     * null in the failed element count: in case we couldn't put it into the cache
       subdir is a directory name in case it's null or empty, no directory will be created*/

    public String putFileInCache(String dataType, Object data) {
        String path = null;
        //
        File cacheDir = context.getCacheDir();
        File categoryDir = new File(cacheDir, dataType);

        if (data != null) {
            switch (dataType) {

                case DataType.TEXT:
                    try {
                        String fileName = categoryDir + File.separator +
                                ("text_" + context.getApplicationContext().getString(R.string.app_name) + new Date().getTime() + ".txt");
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
                        bufferedWriter.write((String) data);
                        path = fileName;
                        bufferedWriter.flush();
                        bufferedWriter.close();
                    } catch (IOException e) {
                    }
                    break;


                case DataType.IMAGE:
                    try {
                        categoryDir = new File(cacheDir, DataType.IMAGE);
                        categoryDir.mkdir();
                        String fileName = categoryDir + File.separator +
                                ("pic_" + context.getApplicationContext().getString(R.string.app_name) + "_" + new Date().getTime() + ".webp");
                        FileOutputStream outputStream = new FileOutputStream(fileName);
                        boolean imageCompressedInFile = ((Bitmap) data).compress(Bitmap.CompressFormat.WEBP, 50, outputStream);
                        path = fileName;
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                    }
                    break;


                case DataType.VIDEO:
                    try {
                        categoryDir = new File(cacheDir, DataType.VIDEO);
                        categoryDir.mkdir();
                        String fileName = categoryDir + File.separator + ("video_" +
                                context.getApplicationContext().getString(R.string.app_name) + "_" + new Date().getTime());
                        FileOutputStream outputStream = new FileOutputStream(fileName);
                        //
                        BufferedInputStream bufferedInputStream = new BufferedInputStream((InputStream) data);
                        byte[] buffer = new byte[8192]; /*reading by 8192 byte*/
                        while ((bufferedInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer);
                        }
                        path = fileName;
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                    }

                    break;


                case DataType.AUDIO:
                    categoryDir = new File(cacheDir, DataType.AUDIO);
                    break;

                case DataType.IMPORTANT_IMAGE:

                    try {
                        categoryDir = new File(cacheDir, DataType.IMPORTANT_IMAGE);
                        categoryDir.mkdir();
                        String fileName = categoryDir + File.separator +
                                ("important_images_" + context.getApplicationContext().getString(R.string.app_name) + "_"
                                        + new Date().getTime() + ".webp");
                        FileOutputStream outputStream = new FileOutputStream(fileName);
                        boolean imageCompressedInFile = ((Bitmap) data).compress(Bitmap.CompressFormat.WEBP, 50, outputStream);
                        path = fileName;
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                    }
                    break;

                case DataType.OTHER:
                    categoryDir = new File(cacheDir, DataType.OTHER);

                    break;
            }

        }

        return path;
    }

    public void cacheFileInBackground(final String dataType, final Object data) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                //
                File cacheDir = context.getCacheDir();
                File categoryDir = new File(cacheDir, dataType);

                if (data != null) {
                    switch (dataType) {

                        case DataType.TEXT:
                            try {
                                String fileName = categoryDir + File.separator +
                                        ("text_" + context.getApplicationContext().getString(R.string.app_name) + new Date().getTime() + ".txt");
                                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
                                bufferedWriter.write((String) data);
                                path = fileName;
                                bufferedWriter.flush();
                                bufferedWriter.close();
                            } catch (IOException e) {
                            }
                            break;


                        case DataType.IMAGE:
                            try {
                                categoryDir = new File(cacheDir, DataType.IMAGE);
                                categoryDir.mkdir();
                                String fileName = categoryDir + File.separator +
                                        ("pic_" + context.getApplicationContext().getString(R.string.app_name) + "_" + new Date().getTime() + ".webp");
                                FileOutputStream outputStream = new FileOutputStream(fileName);
                                boolean imageCompressedInFile = ((Bitmap) data).compress(Bitmap.CompressFormat.WEBP, 50, outputStream);
                                path = fileName;
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                            }
                            break;


                        case DataType.VIDEO:
                            try {
                                categoryDir = new File(cacheDir, DataType.VIDEO);
                                categoryDir.mkdir();
                                String fileName = categoryDir + File.separator + ("video_" +
                                        context.getApplicationContext().getString(R.string.app_name) + "_" + new Date().getTime());
                                FileOutputStream outputStream = new FileOutputStream(fileName);
                                //
                                BufferedInputStream bufferedInputStream = new BufferedInputStream((InputStream) data);
                                byte[] buffer = new byte[8192]; /*reading by 8192 byte*/
                                while ((bufferedInputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer);
                                }
                                path = fileName;
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                            }

                            break;


                        case DataType.AUDIO:
                            categoryDir = new File(cacheDir, DataType.AUDIO);
                            break;

                        case DataType.IMPORTANT_IMAGE:

                            try {
                                categoryDir = new File(cacheDir, DataType.IMPORTANT_IMAGE);
                                categoryDir.mkdir();
                                String fileName = categoryDir + File.separator +
                                        ("important_images_" + context.getApplicationContext().getString(R.string.app_name) + "_"
                                                + new Date().getTime() + ".webp");
                                FileOutputStream outputStream = new FileOutputStream(fileName);
                                boolean imageCompressedInFile = ((Bitmap) data).compress(Bitmap.CompressFormat.WEBP, 50, outputStream);
                                path = fileName;
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                            }
                            break;

                        case DataType.OTHER:
                            categoryDir = new File(cacheDir, DataType.OTHER);

                            break;
                    }

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                doWhenFileCached(path);
            }
        }.execute();

    }

    public void doWhenFileCached(String filePath) {
    }


}//