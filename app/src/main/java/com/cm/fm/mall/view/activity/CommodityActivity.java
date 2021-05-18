package com.cm.fm.mall.view.activity;

import android.app.Activity;

import android.view.View;
import android.widget.ImageView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseMVPActivity;
import com.cm.fm.mall.common.util.Utils;
import com.cm.fm.mall.contract.activity.CommodityContract;
import com.cm.fm.mall.model.adapter.ViewPagerProductAdapter;
import com.cm.fm.mall.model.bean.ProductMsg;
import com.cm.fm.mall.presenter.activity.CommodityPresenter;
import com.cm.fm.mall.common.util.LogUtil;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;

/**
 * 新的 商品页
 */
public class CommodityActivity extends BaseMVPActivity<CommodityPresenter> implements CommodityContract.View,View.OnClickListener {
    ImageView iv_product_back;
    TabLayout tl_product_title;
    ViewPager vp_product_content;
    Activity activity;
    ProductMsg productMsg; //当前展示的商品实体类
    ViewPagerProductAdapter productAdapter;

    String[] titles = {"商品","详情","评价"};
    private final String TAG = "FM_CommodityActivity";


    @Override
    protected int initLayout() {
        activity = this;
        return R.layout.activity_commodity;
    }

    @Override
    protected void initView() {
        iv_product_back = findViewById(R.id.iv_product_back);
        tl_product_title = findViewById(R.id.tl_product_title);
        vp_product_content = findViewById(R.id.vp_product_content);

        iv_product_back.setOnClickListener(this);

        productMsg = getIntent().getParcelableExtra("product");
        if(productMsg != null){
            LogUtil.d(TAG,"productMsg : " + productMsg.toString());
            //适配器
            productAdapter = new ViewPagerProductAdapter(getSupportFragmentManager(),activity,titles,productMsg);
            vp_product_content.setAdapter(productAdapter);
        }
        //TabLayout 关联 viewpager
        tl_product_title.setupWithViewPager(vp_product_content);
        //设置模式：固定
        tl_product_title.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    public void finish() {
        super.finish();
        Utils.actUseAnim(this,R.transition.explode,R.transition.fade);
    }

    @Override
    protected CommodityPresenter createPresenter() {
        return new CommodityPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_product_back:
                activity.finish();
                break;
        }
    }

}
