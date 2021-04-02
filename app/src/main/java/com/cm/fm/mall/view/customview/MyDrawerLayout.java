package com.cm.fm.mall.view.customview;

import android.content.Context;

import android.util.AttributeSet;

import androidx.drawerlayout.widget.DrawerLayout;

/**
 * 自定义抽屉控件
 * 重写 onMeasure 方法防止 DrawerLayout 作为非布局文件的绝对父布局时，宽高设置为match_parent，wrap_content 报错的问题
 * （ DrawerLayout must be measured with MeasureSpec.EXACTLY ）
 * 测量模式有三种：
 *  MeasureSpec.UNSPECIFIED：未指定模式，像多大就多大，不做限制，一般用于系统内部测量
 *  MeasureSpec.AT_MOST：最大模式，对应 wrap_content 属性，子view的最终大小是父view指定的 SpecSize 值，并且子view大小不能超过这个值
 *  MeasureSpec.EXACTLY：精确模式，对应 match_parent 属性和具体的数值，父容器测量出View所需大小，也就是 SpecSize
 *
 */
public class MyDrawerLayout extends DrawerLayout {
    public MyDrawerLayout(Context context) {
        super(context);
    }

    public MyDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //DrawerLayout 的测量模式必须设置为精确模式（MeasureSpec.EXACTLY）
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}