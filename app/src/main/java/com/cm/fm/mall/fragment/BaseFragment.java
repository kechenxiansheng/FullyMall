package com.cm.fm.mall.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.cm.fm.mall.util.LogUtil;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * 碎片父类
 */
public abstract class BaseFragment extends Fragment {
    private String tag = "TAG_BaseFragment";

    public abstract int getResource();      //初始化布局资源文件

    public abstract void init(View view);   //初始化组件

    public abstract void loadingData();    //加载数据，初始化数据，初始化对象

    public abstract void dataDestroy();    //销毁数据，释放内存

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //加载视图
        View view = LayoutInflater.from(getActivity()).inflate(getResource(), container, false);
        LogUtil.d(tag,"cur view is null ? " + (view==null));
        loadingData();
        init(view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
//        Fresco.getImagePipeline().pause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Fresco.getImagePipeline().resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataDestroy();
        System.gc();
    }
}
