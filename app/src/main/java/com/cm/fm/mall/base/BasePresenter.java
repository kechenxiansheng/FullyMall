package com.cm.fm.mall.base;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * 弱引用的 get() 函数，用来获取绑定的 view
 * @param <M> Model
 * @param <V> View
 */
public abstract class BasePresenter<M extends IBaseModel,V extends IBaseView> {
    /** 使用弱引用持有view，否则容易导致内存泄露 */
    private WeakReference<V> mvpView;
    /** model */
    private M mvpModel;

    /** 绑定view 和 model */
    public void bindView(V view){
        mvpView = new WeakReference<>(view);
        if(mvpModel == null){
            mvpModel = createModule();
        }
    }

    /** 解绑view */
    public void unBindView(){
        if(mvpView != null){
            mvpView.clear();
            mvpView = null;
        }
        mvpModel = null;
    }

    /** 是否与View建立连接 */
    protected boolean isViewBind(){
        return mvpView != null && mvpView.get() != null;
    }

    protected V getView(){
        return isViewBind() ? mvpView.get() : null;
    }
    protected M getModel(){
        return mvpModel;
    }
    protected Context getContext(){
        return getView().getContext();
    }
    protected void showLoading(){
        getView().showLoading();
    }
    protected void hideLoading(){
        getView().hideLoading();
    }
    protected void toast(String s){
        getView().toast(s);
    }

    /** 创建 Module */
    protected abstract M createModule();
    /** 初始化 */
    public abstract void init();
}
