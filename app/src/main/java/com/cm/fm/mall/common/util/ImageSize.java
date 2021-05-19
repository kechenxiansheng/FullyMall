package com.cm.fm.mall.common.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * 图片压缩工具类
 *
 * 如何高效的加载Bitmap：采用 BitmapFactory.Options 来加载所需尺寸的图片。
 *      假设通过ImageView 显示图片，很多时候ImageView并没有图片的原始尺寸大，这时将图片加载进来给ImageView，显然是没必要的，因为ImageView并没有办法显示原始的图片。
 * 通过BitmapFactory.Options就可以按一定的采样率来加载缩小后的图片，缩小后的图片在ImageView中显示，这样就会降低内存占用，从而在一定程度上避免了OOM，
 * 提高Bitmap加载时的性能。而BitmapFactory 提供了四中方法都支持Options参数对图片进行采样压缩。
 *      BitmapFactory.Options缩放图片，主要用到了他的inSampleSize参数，即采样率。当inSampleSize 为 1 时，表示不压缩；当inSampleSize 大于1时，比如2时，
 * 采样后的图片，其宽高均为原图片的大小的1/2，而像素为原图的1/4，占有内存也是原图的1/4。比如一张1024*1024像素的图片，假定采用ARGB8888格式存储，那么占用内存为1024*1024*4，
 * 即4M，如果inSampleSize为2，那么采样后的图片占有内存只有512*512*4，即1M。
 *      inSampleSize 取值，官方建议是2的指数，比如1/2/4/8/16等，如果不为2的指数，则系统会向下取整，比如传3，则取2。
 *
 * 获取采样率流程：
 *      1、将BitmapFactory.Options的 inJustDecodeBounds 参数设置为 true，并加载图片
 *      2、从BitmapFactory.Options取出图片的原始高宽信息，分别对应于outWidth、outHeight参数
 *      3、根据采样率规则并结合目标View的所需大小计算出采样率 inSampleSize
 *      4、将BitmapFactory.Options的 inJustDecodeBounds 参数设置为 false，然后重新加载图片
 *
 */
public class ImageSize {
    private static final String TAG = "ADP_ImageSize";

    public ImageSize() {
    }

    /**
     * 高效的加载图片 之 通过 资源id 加载 bitmap
     * @param res
     * @param resId
     * @param reqWidth      view宽度
     * @param reqHeight     view高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resId,options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res,resId,options);
    }

    /**
     * 高效的加载图片 之 通过 文件描述符（FileDescriptor） 加载 bitmap
     * @param descriptor
     * @param reqWidth      view宽度
     * @param reqHeight     view高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor descriptor, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(descriptor,null,options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(descriptor,null,options);
    }

    /**
     * calculateInSampleSize：计算采样率
     * @param options
     * @param reqWidth      view宽度
     * @param reqHeight     view高度
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        //取出图片的原始高宽
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        Log.d(TAG, "calculateInSampleSize: "+ imageWidth +"-"+imageHeight);
        //默认采样率为1，不压缩
        int inSampleSize = 1;
        //如果图片比ImageView宽高大，则计算采样率
        if(imageWidth > reqWidth || imageHeight > reqHeight){
            int halfHeight = imageHeight/2;
            int halfWidth = imageWidth/2;
            //如果bitmap的一半值除以采样率的值还是比view大，则将采样率以2的指数增长
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }
        }
        Log.d(TAG, "calculateInSampleSize,inSampleSize: "+ inSampleSize);
        return inSampleSize;
    }
}
