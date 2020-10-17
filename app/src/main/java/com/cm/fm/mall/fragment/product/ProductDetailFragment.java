package com.cm.fm.mall.fragment.product;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cm.fm.mall.R;
import com.cm.fm.mall.adapter.DetailPictureAdapter;
import com.cm.fm.mall.fragment.BaseFragment;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.MallConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品的详情页
 * 仿京东，直接用 listview 展示图片（图片内容是商品的各种大图，介绍等信息）,listview 下方是 表格形式（直接用）的规格参数，规格下是售后服务
 */
public class ProductDetailFragment extends BaseFragment {
    Activity activity;
    ListView lv_detail_picture;
    DetailPictureAdapter adapter;
    List<String> list = new ArrayList<>();
    private String tag = "TAG_ProductDetailFragment";

    @Override
    public int getResource() {
        return R.layout.fragment_product_detail;
    }

    @Override
    public void init(View view) {
        activity = getActivity();
        lv_detail_picture = view.findViewById(R.id.lv_detail_picture);
        adapter = new DetailPictureAdapter(activity,list);
        lv_detail_picture.setAdapter(adapter);
    }

    @Override
    public void loadingData() {
        for (int i = 1 ; i < 7 ;i++){
            list.add(MallConstant.detail_picture_url + "P_ProductDetail" + i + MallConstant.detail_picture_url_end);
        }
        LogUtil.d(tag,"list data 1 : "+list.get(0));
    }

    @Override
    public void dataDestroy() {

    }
}
