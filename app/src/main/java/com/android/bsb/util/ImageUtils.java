package com.android.bsb.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    private static String TAG = "ImageUtils";

    public static List<File> compressImage(File rootDir,List<String> orgImageList){
        List<File> uploadFiles = new ArrayList<>();
        Bitmap smallBitmap = null;
        for (String filePath : orgImageList){
            smallBitmap = getSmallBitmap(filePath);
            int degree = readPictureDegree(filePath);
            if(degree != 0){
                smallBitmap = rotateBitmap(smallBitmap,degree);
            }
            File smallFile = saveCompressImage(rootDir,new File(filePath).getName(),smallBitmap);
            if(smallFile!=null){
                uploadFiles.add(smallFile);
            }
        }

        if(smallBitmap!=null && !smallBitmap.isRecycled()){
            smallBitmap.recycle();
            smallBitmap = null;
        }

        return uploadFiles;

    }



    private static Bitmap getSmallBitmap(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        options.inSampleSize = calculateInSampleSize(options,480,800);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path,options);
    }


    private static int readPictureDegree(String path){
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 190;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap,int degress){
        if(bitmap != null){
            Matrix matrix = new Matrix();
            matrix.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            return bitmap;
        }
        return bitmap;
    }


    private static File saveCompressImage(File rootDir,String fname,Bitmap bitmap){
        File file = new File(rootDir,"small_"+fname+".jpg");
        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,80,fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            AppLogger.LOGE(TAG,"saveImage error->"+e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            AppLogger.LOGE(TAG,"saveImage error->"+e.getMessage());
            e.printStackTrace();
            return null;
        }finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;

    }


    private static int calculateInSampleSize(BitmapFactory.Options options,int reqW,int reqH){
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if(height > reqH || width > reqW){
            int heightRatio = Math.round((float) height / reqH);
            int widthRatio = Math.round((float) width / reqW);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
