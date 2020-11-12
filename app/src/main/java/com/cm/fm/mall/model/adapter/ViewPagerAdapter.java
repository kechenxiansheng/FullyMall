package com.cm.fm.mall.model.adapter;


import android.view.View;
import android.view.ViewGroup;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * 商品页，图片适配器
 */
public class ViewPagerAdapter extends PagerAdapter {
    private List<View> views;
    private String tag = "TAG_ViewPagerAdapter";
    public ViewPagerAdapter(List<View> views) {
        this.views = views;

    }

    @Override
    public int getCount() {
        return views.size();
    }

    @NonNull
    @Override
    // instantiate　实例化。功能是往PageView里添加自己需要的page。返回值object，就是 isViewFromObject 的id。
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        LogUtil.d(tag," addView position: "+ position);
        //将每个页卡view都加进容器中
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    // 确定这个view的id是不是这个object
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public int getItemPosition(@NonNull Object object) {

        return super.getItemPosition(object);
    }
}
