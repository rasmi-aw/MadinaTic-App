package com.techload.madinatic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphicUtils {

    /*convert a given bitmap to a rounded bitmap*/
    private static final class RoundedImage extends Drawable {
        private Bitmap mBitmap;
        private Paint mPaint;
        private RectF mRectF;
        private int mBitmapWidth;
        private int mBitmapHeight;

        public RoundedImage(Bitmap bitmap) {
            mBitmap = bitmap;
            mRectF = new RectF();
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
            mBitmapWidth = mBitmap.getWidth();
            mBitmapHeight = mBitmap.getHeight();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight / 2, Math.min((mBitmapWidth / 2), mBitmapHeight / 2), mPaint);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mRectF.set(bounds);
        }

        @Override
        public void setAlpha(int alpha) {
            if (mPaint.getAlpha() != alpha) {
                mPaint.setAlpha(alpha);
                invalidateSelf();
            }
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

        @Override
        public int getIntrinsicWidth() {
            return mBitmapWidth;
        }

        @Override
        public int getIntrinsicHeight() {
            return mBitmapHeight;
        }

        public void setAntiAlias(boolean aa) {
            mPaint.setAntiAlias(aa);
            invalidateSelf();
        }

        @Override
        public void setFilterBitmap(boolean filter) {
            mPaint.setFilterBitmap(filter);
            invalidateSelf();
        }

        @Override
        public void setDither(boolean dither) {
            mPaint.setDither(dither);
            invalidateSelf();
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

    }

    /**/
    public void toCircledImage(final ImageView imageView, final String path) {

        if (imageView != null)
            new AsyncTask<Void, Void, Void>() {
            Drawable circledBitmap;

            @Override
            protected Void doInBackground(Void... voids) {
                circledBitmap = new RoundedImage(BitmapFactory.decodeFile(path));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                imageView.setImageDrawable(circledBitmap);
            }
        }.execute();

    }

    /**/
    public static void toCircledImage(final ImageView imageView, final InputStream inputStream) {

        if (imageView != null)
            new AsyncTask<Void, Void, Void>() {
                Drawable circledBitmap;

                @Override
                protected Void doInBackground(Void... voids) {
                    circledBitmap = new RoundedImage(BitmapFactory.decodeStream(inputStream));
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    imageView.setImageDrawable(circledBitmap);
                }
            }.execute();

    }

    /**/
    public static void toCircledImage(final ImageView imageView, final Resources resources, final int id) {

        if (imageView != null)
            new AsyncTask<Void, Void, Void>() {
                Drawable circledBitmap;

                @Override
                protected Void doInBackground(Void... voids) {
                    Bitmap bitmap = BitmapFactory.decodeResource(resources, id);
                    if (bitmap != null) circledBitmap = new RoundedImage(bitmap);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (circledBitmap != null) imageView.setImageDrawable(circledBitmap);
                }
            }.execute();
    }

    /**/
    public static void toCircledImage(final ImageView imageView, final Bitmap bitmap) {

        new AsyncTask<Void, Void, Void>() {
            Drawable circledBitmap;

            @Override
            protected Void doInBackground(Void... voids) {
                circledBitmap = new RoundedImage(bitmap);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                imageView.setImageDrawable(circledBitmap);
            }
        }.execute();

    }

    /**/
    public static Bitmap loadCompressedBitmap(int compressionSize, String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = compressionSize;
        return BitmapFactory.decodeFile(imagePath, options);
    }
    /**/

    public interface REQUEST {
        int PHOTO_FROM_CAMERA = 123;
        int PHOTO_FROM_GALLERY = 129;
        int VIDEO_FROM_CAMERA = 133;
        int VIDEO_FROM_GALLERY = 139;
    }
}