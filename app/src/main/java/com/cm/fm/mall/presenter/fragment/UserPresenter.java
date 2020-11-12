package com.cm.fm.mall.presenter.fragment;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.contract.fragment.UserContract;
import com.cm.fm.mall.model.model.fragment.UserModel;

public class UserPresenter extends BasePresenter<UserContract.Model,UserContract.View> implements UserContract.Presenter {
    @Override
    protected UserContract.Model createModule() {
        return new UserModel();
    }

    @Override
    public void init() {

    }
}
