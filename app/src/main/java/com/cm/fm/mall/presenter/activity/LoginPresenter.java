package com.cm.fm.mall.presenter.activity;

import android.util.Log;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.contract.activity.LoginContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.model.model.activity.LoginModel;
import com.cm.fm.mall.view.activity.LoginActivity;

/**
 * 注意 BasePresenter 的泛型必须添加，使之关联上，否则无法直接调用 model#login 和 view#OnLoginSuccess 等方法
 */
public class LoginPresenter extends BasePresenter<LoginContract.Model,LoginContract.View> implements LoginContract.Presenter {

    private final String TAG = "FM_LoginPresenter";

    @Override
    public void loginP(final String account, final String password) {
        if(isViewBind()){
            getView().showLoading();
            /** 调用 model类的login方法，处理数据 */
            getModel().loginM(account,password, new Callback() {
                @Override
                public void success(Object response) {
                    getView().hideLoading();
                    Log.d(TAG,"response : " + response.toString());
                    //通知activity 登陆检验成功
                    getView().OnLoginResult(MallConstant.SUCCESS,account);
                }
                @Override
                public void fail(String info) {
                    toast(info);
                    getView().hideLoading();
                }
            });

            //使用延迟执行模拟登陆验证过程
//            new android.os.Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            },1000);

        }
    }

    @Override
    protected LoginContract.Model createModule() {
        return new LoginModel();
    }

    @Override
    public void init() {

    }
}
