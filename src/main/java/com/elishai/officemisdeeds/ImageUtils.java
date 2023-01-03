package com.elishai.officemisdeeds;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class ImageUtils {

    //Conservative scale method
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHieght) {
        //Reading the size of an image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth  = options.outWidth;
        float srcHeight = options.outHeight;

        //Calculating the scaling
        int inSampleSize = 1;
        if(srcHeight > destHieght || srcWidth > destWidth) {
            float heightScale = srcHeight / destHieght;
            float widthScale = srcWidth / destWidth;
            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Reading the data and creating the final image
        return  BitmapFactory.decodeFile(path, options);
    }
}
