package com.cm.fm.mall.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Display;
import android.view.Window;
import android.widget.Toast;

import com.cm.fm.mall.R;
import com.cm.fm.mall.activity.LoginActivity;
import com.cm.fm.mall.activity.RegisterActivity;
import com.cm.fm.mall.activity.ShoppingCartActivity;
import com.cm.fm.mall.bean.AddressInfo;
import com.cm.fm.mall.bean.ProductMsg;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ForwardScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Utils {
    private static Utils utils;

    public static Utils getInstance(){
        if(utils==null){
            utils = new Utils();
        }
        return utils;
    }
    //activity跳转
    public void startActivityCloseOther(Activity curAct, Class targetAct){
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
    //activity跳转,并关闭发起跳转的activity
    public void startActivityCloseAnimation(Activity curAct, Class targetAct){
        Intent intent = new Intent(curAct,targetAct);
        curAct.startActivity(intent);
        curAct.finish();
    }
    public void tips(Context activity,String msg) {
        Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
    }
    //通知
    public void sendNotification(Context context, String channelId, String title, String contentText, int notificationId ){
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = null;
        if(Build.VERSION.SDK_INT >= 26){
            NotificationChannel channel = new NotificationChannel(channelId,channelId + "_name",NotificationManager.IMPORTANCE_HIGH);
            if (manager != null) {
                //创建 channel
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
        builder.setSmallIcon(R.mipmap.icon_dog);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.icon_dog));
        builder.setAutoCancel(true);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);    //通知使用系统默认配置（音效，震动，呼吸灯等）
        assert manager != null;
        manager.notify(notificationId,builder.build());
    }

    /** 裁剪成圆形图片 */
    public Bitmap createCircleBitmap(Bitmap resource) {
        //获取图片的宽度
        int width = resource.getWidth();
        int height = resource.getHeight();

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
        canvas.drawBitmap(resource, null, rectF, paint);

        return roundBitmap;
    }

    /**
     * 加测参数是否有空
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
     * @param activity 当前activityd
     * @param transitionId 是用的动画资源id （R.transition.fade）
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
}
