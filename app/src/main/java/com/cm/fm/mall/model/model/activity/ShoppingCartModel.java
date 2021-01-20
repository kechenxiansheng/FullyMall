package com.cm.fm.mall.model.model.activity;

import com.cm.fm.mall.common.Callback;
import com.cm.fm.mall.contract.activity.ShoppingCartContract;
import com.cm.fm.mall.model.bean.ShoppingProduct;
import com.cm.fm.mall.model.bean.UserInfo;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ShoppingCartModel implements ShoppingCartContract.Model {

    private String tag = "TAG_ShoppingCartModel";
    @Override
    public List<ShoppingProduct> queryAllShoppingCartProducts() {
        return DataSupport.findAll(ShoppingProduct.class);
    }

    @Override
    public void checkLoginM(Callback callback) {
        List<UserInfo> userInfos = DataSupport.findAll(UserInfo.class);
        if(userInfos.size()==0){
            //说明是首次使用app，去注册
            callback.success(-1);
        }else {
            int userType = userInfos.get(0).getUserType();
            if(userType == 0){
                //游客状态去登陆
                callback.success(1);
            }else {
                //登录状态，进行结算等处理
                callback.success(0);
            }
        }
    }
}
