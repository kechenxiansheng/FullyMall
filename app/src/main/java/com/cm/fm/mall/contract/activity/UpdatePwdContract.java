package com.cm.fm.mall.contract.activity;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.base.ResponseCallback;

public interface UpdatePwdContract {
    interface Model extends IBaseModel {
        void savePwdM(String password,ResponseCallback callback);
    }

    interface View extends IBaseView {
        void OnCheckResult(int code,String msg);
    }

    interface Presenter{
        //保存新密码
        void savePwdP(String password);
    }
}
