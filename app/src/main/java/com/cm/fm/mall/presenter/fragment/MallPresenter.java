package com.cm.fm.mall.presenter.fragment;

import android.util.Log;

import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.common.MallConstant;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.contract.fragment.MallContract;
import com.cm.fm.mall.model.bean.ProductMsg;
import com.cm.fm.mall.model.model.fragment.MallModel;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MallPresenter extends BasePresenter<MallContract.Model,MallContract.View> implements MallContract.Presenter {
    private final String TAG = "FM_MallPresenter";

    @Override
    protected MallContract.Model createModule() {
        return new MallModel();
    }

    @Override
    public void init() {

    }

    @Override
    public void search(List<ProductMsg> productMsgs,String searchContent) {
        if(isViewBind()){
            searchContent = searchContent.trim().toLowerCase();
            Iterator<ProductMsg> iterator = productMsgs.iterator();
            while (iterator.hasNext()){
                ProductMsg productMsg =  iterator.next();
                //TODO 适配器只监听原有的数据list，所以此处筛选，商品类型、商品名、商品描述中都不包含用户搜索的内容 的商品进行删除
                if(!productMsg.getType().toLowerCase().trim().contains(searchContent) && !productMsg.getProductName().toLowerCase().trim().contains(searchContent)
                        && !productMsg.getProductDescription().toLowerCase().trim().contains(searchContent)){
                    iterator.remove();
                }
            }
            LogUtil.d(TAG,"productMsgs size:"+productMsgs.size());
            LogUtil.d(TAG,"productMsgs:"+productMsgs);

            getView().OnSearchResult(MallConstant.SUCCESS, productMsgs);
        }else {
            getView().OnSearchResult(MallConstant.FAIL, null);
        }
    }
}
