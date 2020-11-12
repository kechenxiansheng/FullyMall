package com.cm.fm.mall.presenter.activity;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.contract.activity.CommodityContract;
import com.cm.fm.mall.model.model.activity.CommodityModel;


public class CommodityPresenter extends BasePresenter<CommodityContract.Model,CommodityContract.View> implements CommodityContract.Presenter {
    @Override
    protected CommodityContract.Model createModule() {
        return new CommodityModel();
    }

    @Override
    public void init() {

    }
}
