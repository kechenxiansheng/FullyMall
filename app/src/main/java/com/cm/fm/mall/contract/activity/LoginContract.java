package com.cm.fm.mall.contract.activity;


import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.base.ResponseCallback;

/**
 * 登陆的契约类
 * 定义登陆的Model、View、Presenter接口，方便查看三者之间的关系
 * 登录流程：点击登录按钮，调用 Presenter#login 方法，在login方法中调用 Model#login 方法，并使用回调返回登陆结果，并执行视图操作 View#OnLoginSuccess
 */
public interface LoginContract {
    interface Model extends IBaseModel {
        void loginM(String account,String password, ResponseCallback callback);
    }

    interface View extends IBaseView {
        void OnLoginResult(int code,String msg);
    }

    interface Presenter{
        void loginP(String account,String password);
    }
}
