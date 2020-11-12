package com.cm.fm.mall.presenter.activity;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.contract.activity.UserSelfContract;
import com.cm.fm.mall.model.bean.UserInfo;
import com.cm.fm.mall.model.model.activity.UserSelfModel;

import java.util.List;

public class UserSelfPresenter extends BasePresenter<UserSelfContract.Model,UserSelfContract.View> implements UserSelfContract.Presenter {
    @Override
    protected UserSelfContract.Model createModule() {
        return new UserSelfModel();
    }

    @Override
    public void init() {

    }

    @Override
    public List<UserInfo> queryUserInfo() {
        if(isViewBind()){
            return getModel().queryUserInfo();
        }
        return null;
    }

    @Override
    public boolean updateUserInfo(UserInfo userInfo,String name,int sex) {
        if(isViewBind()){
            return getModel().updateUserInfo(userInfo,name,sex);
        }
        return false;
    }
}
