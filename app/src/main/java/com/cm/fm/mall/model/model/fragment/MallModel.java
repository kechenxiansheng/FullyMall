package com.cm.fm.mall.model.model.fragment;

import com.cm.fm.mall.contract.fragment.MallContract;
import com.cm.fm.mall.model.bean.ProductMsg;
import com.cm.fm.mall.common.util.LogUtil;

import java.util.Iterator;
import java.util.List;

public class MallModel implements MallContract.Model {
    private String tag = "TAG_MallModel";

    @Override
    public List<ProductMsg> search(List<ProductMsg> productMsgs,String searchContent) {
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
        LogUtil.d(tag,"productMsgs size:"+productMsgs.size());
        LogUtil.d(tag,"productMsgs:"+productMsgs);
        return productMsgs;
    }
}
