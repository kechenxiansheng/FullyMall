package com.cm.fm.mall.contract.activity;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.common.Callback;

public interface UpdatePwdContract {
    interface Model extends IBaseModel {
        void savePwdM(String account,String password,Callback callback);
    }

    interface View extends IBaseView {
        void OnCheckResult(int code,String msg);
    }

    interface Presenter{
        //保存新密码
        void savePwdP(String account,String password);
    }
}
