package com.cm.fm.mall.presenter.activity;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.base.ResponseCallback;
import com.cm.fm.mall.contract.activity.ShoppingCartContract;
import com.cm.fm.mall.model.bean.ShoppingProduct;
import com.cm.fm.mall.model.model.activity.ShoppingCartModel;

import java.util.List;

public class ShoppingCartPresenter extends BasePresenter<ShoppingCartContract.Model,ShoppingCartContract.View> implements ShoppingCartContract.Presenter {

    private String tag = "TAG_ShoppingCartPresenter";

    @Override
    protected ShoppingCartContract.Model createModule() {
        return new ShoppingCartModel();
    }

    @Override
    public void init() {

    }

    @Override
    public List<ShoppingProduct> queryAllShoppingCartProducts() {
        return getModel().queryAllShoppingCartProducts();
    }

    @Override
    public double calculateTotals(List<ShoppingProduct> products){
        Double total = 0.0;
        if(isViewBind()){
            for (ShoppingProduct shoppingProduct:products) {
                total += (shoppingProduct.getPrice() * shoppingProduct.getBuyNum());
            }
        }
        return total;
    }

    @Override
    public void checkLoginP() {
        if(isViewBind()){
            getModel().checkLoginM(new ResponseCallback() {
                @Override
                public void success(Object response) {
                    int code = (int) response;
                    getView().OnLoginCheckResult(code,"");
                }

                @Override
                public void fail(String info) {

                }

                @Override
                public void error(String error) {

                }
            });
        }
    }
}
