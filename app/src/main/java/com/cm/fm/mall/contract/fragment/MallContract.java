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

    }

    interface Presenter{
        List<ProductMsg> search(List<ProductMsg> productMsgs,String searchContent);
    }
}
