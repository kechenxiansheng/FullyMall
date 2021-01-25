package com.cm.fm.mall.contract.fragment;

import android.content.Context;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.model.bean.ProductMsg;

import java.util.List;

public interface MallContract {
    interface Model extends IBaseModel {
        List<ProductMsg> search(List<ProductMsg> productMsgs,String searchContent);
    }

    interface View extends IBaseView {
        void OnSearchResult(int code,List<ProductMsg> productMsgs);
    }

    interface Presenter{
        void search(List<ProductMsg> productMsgs,String searchContent);
    }
}
