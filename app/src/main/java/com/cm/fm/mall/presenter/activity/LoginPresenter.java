package com.cm.fm.mall.presenter.activity;

import android.util.Log;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.base.ResponseCallback;
import com.cm.fm.mall.contract.activity.LoginContract;
import com.cm.fm.mall.model.model.activity.LoginModel;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.model.constant.MallConstant;
import com.cm.fm.mall.view.activity.LoginActivity;

/**
 * 注意 BasePresenter 的泛型必须添加，使之关联上，否则无法直接调用 model#login 和 view#OnLoginSuccess 等方法
 */
public class LoginPresenter extends BasePresenter<LoginContract.Model,LoginContract.View> implements LoginContract.Presenter {

    private String tag = "TAG_LoginPresenter";

    @Override
    public void loginP(final String account, final String password) {
        if(isViewBind()){
            getView().showLoading();
            //使用演示执行模拟登陆验证过程
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /** 调用 model类的login方法，处理数据 */
                    getModel().loginM(account,password, new ResponseCallback() {
                        @Override
                        public void success(Object response) {
                            getView().hideLoading();
                            Log.d(tag,"response : " + response.toString());
                            UserInfo cur_userInfo = (UserInfo) response;
                            String pwd = cur_userInfo.getPassword();
                            if(!password.equals(pwd)){
                                getView().OnLoginResult(MallConstant.FAIL,"账号密码错误");
                                return;
                            }
                            //修改当前用户登陆的状态
                            cur_userInfo.setUserType(LoginActivity.USER_TYPE_IS_LOGIN);   //登陆态 正式用户
                            cur_userInfo.update(cur_userInfo.getId());
                            //通知activity 登陆检验成功
                            getView().OnLoginResult(MallConstant.SUCCESS,account);
                        }
                        @Override
                        public void fail(String info) {
                            toast(info);
                            getView().hideLoading();
                        }
                        @Override
                        public void error(String error) {
                            toast(error);
                            getView().hideLoading();
                        }
                    });
                }
            },1000);

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
