package com.cm.fm.mall.contract.activity;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.base.ResponseCallback;
import com.cm.fm.mall.model.bean.UserInfo;

import java.util.List;

public interface UserSelfContract {
    interface Model extends IBaseModel {
        List<UserInfo> queryUserInfo();
        boolean updateUserInfo(UserInfo userInfo,String name,int sex);
    }

    interface View extends IBaseView {
    }

    interface Presenter{
        List<UserInfo> queryUserInfo();
        boolean updateUserInfo(UserInfo userInfo,String nickName,int sex);
    }
}
