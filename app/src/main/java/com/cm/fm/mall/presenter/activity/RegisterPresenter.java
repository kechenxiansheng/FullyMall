package com.cm.fm.mall.presenter.activity;

import android.util.Log;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.base.ResponseCallback;
import com.cm.fm.mall.contract.activity.RegisterContract;
import com.cm.fm.mall.model.constant.MallConstant;
import com.cm.fm.mall.model.model.activity.RegisterModel;

public class RegisterPresenter extends BasePresenter<RegisterContract.Model,RegisterContract.View> implements RegisterContract.Presenter {
    private String tag = "TAG_RegisterPresenter";

    @Override
    protected RegisterContract.Model createModule() {
        return new RegisterModel();
    }

    @Override
    public void init() {

    }

    @Override
    public void registerP(final String account, final String password) {
        if(isViewBind()){
            getView().showLoading();
            //使用延时执行模拟登陆验证过程
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /** 调用 model类的 register 方法，处理数据 */
                    getModel().registerM(account,password, new ResponseCallback() {
                        @Override
                        public void success(Object response) {
                            getView().hideLoading();
                            Log.d(tag,"response : " + response.toString());
                            //通知activity 注册成功
                            getView().OnRegisterResult(MallConstant.SUCCESS,account);
                        }
                        @Override
                        public void fail(String info) {
                            getView().OnRegisterResult(MallConstant.FAIL,info);
                            getView().hideLoading();
                        }
                        @Override
                        public void error(String error) {
                            Log.d(tag,"register error: " + error);
                            getView().OnRegisterResult(MallConstant.ERROR,"");
                            getView().hideLoading();
                        }
                    });
                }
            },1000);

        }
    }
}
