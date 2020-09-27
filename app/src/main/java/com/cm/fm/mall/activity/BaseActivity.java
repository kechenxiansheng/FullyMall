package com.cm.fm.mall.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * 定义所有activity的父类
 */
public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "TAG_BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate : "  + this.getLocalClassName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy : " + this.getLocalClassName());
        ActivityCollector.removeActivity(this);

    }

    @Override   //Resume：中止后继续的意思
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume : " + this.getLocalClassName());

    }

    @Override   //Pause 暂停、停顿
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause ："  + this.getLocalClassName());

    }
}
