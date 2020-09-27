package com.cm.fm.mall;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

//import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.mob.MobSDK;

import org.litepal.LitePal;

public class MyApplication extends Application {
    //提供一个全局的context
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        MultiDex.install(context);
        //初始化litepal
        LitePal.initialize(this);
        //smssdk 回传隐私授权结果（应用应该有个变量控制用户隐私授权的结果，将该变量的结果作为下面接口的第一个参数传递给SDK）
        MobSDK.submitPolicyGrantResult(true,null);
        //加载图片的框架
        Fresco.initialize(this);

        //百度定位sdk初始化，必须为application的上下文
//        SDKInitializer.initialize(context);
    }
    //只要app启动，任何位置都可调用此方法获取到context
    public static Context getContext(){
        return context;
    }
}
