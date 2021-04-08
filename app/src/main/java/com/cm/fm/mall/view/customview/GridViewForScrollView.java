package com.cm.fm.mall.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 网格布局
 * 重写onMeasure 方法
 */
public class GridViewForScrollView extends GridView {

    public GridViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewForScrollView(Context context) {
        super(context);
    }

    public GridViewForScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * TODO MeasureSpec 位操作工具类
     * MeasureSpec.makeMeasureSpec(size,mode) ：自定义view 中经常会使用 MeasureSpec 表示组件的大小，除了组件尺寸，还有大小模式
     * 参数：size  组件尺寸 mode  大小模式（3 种）
     * 测量模式有三种：
     *  MeasureSpec.UNSPECIFIED：未指定模式，像多大就多大，不做限制，一般用于系统内部测量
     *  MeasureSpec.AT_MOST：最大模式，对应 wrap_content 属性，子view的最终大小是父view指定的 SpecSize 值，并且子view大小不能超过这个值
     *  MeasureSpec.EXACTLY：精确模式，对应 match_parent 属性和具体的数值，父容器测量出View所需大小，也就是 SpecSize
     *
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}
