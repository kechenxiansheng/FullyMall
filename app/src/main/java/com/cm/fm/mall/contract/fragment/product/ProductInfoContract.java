package com.cm.fm.mall.contract.fragment.product;

import android.app.Activity;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.model.bean.ProductMsg;

public interface ProductInfoContract {
    interface Model extends IBaseModel {
        boolean saveProductData(ProductMsg productMsg,int buyNum);
    }

    interface View extends IBaseView {

    }

    interface Presenter{
        boolean saveProductData(ProductMsg productMsg,int buyNum);
        int getScreenSize(Activity activity);
    }
}
