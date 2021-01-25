package com.cm.fm.mall.contract.activity;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.common.Callback;

public interface BindPhoneContract {
    interface Model extends IBaseModel {
        void savePhoneM(String account,String phoneNum, Callback callback);
    }

    interface View extends IBaseView {
        void OnSaveResult(int code,String msg);
    }

    interface Presenter{
        void savePhoneP(String account,String password);
    }
}
