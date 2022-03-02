package com.cm.fm.mall.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.view.View;

import com.cm.fm.mall.common.util.LogUtil;
import com.cm.fm.mall.model.bean.ProductMsg;
import com.cm.fm.mall.view.fragment.product.ProductCommentFragment;
import com.cm.fm.mall.view.fragment.product.ProductDetailFragment;
import com.cm.fm.mall.view.fragment.product.ProductInfoFragment;

/**
 * 商品页 viewpager 适配器
 */
public class ViewPagerProductAdapter extends FragmentStatePagerAdapter {
    Activity context;
    String[] titles;
    ProductMsg productMsg;
    FragmentManager manager;
    ProductInfoFragment infoFragment;
    ProductDetailFragment detailFragment;
    ProductCommentFragment commentFragment;
    private String tag = "TAG_VPProductAdapter";

    public ViewPagerProductAdapter(FragmentManager manager,Activity context, String[] titles) {
        super(manager);
        this.manager = manager;
        this.context = context;
        this.titles = titles;
    }

    public ViewPagerProductAdapter(FragmentManager fm, Activity context, String[] titles, ProductMsg productMsg) {
        super(fm);
        this.context = context;
        this.titles = titles;
        this.productMsg = productMsg;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                LogUtil.d(tag,"show ProductInfoFragment");
                if(infoFragment == null){
                    infoFragment = new ProductInfoFragment();
                }
                //通过bundle将数据传给fragment
                Bundle bundle = new Bundle();
                bundle.putParcelable("productMsg",productMsg);
                infoFragment.setArguments(bundle);
                return infoFragment;
            case 1:
                LogUtil.d(tag,"show ProductDetailFragment");
                if(detailFragment == null){
                    detailFragment = new ProductDetailFragment();
                }
                return detailFragment;
            case 2:
                LogUtil.d(tag,"show ProductCommentFragment");
                if(commentFragment == null){
                    commentFragment = new ProductCommentFragment();
                }
                return commentFragment;
            default:
                LogUtil.d(tag,"show default");
                if(infoFragment == null){
                    infoFragment = new ProductInfoFragment();
                }
                return infoFragment;
        }

    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    //TabLayout 搭配 viewpager 需要重写，设置标题
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //返回当前页签的标题
        return titles[position];
    }
}
