package com.cm.fm.mall.presenter.activity;

import android.util.Log;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.base.ResponseCallback;
import com.cm.fm.mall.contract.activity.BindPhoneContract;
import com.cm.fm.mall.model.constant.MallConstant;
import com.cm.fm.mall.model.model.activity.BindPhoneModel;

public class BindPhonePresenter extends BasePresenter<BindPhoneContract.Model,BindPhoneContract.View> implements BindPhoneContract.Presenter {

    private String tag = "TAG_BindPhonePresenter";

    @Override
    protected BindPhoneContract.Model createModule() {
        return new BindPhoneModel();
    }

    @Override
    public void init() {

    }

    @Override
    public void savePhoneP( final String phoneNum) {
        if(isViewBind()){
            getView().showLoading();
            /** 调用 model 类的 bindPhone 方法，处理数据 */
            getModel().savePhoneM(phoneNum, new ResponseCallback() {
                @Override
                public void success(Object response) {
                    getView().hideLoading();
                    Log.d(tag,"response : " + response.toString());
                    //通知activity 手机已绑定，并保存成功
                    getView().OnSaveResult(MallConstant.SUCCESS,"");
                }
                @Override
                public void fail(String info) {
                    getView().OnSaveResult(MallConstant.FAIL,info);
                    getView().hideLoading();
                }
                @Override
                public void error(String error) {
                    Log.d(tag,"register error: " + error);
                    getView().OnSaveResult(MallConstant.ERROR,"");
                    getView().hideLoading();
                }
            });
        }
    }
}
