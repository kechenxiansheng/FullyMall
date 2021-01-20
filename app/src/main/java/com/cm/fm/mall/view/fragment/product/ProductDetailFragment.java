package com.cm.fm.mall.view.fragment.product;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BasePresenter;
import com.cm.fm.mall.model.adapter.DetailPictureAdapter;
import com.cm.fm.mall.base.BaseMVPFragment;
import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.common.MallConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品的详情页
 * 仿京东，直接用 listview 展示图片（图片内容是商品的各种大图，介绍等信息）,listview 下方是 表格形式（直接用）的规格参数，规格下是售后服务
 */
public class ProductDetailFragment extends BaseMVPFragment {
    Activity activity;
    ListView lv_detail_picture;
    DetailPictureAdapter adapter;
    List<String> list = new ArrayList<>();
    private String tag = "TAG_ProductDetailFragment";

    @Override
    public int initLayout() {
        return R.layout.fragment_product_detail;
    }

    @Override
    public void initView(View view) {
        activity = getActivity();
        lv_detail_picture = view.findViewById(R.id.lv_detail_picture);
        adapter = new DetailPictureAdapter(activity,list);
        lv_detail_picture.setAdapter(adapter);
    }
    @Override
    protected BasePresenter createPresenter() {
        return null;
    }
    @Override
    public void initDataFront() {
        for (int i = 1 ; i < 7 ;i++){
            list.add(MallConstant.DETAIL_PICTURE_URL + "P_ProductDetail" + i + MallConstant.DETAIL_PICTURE_URL_END);
        }
        LogUtil.d(tag,"list data 1 : "+list.get(0));
    }

    @Override
    public void dataDestroy() {

    }
}
