package com.cm.fm.mall.base;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cm.fm.mall.common.util.LogUtil;

import androidx.fragment.app.Fragment;

/**
 * 碎片父类
 * 模板方法设计模式
 */
public abstract class BaseMVPFragment<P extends BasePresenter> extends Fragment implements IBaseView {
    private String tag = "TAG_BaseMVPFragment";
    protected P mPresenter;
    protected View curView;

    public abstract int initLayout();      //初始化布局资源文件
    public abstract void initView(View view);   //初始化组件
    /** 初始化数据 */
    protected void initDataFront(){}
    protected void initDataEnd(){}
    /** 初始化presenter */
    protected abstract P createPresenter();
    public abstract void dataDestroy();     //销毁数据，释放内存


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //加载视图
        curView = LayoutInflater.from(getActivity()).inflate(initLayout(), container, false);
        LogUtil.d(tag,"cur view is null ? " + (curView==null));
        mPresenter = createPresenter();
        if(mPresenter != null){
            mPresenter.bindView(this);
        }
        initDataFront();
        initView(curView);
        initDataEnd();
        return curView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataDestroy();
        System.gc();
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
