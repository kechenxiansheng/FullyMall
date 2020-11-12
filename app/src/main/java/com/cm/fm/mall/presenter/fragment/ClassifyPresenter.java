package com.cm.fm.mall.presenter.fragment;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.contract.fragment.ClassifyContract;
import com.cm.fm.mall.model.model.fragment.ClassifyModel;

public class ClassifyPresenter extends BasePresenter<ClassifyContract.Model,ClassifyContract.View> implements ClassifyContract.Presenter {
    @Override
    protected ClassifyContract.Model createModule() {
        return new ClassifyModel();
    }

    @Override
    public void init() {

    }

    @Override
    public String getJsonData(String fileName) {
        if(isViewBind()){
            return getModel().getJsonData(getContext(),fileName);
        }
        return "";
    }
}
