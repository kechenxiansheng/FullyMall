package com.cm.fm.mall.presenter.fragment.product;

import android.app.Activity;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.contract.fragment.product.ProductInfoContract;
import com.cm.fm.mall.model.bean.ProductMsg;
import com.cm.fm.mall.model.model.fragment.product.ProductInfoModel;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.util.Utils;


import java.util.List;

public class ProductInfoPresenter extends BasePresenter<ProductInfoContract.Model,ProductInfoContract.View> implements ProductInfoContract.Presenter {
    private final String TAG = "FM_ProductInfoPresenter";

    @Override
    protected ProductInfoContract.Model createModule() {
        return new ProductInfoModel();
    }

    @Override
    public void init() {

    }

    @Override
    public boolean saveProductData(ProductMsg productMsg,int buyNum) {
        if(isViewBind()){
           return getModel().saveProductData(productMsg,buyNum);
        }
        return false;
    }

    @Override
    public int getScreenSize(Activity activity) {
        //TODO 获取手机的宽高
        List<Integer> sizeList = Utils.getSize(activity);
        int needWidth = 0;
        if (sizeList.size() != 0) {
            int width = sizeList.get(0);
            int height = sizeList.get(1);
            if (width < height) {
                needWidth = (int) (width * 0.6);
            } else {
                needWidth = (int) (height * 0.6);
            }
            LogUtil.d(TAG, "width:" + width + "  height:" + height);
        }
        LogUtil.d(TAG, "needWidth:" + needWidth);
        return needWidth;
    }
}
