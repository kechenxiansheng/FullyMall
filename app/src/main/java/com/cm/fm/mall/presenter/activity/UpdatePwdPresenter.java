package com.cm.fm.mall.presenter.activity;

import android.util.Log;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.base.ResponseCallback;
import com.cm.fm.mall.contract.activity.UpdatePwdContract;
import com.cm.fm.mall.model.constant.MallConstant;
import com.cm.fm.mall.model.model.activity.UpdatePwdModel;

public class UpdatePwdPresenter extends BasePresenter<UpdatePwdContract.Model,UpdatePwdContract.View> implements UpdatePwdContract.Presenter{
    private String tag = "TAG_UpdatePwdPresenter";

    @Override
    protected UpdatePwdContract.Model createModule() {
        return new UpdatePwdModel();
    }

    @Override
    public void init() {

    }

    @Override
    public void savePwdP(String password) {
        if(isViewBind()){
            getView().showLoading();
            /** 调用 model 类的 savePwd 方法，处理数据 */
            getModel().savePwdM(password, new ResponseCallback() {
                @Override
                public void success(Object response) {
                    getView().hideLoading();
                    Log.d(tag,"response : " + response.toString());
                    //通知activity 密码已修改，并保存成功
                    getView().OnCheckResult(MallConstant.SUCCESS,"");
                }
                @Override
                public void fail(String info) {
                    getView().OnCheckResult(MallConstant.FAIL,info);
                    getView().hideLoading();
                }
                @Override
                public void error(String error) {
                    Log.d(tag,"register error: " + error);
                    getView().OnCheckResult(MallConstant.ERROR,"");
                    getView().hideLoading();
                }
            });
        }
    }
}
