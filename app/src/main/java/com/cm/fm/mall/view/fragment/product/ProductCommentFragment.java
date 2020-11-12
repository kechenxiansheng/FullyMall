package com.cm.fm.mall.view.fragment.product;

import android.view.View;

import com.cm.fm.mall.R;
import com.cm.fm.mall.base.BaseMVPFragment;
import com.cm.fm.mall.base.BasePresenter;

/**
 * 商品的评论页
 */
public class ProductCommentFragment extends BaseMVPFragment {
    @Override
    public int initLayout() {
        return R.layout.fragment_product_comment;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }
    @Override
    public void dataDestroy() {

    }
}
