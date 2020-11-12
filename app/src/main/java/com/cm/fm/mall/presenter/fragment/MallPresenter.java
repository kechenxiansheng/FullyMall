package com.cm.fm.mall.presenter.fragment;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.contract.fragment.MallContract;
import com.cm.fm.mall.model.bean.ProductMsg;
import com.cm.fm.mall.model.model.fragment.MallModel;

import java.util.List;

public class MallPresenter extends BasePresenter<MallContract.Model,MallContract.View> implements MallContract.Presenter {
    @Override
    protected MallContract.Model createModule() {
        return new MallModel();
    }

    @Override
    public void init() {

    }

    @Override
    public List<ProductMsg> search(List<ProductMsg> productMsgs,String searchContent) {
        if(isViewBind()){
            return getModel().search(productMsgs,searchContent);
        }
        return null;
    }
}
