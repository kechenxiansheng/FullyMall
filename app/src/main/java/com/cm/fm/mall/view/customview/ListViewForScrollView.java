package com.cm.fm.mall.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 由于scrollview 中嵌套 listview 会导致 listview 只显示一条数据，所以修改 measure 函数以兼容两者的搭配
 * 测量模式有三种：
 *  MeasureSpec.UNSPECIFIED：未指定模式，像多大就多大，不做限制，一般用于系统内部测量
 *  MeasureSpec.AT_MOST：最大模式，对应 wrap_content 属性，子view的最终大小是父view指定的 SpecSize 值，并且子view大小不能超过这个值
 *  MeasureSpec.EXACTLY：精确模式，对应 match_parent 属性和具体的数值，父容器测量出View所需大小，也就是 SpecSize
 *
 */
public class ListViewForScrollView extends ListView {
    public ListViewForScrollView(Context context) {
        super(context);
    }
    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ListViewForScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

