package com.cm.fm.mall.common.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Display;
import android.view.Window;
import android.widget.Toast;

import com.cm.fm.mall.R;
import com.cm.fm.mall.model.bean.AddressInfo;
import com.cm.fm.mall.model.bean.ProductMsg;
import com.cm.fm.mall.view.activity.ShoppingCartActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.core.app.NotificationCompat;


public class Utils {
    private static Utils utils;

    public static Utils getInstance(){
        if(utils==null){
            utils = new Utils();
        }
        return utils;
    }
    //activity跳转
    public void startActivityCloseSelf(Activity curAct, Class targetAct){
        Intent intent = new Intent(curAct, targetAct);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        curAct.startActivity(intent);
        curAct.finish();

    }
    //activity跳转
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startActivity(Activity curAct, Class targetAct){
        Intent intent = new Intent(curAct,targetAct);
        curAct.startActivity(intent);
    }

    //activity跳转
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startActivityWithData(Activity curAct, Class targetAct,String key,String data){
        Bundle bundle = new Bundle();
        bundle.putString(key,data);
        Intent intent = new Intent(curAct,targetAct);
        intent.putExtras(bundle);
        curAct.startActivity(intent);
        curAct.finish();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startActivityAnimation(Activity curAct, Class targetAct){
        Intent intent = new Intent(curAct,targetAct);
        curAct.startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(curAct).toBundle());
    }
    //activity跳转 携带activityId 用于区分activity
    public void startActivity(Activity curAct, Class targetAct,int activityId){
        Intent intent = new Intent(curAct,targetAct);
        intent.putExtra("activityId",activityId);
        curAct.startActivity(intent);
    }
    //activity跳转,并携带数据
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startActivityData(Activity curAct, Class targetAct, ProductMsg productMsg){
        Intent intent = new Intent(curAct,targetAct);
        intent.putExtra("product",productMsg);
        curAct.startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(curAct).toBundle());
    }
    //activity 携带数据跳转，需要回传
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startActivityDataForResult(Activity curAct, Class targetAct, int requestCode, AddressInfo info){
        Intent intent = new Intent(curAct,targetAct);
        intent.putExtra("addressInfo",info);
        curAct.startActivityForResult(intent,requestCode,ActivityOptions.makeSceneTransitionAnimation(curAct).toBundle());
    }
    //activity跳转需要回传
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startActivityForResultAnimation(Activity curAct, Class targetAct, int requestCode){
        Intent intent = new Intent(curAct,targetAct);
        curAct.startActivityForResult(intent,requestCode,ActivityOptions.makeSceneTransitionAnimation(curAct).toBundle());
    }

    //activity跳转,并关闭发起跳转的activity
    public void startActivityClose(Activity curAct, Class targetAct){
        Intent intent = new Intent(curAct,targetAct);
        curAct.startActivity(intent);
        curAct.finish();
    }
    /** 跳转,并关闭其他所有的activity */
    public void startActivityCloseAll(Activity curAct, Class targetAct){
        Intent intent = new Intent(curAct,targetAct);
        /** Intent.FLAG_ACTIVITY_CLEAR_TASK 会清除之前所有的activity，并且需要搭配 Intent.FLAG_ACTIVITY_NEW_TASK 一起使用
         *  Intent.FLAG_ACTIVITY_NEW_TASK 其实是将activity 的启动模式定义为 singleTask 模式
         * */
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        curAct.startActivity(intent);
        curAct.finish();
    }
    //activity跳转,并关闭发起跳转的activity
    public void startActivityCloseAnimation(Activity curAct, Class targetAct){
        Intent intent = new Intent(curAct,targetAct);
        curAct.startActivity(intent);
        curAct.finish();
    }
    public void tips(Context activity,String msg) {
        if(!isMainThread()){
            Looper.prepare();
        }
        Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
        if(!isMainThread()){
            Looper.loop();
        }
    }
    /** 通知 */
    public void sendNotification(Context context, String channelId, String title, String contentText, int notificationId ){
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = null;
        if(Build.VERSION.SDK_INT >= 26){
            NotificationChannel channel = new NotificationChannel(channelId,channelId,NotificationManager.IMPORTANCE_HIGH);
            if (manager != null) {
                //创建 channel（手机通知设置中可以看到此channel）
                manager.createNotificationChannel(channel);
            }
            //api 大于26 必须加上channelId
            builder = new Notification.Builder(context,channelId);
        }else {
            builder = new Notification.Builder(context);
        }
        builder.setContentTitle(title);
        builder.setContentText(contentText);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.red_tip);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.icon_dog));
        builder.setAutoCancel(true);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);    //通知使用系统默认配置（音效，震动，呼吸灯等）

        //设置点击跳转
        Intent intent = new Intent(context,ShoppingCartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //相当于将activity设置为 singleTask
        PendingIntent hangPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        /** 大于21，使用悬挂式通知 */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setFullScreenIntent(hangPendingIntent, true);
        }
        assert manager != null;
        manager.notify(notificationId,builder.build());
    }



    /**
     * 检测参数是否有空
     * @param params
     * @return 检测结果 true（没有空值）false（有空值）
     */
    public boolean checkParameter(String ... params){
        boolean result = true;
        for (String param : params) {
            if(TextUtils.isEmpty(param) || param.trim().length()==0){
                result  = false;
                break;
            }
        }
        return result;
    }

    /**
     * activity 进入，退出，再次进入使用动画
     * @param activity 当前activity
     * @param transitionId 动画资源id （R.transition.fade）
     */
    public void actUseAnim(Activity activity,int transitionId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition slide = TransitionInflater.from(activity).inflateTransition(transitionId);
            activity.getWindow().setExitTransition(slide);    //退出
            activity.getWindow().setEnterTransition(slide);   //进入
            activity.getWindow().setReenterTransition(slide); //再次进入
        }

    }

    /**
     * 获取屏幕高宽尺寸
     * @param context
     * @return
     */
    public List<Integer> getSize(Activity context){
        List<Integer> list = new ArrayList<>();
        //TODO 获取手机的宽高
        Display display = context.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        list.add(width);
        list.add(height);
        return list;
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 判断是否在主线程中
     */
    public boolean isMainThread(){
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * 判断是否支持拍照功能
     * @param activity
     * @return
     */
    public boolean hasCamera(Activity activity){
        PackageManager pm = activity.getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)         //后置摄像头
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)             //前置摄像头
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
                || Camera.getNumberOfCameras() > 0;
        /*
        android 21 开始 Camera.getNumberOfCameras() 定义为过时，可使用 CameraManager.getCameraIdList() 代替
        */
//        CameraManager mManager = (CameraManager)activity.getSystemService(Context.CAMERA_SERVICE);
//        String[] mCameraIds = null;
//        try {
//            if (mManager != null) {
//                mCameraIds = mManager.getCameraIdList();
//            }
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }

        return hasACamera;
    }

    /**
     * 时间格式
     * @param format    转换的格式
     * @param date      需要转换的时间
     * @return          指定格式的时间字符串
     */
    public static String convertDate(String format,Date date){
        if(TextUtils.isEmpty(format)){
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
