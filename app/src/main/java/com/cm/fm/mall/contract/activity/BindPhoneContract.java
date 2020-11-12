package com.cm.fm.mall.contract.activity;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.base.ResponseCallback;

public interface BindPhoneContract {
    interface Model extends IBaseModel {
        void savePhoneM(String phoneNum, ResponseCallback callback);
    }

    interface View extends IBaseView {
        void OnSaveResult(int code,String msg);
    }

    interface Presenter{
        void savePhoneP(String password);
    }
}
