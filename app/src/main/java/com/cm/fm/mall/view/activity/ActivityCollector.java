package com.cm.fm.mall.view.activity;

import android.app.Activity;
import android.util.Log;

import com.cm.fm.mall.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * activity 收集器
 * 管理所有activity
 */
public class ActivityCollector {
    public static final String TAG = "TAG_ActivityCollector";
    public static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity){
        LogUtil.d(TAG,"addActivity:" +activity.getLocalClassName());
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity){
        LogUtil.d(TAG,"removeActivity:" +activity.getLocalClassName());
        activityList.remove(activity);
    }

    /**
     * 关闭所有activity
     */
    public static void finishAll(){
        LogUtil.d(TAG,"finishAll");
        for (Activity act:activityList) {
            //isFinishing：true表示当前activity状态是正在finish
            if(!act.isFinishing()){
                act.finish();
            }
        }
    }
    /**
     * 关闭指定的Activity
     * @Param activityName 类名
     * */
    public static void finishOneActivity(String activityName){
        Log.d(TAG,"activityName : " + activityName);
        //在activities集合中找到类名与指定类名相同的Activity就关闭
        for (Activity activity : activityList){
            String name = activity.getClass().getName();//activity的包名+类名
            Log.d(TAG,"name : " + name);    //com.cm.fm.mall.view.activity.MainActivity
            if(name.equals(activityName)){
                if(activity.isFinishing()){
                    //当前activity如果已经Finish，则只从activities清除就好了
                    activityList.remove(activity);
                } else {
                    //没有Finish则Finish
                    activity.finish();
                }
            }
        }
    }
}
