package com.cm.fm.mall.model.model.fragment.product;

import android.content.ContentValues;

import com.cm.fm.mall.contract.fragment.product.ProductInfoContract;
import com.cm.fm.mall.model.bean.ProductMsg;
import com.cm.fm.mall.model.bean.ShoppingProduct;
import com.cm.fm.mall.util.LogUtil;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class ProductInfoModel implements ProductInfoContract.Model {
    private String tag = "TAG_ProductInfoModel";
    @Override
    public boolean saveProductData(ProductMsg productMsg,int buyNum) {
        //创建数据库和表
        Connector.getDatabase();
        LogUtil.d(tag,"saveProductData productMsg :"+productMsg.toString());
        //保存购买的商品(正常情况下应该保存在服务器)
        ShoppingProduct product = new ShoppingProduct();
        product.setProductID(productMsg.getProductID());
        product.setProductName(productMsg.getProductName());
        product.setPrice(productMsg.getPrice());
        product.setProductDescription(productMsg.getProductDescription());
        product.setExtension(productMsg.getExtension());
        product.setInventory(productMsg.getInventory());
        //保存商品前，查询数据库是否有与当前 相同的商品，有则将购买数量相加
        List<ShoppingProduct> savedShoppingProducts = DataSupport.select
                ("id","productID","productName","productDescription","type","price","inventory","buyNum","extension")
                .where("productID=?",productMsg.getProductID()+"").find(ShoppingProduct.class);
        //总的购买数量
        LogUtil.d(tag,"saveProductData buyNum :"+buyNum);
        int curBuyProductNum = buyNum;
        LogUtil.d(tag,"saveProductData size: "+savedShoppingProducts.size());
        if(savedShoppingProducts.size() > 0){
            //加上商品在数据库已保存的购买数量
            for (ShoppingProduct product1 :savedShoppingProducts) {
                LogUtil.d(tag,"saveProductData 当前数据 :"+ product1.toString());
                curBuyProductNum += product1.getBuyNum();
            }
            product.setBuyNum(curBuyProductNum);
            ContentValues values = new ContentValues();
            values.put("buyNum",curBuyProductNum);
            DataSupport.update(ShoppingProduct.class,values,savedShoppingProducts.get(0).getId());
            LogUtil.d(tag,"saveProductData update buyNum :"+curBuyProductNum);
        }else {
            //数据库没有当前商品，直接保存购买数量及相关信息
            product.setBuyNum(buyNum);
            if(product.save()){
                return false;
            }
            LogUtil.d(tag,"saveProductData save buyNum success");
        }
        LogUtil.d(tag,"saveProductData product info : "+product.toString());
        return true;
    }
}
