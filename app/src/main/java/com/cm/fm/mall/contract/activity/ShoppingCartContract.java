package com.cm.fm.mall.contract.activity;

import com.cm.fm.mall.base.IBaseModel;
import com.cm.fm.mall.base.IBaseView;
import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.model.bean.ShoppingProduct;

import java.util.List;

/**
 * 购物车契约类
 */
public interface ShoppingCartContract {
    interface Model extends IBaseModel {
        List<ShoppingProduct> queryAllShoppingCartProducts();
        void checkLoginM(Callback callback);
    }

    interface View extends IBaseView {
        void OnLoginCheckResult(int code,String msg);
    }

    interface Presenter{
        List<ShoppingProduct> queryAllShoppingCartProducts();
        double calculateTotals(List<ShoppingProduct> products);
        void checkLoginP();
    }
}
