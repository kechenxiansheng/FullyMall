package com.cm.fm.mall.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cm.fm.mall.R;


/**
 * 通用dialog
 * 建造者模式
 */
public class CommonDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private int mLayoutId;
    private String mContentTxt;
    private String mSureTxt;
    private String mCancelTxt;
    private String mTitleTxt;
    private TextView tv_dialog_content;
    private TextView tv_cancel_button;
    private TextView tv_sure_button;
    private TextView tv_dialog_title;
    private ChooseListener mChooseListener;
    private final String TAG = "FM_CommonDialog";

    public CommonDialog(Builder builder) {
        super(builder.mContext,builder.styleId);
        this.mContext = builder.mContext;
        this.mLayoutId = builder.layoutId;
        this.mTitleTxt = builder.titleTxt;
        this.mContentTxt = builder.contentTxt;
        this.mSureTxt = builder.sureTxt;
        this.mCancelTxt = builder.cancelTxt;
        this.mChooseListener = builder.chooseListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mLayoutId == 0){
            mLayoutId = R.layout.common_dialog;
        }
        View tip_view = View.inflate(mContext, mLayoutId,null);
        setContentView(tip_view);
        //通过 Window 设置在整个窗口显示在中间，窗口不可见时，window为null
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);  //背景透明
            //设置高宽
            WindowManager.LayoutParams params = window.getAttributes();
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }

        tv_dialog_title = tip_view.findViewById(R.id.tv_dialog_title);
        tv_dialog_content = tip_view.findViewById(R.id.tv_dialog_content);
        tv_cancel_button = tip_view.findViewById(R.id.tv_cancel_button);
        tv_sure_button = tip_view.findViewById(R.id.tv_sure_button);
        if(!TextUtils.isEmpty(mTitleTxt)){
            tv_dialog_title.setText(mTitleTxt);
        }
        if(!TextUtils.isEmpty(mSureTxt)){
            tv_sure_button.setText(mSureTxt);
        }
        if(!TextUtils.isEmpty(mCancelTxt)){
            tv_cancel_button.setText(mCancelTxt);
        }
        tv_dialog_content.setText(mContentTxt);

        //取消删除
        tv_cancel_button.setOnClickListener(this);
        //确认删除
        tv_sure_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel_button:
                Log.d(TAG,"点击了取消");
                this.dismiss();
                mChooseListener.cancel();
                break;
            case R.id.tv_sure_button:
                Log.d(TAG,"点击了继续");
                this.dismiss();
                mChooseListener.sure();
                break;
        }
    }

    public static class Builder {
        private Context mContext;
        private String contentTxt;
        private String titleTxt;
        private String sureTxt;
        private String cancelTxt;
        private ChooseListener chooseListener;
        private int styleId;
        private int layoutId;

        public Builder(Context context){
            this.mContext = context;
        }

        public Builder setLayoutId(int layoutId){
            this.layoutId = layoutId;
            return this;
        }
        public Builder setStyleId(int styleId){
            this.styleId = styleId;
            return this;
        }
        public Builder setTitleTxt(String titleTxt){
            this.titleTxt = titleTxt;
            return this;
        }
        public Builder setContentTxt(String contentTxt){
            this.contentTxt = contentTxt;
            return this;
        }
        public Builder setSureText(String sureText){
            this.sureTxt = sureText;
            return this;
        }
        public Builder setCancelText(String cancelTxt){
            this.cancelTxt = cancelTxt;
            return this;
        }
        public Builder setChooseListener(ChooseListener chooseListener){
            this.chooseListener = chooseListener;
            return this;
        }
        public CommonDialog build(){
            return new CommonDialog(this);
        }

    }

    public interface ChooseListener {
        void sure();
        void cancel();
    }
}
