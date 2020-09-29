package com.cm.fm.mall.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cm.fm.mall.R;
import com.cm.fm.mall.adapter.ViewPagerProductAdapter;
import com.cm.fm.mall.bean.ProductMsg;
import com.cm.fm.mall.fragment.product.ProductCommentFragment;
import com.cm.fm.mall.fragment.product.ProductDetailFragment;
import com.cm.fm.mall.fragment.product.ProductInfoFragment;
import com.cm.fm.mall.util.LogUtil;
import com.cm.fm.mall.util.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/**
 * 商品（Commodity）新页面
 */
public class CommodityActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_product_back;
    TabLayout tl_product_title;
    ViewPager vp_product_content;
    Activity activity;
    ProductMsg productMsg; //当前展示的商品实体类
    ViewPagerProductAdapter productAdapter;

    String[] titles = {"商品","详情","评价"};
    private String tag = "TAG_CommodityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity);
        activity = this;
        iv_product_back = findViewById(R.id.iv_product_back);
        tl_product_title = findViewById(R.id.tl_product_title);
        vp_product_content = findViewById(R.id.vp_product_content);

        iv_product_back.setOnClickListener(this);

        productMsg = getIntent().getParcelableExtra("product");
        if(productMsg != null){
            LogUtil.d(tag,"productMsg : " + productMsg.toString());
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_product_back:
                activity.finish();
                break;
        }
    }
    /** 通过反射设置TabLayout下划线宽度
     *  从源码得知 线的宽度是根据 tabView 的宽度来设置的
     */
    public static void reflex(final TabLayout tabLayout) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    int dp10 = Utils.dip2px(tabLayout.getContext(), 10);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width-15;
                        params.leftMargin = dp10;
                        params.rightMargin = dp10;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
