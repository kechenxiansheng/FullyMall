package com.cm.fm.mall.contract.activity;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.model.bean.UserInfo;

import java.util.List;

public interface UserSelfContract {
    interface Model extends IBaseModel {
        List<UserInfo> queryUserInfo();
        void updateUserInfo(UserInfo userInfo,Callback callback);
    }

    interface View extends IBaseView {
        void OnUpdateResult(int code,String msg);
    }

    interface Presenter{
        List<UserInfo> queryUserInfo();
        void updateUserInfo(UserInfo userInfo);
    }
}
