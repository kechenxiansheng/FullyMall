package com.cm.fm.mall.contract.activity;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;

/**
 * 注册的契约类
 */
public interface RegisterContract {
    interface Model extends IBaseModel {
        void registerM(String account,String password, Callback callback);
    }

    interface View extends IBaseView {
        void OnRegisterResult(int code,String msg);
    }

    interface Presenter{
        void registerP(String account,String password);
    }
}
