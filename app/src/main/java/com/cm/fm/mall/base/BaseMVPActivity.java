package com.cm.fm.mall.base;

import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

public abstract class BaseMVPActivity<P extends BasePresenter> extends BaseActivity implements IBaseView {

    //mPresenter 是 BasePresenter 子类
    protected P mPresenter;
    protected View curView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curView = View.inflate(getContext(),initLayout(),null);
        activityAnim();
        setContentView(curView);
        mPresenter = createPresenter();
        if(mPresenter != null){
            mPresenter.bindView(this);
        }
        initDataFront();
        initView();
        initDataEnd();
    }
    //根据需求是否重写
    /** activity 进入退出动画 */
    protected void activityAnim(){}
    /** 初始化数据 */
    protected void initDataFront(){}
    protected void initDataEnd(){}

    //抽象方法必须重写
    /** 获取布局id */
    protected abstract int initLayout();
    /** 初始化view */
    protected abstract void initView();
    /** 初始化presenter */
    protected abstract P createPresenter();



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public Context getContext() {
        return this;
    }
}
