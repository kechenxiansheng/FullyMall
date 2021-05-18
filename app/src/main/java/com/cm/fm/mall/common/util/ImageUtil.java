package com.cm.fm.mall.common.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Base64;

import com.cm.fm.mall.common.MallConstant;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.cm.fm.mall.MyApplication.getContext;

/**
 * 图片工具类
 */
public class ImageUtil {
    private static final String TAG = "FM_ImageUtil";

    /** bitmap 转为 string */
    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //第二个参数 压缩比重，图片存储在磁盘上的大小会根据这个值变化。值越小存储在磁盘的图片文件越小（如果是 PNG 格式，第二个参数无效）
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        // 转为byte数组
        byte[] bytes = outputStream.toByteArray();
        String encode = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encode;
    }
    /** string 转为 bitmap */
    public static Bitmap stringToBitmap(String bitmapStr){
        try {
            byte[] bitmapArray = Base64.decode(bitmapStr, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /** 裁剪成圆形图片 */
    public static Bitmap createCircleBitmap(Bitmap bitmap) {
        //获取图片的宽度
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int r =0;
        if (width < height){
            r = width;
        }else {
            r = height;
        }
        //创建一个与原bitmap一样宽度的正方形bitmap
        Bitmap roundBitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);
        //以该bitmap为底，创建一块画布
        Canvas canvas = new Canvas(roundBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
        RectF rectF = new RectF(0,0,r,r);
        //画圆
        canvas.drawRoundRect(rectF, r/2, r/2, paint);
        //设置画笔为取交集模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //裁剪图片
        canvas.drawBitmap(bitmap, null, rectF, paint);

        return roundBitmap;
    }
    /** 将图片缓存本地 */
    public static void saveHeadCache(Bitmap bitmap){
        if(bitmap == null){
            LogUtil.e(TAG,"save error.bitmap is null");
            return;
        }
        //本地缓存路径（缓存在外部存储的-External）
        String path = getContext().getExternalFilesDir(DIRECTORY_PICTURES) + File.separator + MallConstant.PHOTO_NAME;
        //path: /storage/emulated/0/Android/data/com.cm.fm.mall/files/Pictures/head_photo.jpg
        LogUtil.d(TAG,"savePhoto path:"+path);
        File headPhoto = new File(path);
        if(headPhoto.exists()){
            boolean delete = headPhoto.delete();
            LogUtil.d(TAG,"savePhoto delete old photo : " + delete);
        }
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(headPhoto));
            //第二个参数 压缩比重，图片存储在磁盘上的大小会根据这个值变化。值越小存储在磁盘的图片文件越小（如果是 PNG 格式，第二个参数无效）
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            LogUtil.d(TAG,"savePhoto end");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
