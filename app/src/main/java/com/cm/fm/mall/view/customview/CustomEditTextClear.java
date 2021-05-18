package com.cm.fm.mall.view.customview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.cm.fm.mall.R;

import androidx.appcompat.widget.AppCompatEditText;


/**
 * 自定义view 之 继承控件
 * 需求：自定义一个输入框有内容时，右侧带删除图标的编辑框
 *
 * 注：由于demo不需要绘制东西，所以没有重写 onDraw() 函数
 *
 * OnFocusChangeListener 接口，用作焦点变化监听
 * TextWatcher 接口，用作文本变化监听
 *
 */
public class CustomEditTextClear extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {
    private static final String TAG = "FM_CustomEditTextTipV";
    //删除按钮的引用
    private Drawable clearDrawable;
    private boolean hasFocus;

    public CustomEditTextClear(Context context, AttributeSet attrs) {
        //使用系统的编辑框样式
        this(context,attrs, android.R.attr.editTextStyle);
    }

    public CustomEditTextClear(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        //getCompoundDrawables() : Compound-周围，函数是TextView的函数，返回的是数组：编辑框 左、上、右、下边框的 Drawable 对象
        Drawable[] compoundDrawables = getCompoundDrawables();
        clearDrawable = compoundDrawables[2];
        if(clearDrawable == null){
            //通过 getResources 加载删除图片
            clearDrawable = getResources().getDrawable(R.mipmap.delete_small);
        }

        //指定左右图标和右侧删除按钮的边框。
        //当 drawable 的 draw() 方法被调用时，它将在这里进行绘制。
        /** getIntrinsicWidth() 返回dp单位的宽，并非是drawable的px单位宽度值 */
        clearDrawable.setBounds(0,0,clearDrawable.getIntrinsicWidth(),clearDrawable.getIntrinsicHeight());

        //默认隐藏图标
        setClearIconVisible(false);

        //设置 焦点变化和文字变化监听
        setOnFocusChangeListener(this);
        addTextChangedListener(this);

    }

    /**
     * 设置删除图标的显示和隐藏
     * @param visible
     */
    private void setClearIconVisible(boolean visible){
        Drawable right = visible ? clearDrawable : null;
        //setCompoundDrawables() 对view周边进行绘制：由于我们的删除按钮在右侧，所以这里只对右侧方位的绘制进行控制
        setCompoundDrawables(getCompoundDrawables()[0],getCompoundDrawables()[1],right,getCompoundDrawables()[3]);
    }

    /**
     * 晃动效果 -- 平移动画（TranslateAnimation）实现
     * @param counts  晃动多少下
     * @return
     */
    private static Animation shakeAnimation(int counts){
        Log.d(TAG, "shakeAnimation: ");
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 5, 0, 0);
        //setInterpolator 设置动画插值器。插值器曲线类型：轨迹是正选曲线的插值器 CycleInterpolator
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        //动画时间：0.5秒
        translateAnimation.setDuration(500);
        return translateAnimation;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        Editable text = getText();
        if(hasFocus){
            //获取焦点时，如果内容为空，不显示删除按钮
            setClearIconVisible((text!=null && text.length()>0));
        }else {
            setClearIconVisible(false);
            //失去焦点时，内容为空，则抖动提示
            if(text == null || text.length() == 0){
                setHintTextColor(Color.RED);
                setAnimation(shakeAnimation(3));
            }

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //内容变化中，会调用两次此函数
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(hasFocus){
            //显示隐藏按钮
            setClearIconVisible(s.length() > 0);
            //内容为空，抖动提示
            if(s.length() == 0){
                setHint("内容为空");
                setHintTextColor(Color.GRAY);
                setAnimation(shakeAnimation(3));
            }
        }

        Log.d(TAG, "onTextChanged: " + s.toString());
    }

    /** @说明： isInnerWidth, isInnerHeight为ture，触摸点在删除图标之内，则视为点击了删除图标*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                int x = (int)event.getX();
                int y = (int)event.getY();
                Log.d(TAG, "onTouchEvent: touchEvent getX = " + x + ",getY = " + y);
                //获取右边drawable的属性(清除按钮的属性)
                Rect rect = getCompoundDrawables()[2].getBounds();
                int height = rect.height();
                int width = rect.width();
                Log.d(TAG, "onTouchEvent: deleteDrawable height = " + height + ",width = " + width);

                //getTotalPaddingRight() 获取控件右边的padding总值，包括右边的drawable在内（获取删除图标左边缘到控件右边缘的距离）
                //getPaddingRight() 获取控件右边的padding值，不包括右边的drawable（获取删除图标右边缘到控件右边缘的距离）
                Log.d(TAG, "onTouchEvent: width = " + getWidth());
                int minX = getWidth() - getTotalPaddingRight();
                int maxX = getWidth() - getPaddingRight();
                boolean isInnerWidth = x > minX && x < maxX;
                Log.d(TAG, "onTouchEvent: deleteDrawable minX = " + minX + ",maxX = " + maxX);
                //计算删除按钮的Y值，不能用这种方式！！！无效！
//                int minY = getHeight() - getTotalPaddingBottom();
//                int maxY = getHeight() - getPaddingBottom();
//                Log.d(TAG, "onTouchEvent: minY = " + minY + ",maxY = " + maxY);
//                boolean isInnerHeight = y > minY && y < maxY;
                //计算删除图标顶部边缘到控件顶部边缘的距离
                int minY = (getHeight() - height)/2;
                int maxY = minY + height;
                Log.d(TAG, "onTouchEvent: deleteDrawable minY = " + minY + ",maxY = " + maxY);
                boolean isInnerHeight = y > minY && y < maxY;
                Log.d(TAG, "onTouchEvent: isInnerWidth = " + isInnerWidth + ",isInnerHeight = " + isInnerHeight);
                if (isInnerWidth && isInnerHeight) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 供外部调用的方法
     * 晃动进行提示
     */
    public void shakeTip(int counts){
        //失去焦点
//        if(!this.hasFocus()){
//            setHint("账号不能为空");
//            setHintTextColor(Color.RED);
//            this.setAnimation(shakeAnimation(counts));
//        }
        Log.d(TAG, "shakeTip: ");

        this.setAnimation(shakeAnimation(counts));

    }


}
