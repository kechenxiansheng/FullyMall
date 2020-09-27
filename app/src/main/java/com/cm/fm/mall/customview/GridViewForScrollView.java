package com.cm.fm.mall.customview;

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
     * @param size  组件尺寸
     * @param mode  大小模式（3 种）
     * 精确模式（MeasureSpec.EXACTLY）：尺寸的值是多少，那么这个组件的长或宽就是多少
     * 最大模式（MeasureSpec.AT_MOST）：这个也就是父组件，能够给出的最大的空间，当前组件的长或宽最大只能为这么大，当然也可以比这个小。
     * 未指定模式（MeasureSpec.UNSPECIFIED）：这个就是说，当前组件，可以随便用空间，不受限制。
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}
