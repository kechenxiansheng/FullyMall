package com.cm.fm.mall.presenter.activity;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.contract.activity.AddressContract;
import com.cm.fm.mall.model.model.activity.AddressModel;

public class AddressPresenter extends BasePresenter<AddressContract.Model,AddressContract.View> implements AddressContract.Presenter {

    @Override
    protected AddressContract.Model createModule() {
        return new AddressModel();
    }

    @Override
    public void init() {

    }
}
